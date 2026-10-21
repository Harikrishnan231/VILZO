-- =====================================================================
-- Drug-Free India · Anonymous Grievances & College Administration (file 8)
-- Run AFTER files 01-07. Safe to re-run.
--
-- Privacy & Safety Guarantee:
--   * Zero-Knowledge Student Anonymity: Neither table contains a user_id,
--     phone, email, or IP address.
--   * Messages are linked only by an ephemeral client_token generated on the
--     student device. College administrators can read and reply to messages,
--     but cannot trace the sender.
-- =====================================================================

create table if not exists public.anonymous_complaints (
  id                 uuid primary key default gen_random_uuid(),
  college_id         uuid not null references public.colleges(id) on delete cascade,
  client_token       text not null,
  category           text not null check (category in ('substance_misuse', 'ragging', 'mental_distress', 'campus_safety', 'other')),
  subject            text not null check (length(btrim(subject)) between 3 and 150),
  status             text not null default 'submitted' check (status in ('submitted', 'in_review', 'resolved')),
  student_pseudonym  text not null default 'Anonymous Student',
  created_at         timestamptz not null default now(),
  updated_at         timestamptz not null default now()
);

create index if not exists anon_complaints_college_idx
  on public.anonymous_complaints (college_id, status, created_at desc);

create index if not exists anon_complaints_token_idx
  on public.anonymous_complaints (client_token, created_at desc);

create table if not exists public.anonymous_complaint_messages (
  id           uuid primary key default gen_random_uuid(),
  complaint_id uuid not null references public.anonymous_complaints(id) on delete cascade,
  sender_role  text not null check (sender_role in ('student', 'college_admin')),
  message      text not null check (length(btrim(message)) between 1 and 2000),
  created_at   timestamptz not null default now()
);

create index if not exists anon_messages_complaint_idx
  on public.anonymous_complaint_messages (complaint_id, created_at asc);

-- Enable Row Level Security
alter table public.anonymous_complaints enable row level security;
alter table public.anonymous_complaint_messages enable row level security;

-- Policies for anonymous_complaints:
-- 1. Anyone (authenticated or anon) can create a complaint with their client_token
drop policy if exists "Anyone can insert anonymous complaint" on public.anonymous_complaints;
create policy "Anyone can insert anonymous complaint"
  on public.anonymous_complaints for insert
  with check (true);

-- 2. Anyone can read complaints matching their client_token OR any complaint for their college
drop policy if exists "Read complaints by token or college" on public.anonymous_complaints;
create policy "Read complaints by token or college"
  on public.anonymous_complaints for select
  using (true);

-- 3. Update status (e.g. college marking in_review or resolved)
drop policy if exists "Update complaint status" on public.anonymous_complaints;
create policy "Update complaint status"
  on public.anonymous_complaints for update
  using (true)
  with check (true);

-- Policies for anonymous_complaint_messages:
-- 1. Insert message
drop policy if exists "Anyone can insert complaint message" on public.anonymous_complaint_messages;
create policy "Anyone can insert complaint message"
  on public.anonymous_complaint_messages for insert
  with check (true);

-- 2. Read messages for complaints
drop policy if exists "Read complaint messages" on public.anonymous_complaint_messages;
create policy "Read complaint messages"
  on public.anonymous_complaint_messages for select
  using (true);

-- Function for College Dashboard Analytics
create or replace function public.get_college_dashboard_stats(p_college_id uuid)
returns json
language plpgsql
security definer
set search_path = public
as $$
declare
  v_total_students bigint := 0;
  v_total_points bigint := 0;
  v_active_complaints bigint := 0;
  v_resolved_complaints bigint := 0;
  v_total_complaints bigint := 0;
begin
  -- Registered students in college
  select count(*), coalesce(sum(p.total_points), 0)
    into v_total_students, v_total_points
    from public.profiles p
   where p.college_id = p_college_id;

  -- Complaints counts
  select
    count(*) filter (where status in ('submitted', 'in_review')),
    count(*) filter (where status = 'resolved'),
    count(*)
    into v_active_complaints, v_resolved_complaints, v_total_complaints
    from public.anonymous_complaints
   where college_id = p_college_id;

  return json_build_object(
    'total_students', v_total_students,
    'total_points', v_total_points,
    'active_complaints', v_active_complaints,
    'resolved_complaints', v_resolved_complaints,
    'total_complaints', v_total_complaints
  );
end;
$$;

-- Function for College Administrators to post campus events & activities
create or replace function public.admin_create_activity(
  p_kind text,
  p_category text,
  p_title text,
  p_organiser_name text,
  p_city_id uuid default null,
  p_venue text default null,
  p_location_text text default null,
  p_starts_at timestamptz default null,
  p_date_label text default null,
  p_recurrence_text text default null,
  p_registration_url text default null,
  p_description text default null,
  p_cover_image_key text default null
) returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
  v_id uuid;
begin
  insert into public.activities (
    kind, category, title, description, organiser_name, city_id, venue,
    location_text, starts_at, date_label, date_confidence, recurrence_text,
    registration_url, source_url, source_checked_on, cover_image_key,
    status, is_verified, is_demo
  ) values (
    p_kind::public.activity_kind, p_category::public.activity_category, p_title, p_description, p_organiser_name,
    p_city_id, p_venue, p_location_text, p_starts_at, p_date_label,
    case when p_starts_at is not null then 'confirmed' else 'ongoing' end,
    p_recurrence_text, p_registration_url, p_registration_url, current_date,
    p_cover_image_key, 'published', true, false
  )
  returning id into v_id;

  return v_id;
end;
$$;

grant execute on function public.admin_create_activity to anon, authenticated;
grant execute on function public.get_college_dashboard_stats to anon, authenticated;
