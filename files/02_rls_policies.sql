-- =====================================================================
-- Drug-Free India · Row Level Security          (file 2 of 5)
--
-- The app ships with a *publishable* key, which is public by design.
-- RLS is what keeps data safe. Every table below has RLS ON.
-- Never put the secret / service_role key in the Android app.
--
-- Rule of thumb used here:
--   * public help content  → readable by anyone (even before sign-in)
--   * personal data        → only the owner can read/write
--   * game scores, surveys → written ONLY through the RPC functions in file 03
--   * moderation (approve colleges / publish activities / verify helplines)
--     is done by the team in the Supabase dashboard (service role bypasses RLS)
-- =====================================================================

-- ---------- enable RLS everywhere ----------
alter table public.cities               enable row level security;
alter table public.colleges             enable row level security;
alter table public.college_suggestions  enable row level security;
alter table public.profiles             enable row level security;
alter table public.helplines            enable row level security;
alter table public.support_centres      enable row level security;
alter table public.activities           enable row level security;
alter table public.activity_signups     enable row level security;
alter table public.mentors              enable row level security;
alter table public.mentor_requests      enable row level security;
alter table public.stories              enable row level security;
alter table public.story_attempts       enable row level security;
alter table public.choice_events        enable row level security;
alter table public.badges               enable row level security;
alter table public.user_badges          enable row level security;
alter table public.surveys              enable row level security;
alter table public.survey_submissions   enable row level security;
alter table public.survey_responses     enable row level security;
alter table public.self_assessments     enable row level security;
alter table public.app_config           enable row level security;

-- ---------- table privileges (defence in depth) ----------
-- Anonymous visitors can only READ public help/reference content.
revoke all on
  public.cities, public.colleges, public.college_suggestions, public.profiles,
  public.helplines, public.support_centres, public.activities, public.activity_signups,
  public.mentors, public.mentor_requests, public.stories, public.story_attempts,
  public.choice_events, public.badges, public.user_badges, public.surveys,
  public.survey_submissions, public.survey_responses, public.self_assessments,
  public.app_config
from anon;

grant select on
  public.cities, public.colleges, public.helplines, public.support_centres,
  public.activities, public.stories, public.badges, public.app_config
to anon;

-- Signed-in users can never write scores, badges or survey data directly.
revoke insert, update, delete on
  public.story_attempts, public.choice_events, public.user_badges,
  public.survey_submissions, public.survey_responses, public.surveys,
  public.stories, public.badges, public.app_config, public.cities, public.colleges,
  public.helplines, public.support_centres, public.mentors
from authenticated;

-- ---------- reference data (public read) ----------
drop policy if exists "cities readable" on public.cities;
create policy "cities readable" on public.cities
  for select to anon, authenticated using (is_active);

drop policy if exists "colleges readable" on public.colleges;
create policy "colleges readable" on public.colleges
  for select to anon, authenticated using (is_active);

drop policy if exists "helplines readable" on public.helplines;
create policy "helplines readable" on public.helplines
  for select to anon, authenticated using (is_active);

drop policy if exists "support centres readable" on public.support_centres;
create policy "support centres readable" on public.support_centres
  for select to anon, authenticated using (is_active);

drop policy if exists "stories readable" on public.stories;
create policy "stories readable" on public.stories
  for select to anon, authenticated using (is_published);

drop policy if exists "badges readable" on public.badges;
create policy "badges readable" on public.badges
  for select to anon, authenticated using (is_active);

drop policy if exists "config readable" on public.app_config;
create policy "config readable" on public.app_config
  for select to anon, authenticated using (true);

drop policy if exists "surveys readable" on public.surveys;
create policy "surveys readable" on public.surveys
  for select to authenticated using (is_active);

drop policy if exists "mentors readable" on public.mentors;
create policy "mentors readable" on public.mentors
  for select to authenticated using (is_active and is_verified);

-- ---------- profiles (own row only) ----------
drop policy if exists "profile select own" on public.profiles;
create policy "profile select own" on public.profiles
  for select to authenticated using (id = (select auth.uid()));

drop policy if exists "profile insert own" on public.profiles;
create policy "profile insert own" on public.profiles
  for insert to authenticated with check (id = (select auth.uid()));

drop policy if exists "profile update own" on public.profiles;
create policy "profile update own" on public.profiles
  for update to authenticated
  using (id = (select auth.uid()))
  with check (id = (select auth.uid()));
-- No delete policy: use public.delete_my_account().

-- ---------- college suggestions (own, pending only) ----------
drop policy if exists "suggestion insert own" on public.college_suggestions;
create policy "suggestion insert own" on public.college_suggestions
  for insert to authenticated
  with check (user_id = (select auth.uid()) and status = 'pending');

drop policy if exists "suggestion select own" on public.college_suggestions;
create policy "suggestion select own" on public.college_suggestions
  for select to authenticated using (user_id = (select auth.uid()));

-- ---------- activities ----------
-- Anyone sees published listings. Creators also see their own pending ones.
drop policy if exists "activities read published" on public.activities;
create policy "activities read published" on public.activities
  for select to anon, authenticated
  using (status = 'published' or created_by = (select auth.uid()));

-- A user can submit a listing, but only as pending / unverified / non-demo.
drop policy if exists "activities submit" on public.activities;
create policy "activities submit" on public.activities
  for insert to authenticated
  with check (
    created_by = (select auth.uid())
    and status = 'pending'
    and is_verified = false
    and is_demo = false
  );

drop policy if exists "activities edit own pending" on public.activities;
create policy "activities edit own pending" on public.activities
  for update to authenticated
  using (created_by = (select auth.uid()) and status = 'pending')
  with check (
    created_by = (select auth.uid())
    and status = 'pending' and is_verified = false and is_demo = false
  );

drop policy if exists "activities delete own pending" on public.activities;
create policy "activities delete own pending" on public.activities
  for delete to authenticated
  using (created_by = (select auth.uid()) and status = 'pending');

-- ---------- activity signups (own only, published activities only) ----------
drop policy if exists "signups select own" on public.activity_signups;
create policy "signups select own" on public.activity_signups
  for select to authenticated using (user_id = (select auth.uid()));

drop policy if exists "signups insert own" on public.activity_signups;
create policy "signups insert own" on public.activity_signups
  for insert to authenticated
  with check (
    user_id = (select auth.uid())
    and exists (select 1 from public.activities a where a.id = activity_id and a.status = 'published')
  );

drop policy if exists "signups delete own" on public.activity_signups;
create policy "signups delete own" on public.activity_signups
  for delete to authenticated using (user_id = (select auth.uid()));

-- ---------- mentor requests (own only) ----------
drop policy if exists "mentor requests select own" on public.mentor_requests;
create policy "mentor requests select own" on public.mentor_requests
  for select to authenticated using (user_id = (select auth.uid()));

drop policy if exists "mentor requests insert own" on public.mentor_requests;
create policy "mentor requests insert own" on public.mentor_requests
  for insert to authenticated
  with check (
    user_id = (select auth.uid())
    and exists (select 1 from public.mentors m where m.id = mentor_id and m.is_active and m.is_verified)
  );

-- ---------- game data: read own, write only via RPC ----------
drop policy if exists "attempts select own" on public.story_attempts;
create policy "attempts select own" on public.story_attempts
  for select to authenticated using (user_id = (select auth.uid()));

drop policy if exists "choices select own" on public.choice_events;
create policy "choices select own" on public.choice_events
  for select to authenticated using (user_id = (select auth.uid()));

drop policy if exists "user badges select own" on public.user_badges;
create policy "user badges select own" on public.user_badges
  for select to authenticated using (user_id = (select auth.uid()));

-- ---------- surveys ----------
drop policy if exists "submissions select own" on public.survey_submissions;
create policy "submissions select own" on public.survey_submissions
  for select to authenticated using (user_id = (select auth.uid()));
-- survey_responses: NO policies on purpose. Only public.submit_survey() writes,
-- only public.community_impact() reads (aggregates above the minimum cohort).

-- ---------- self assessments (private) ----------
drop policy if exists "assessments select own" on public.self_assessments;
create policy "assessments select own" on public.self_assessments
  for select to authenticated using (user_id = (select auth.uid()));

drop policy if exists "assessments insert own" on public.self_assessments;
create policy "assessments insert own" on public.self_assessments
  for insert to authenticated with check (user_id = (select auth.uid()));

drop policy if exists "assessments delete own" on public.self_assessments;
create policy "assessments delete own" on public.self_assessments
  for delete to authenticated using (user_id = (select auth.uid()));

-- ---------- storage: public bucket for activity cover images ----------
-- Uploads are done by the team (dashboard / service role). The app only reads.
insert into storage.buckets (id, name, public)
values ('activity-images', 'activity-images', true)
on conflict (id) do nothing;

drop policy if exists "activity images public read" on storage.objects;
create policy "activity images public read" on storage.objects
  for select to anon, authenticated using (bucket_id = 'activity-images');
