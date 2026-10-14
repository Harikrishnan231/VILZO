-- =====================================================================
-- Drug-Free India · Supabase schema            (file 1 of 5)
-- Project: https://isrxwxwhkfdiyjofsmrt.supabase.co
--
-- RUN ORDER (Supabase Dashboard → SQL Editor, one file at a time):
--   01_schema.sql → 02_rls_policies.sql → 03_functions.sql → 04_seed.sql
--   05_dev_demo_seed.sql is OPTIONAL and for development/demo only.
--
-- SAFE TO RE-RUN: only CREATE ... IF NOT EXISTS / CREATE OR REPLACE / ADD COLUMN IF NOT EXISTS.
-- It never drops tables or deletes data.
--
-- BEFORE YOU RUN IT: open Table Editor and check whether tables named
-- profiles, cities, colleges, activities, stories ... already exist with a different shape.
-- If they do, stop and adapt this file instead of overwriting your existing design.
--
-- PRIVACY DESIGN (read this once):
--   * Trusted contacts are NOT stored here. They stay encrypted on the user's device.
--   * Survey answers are stored WITHOUT user id and WITHOUT timestamps (only month).
--     "Who already answered" lives in a separate table that holds no answers.
--   * Colleges only show in leaderboards as aggregates above a minimum cohort size.
--   * Pending college suggestions live in their own table, never in `colleges`.
-- =====================================================================

create extension if not exists pg_trgm with schema extensions;

-- ---------------------------------------------------------------------
-- Enums
-- ---------------------------------------------------------------------
do $$ begin
  create type public.support_centre_type as enum ('government','counselling','de_addiction','hospital','ngo');
exception when duplicate_object then null; end $$;

do $$ begin
  create type public.activity_kind as enum ('activity','event','club','volunteering');
exception when duplicate_object then null; end $$;

do $$ begin
  create type public.activity_category as enum ('sports','art','music','dance','poetry','volunteering','fitness','skills','other');
exception when duplicate_object then null; end $$;

do $$ begin
  create type public.listing_status as enum ('pending','published','rejected');
exception when duplicate_object then null; end $$;

do $$ begin
  create type public.suggestion_status as enum ('pending','approved','rejected','duplicate');
exception when duplicate_object then null; end $$;

-- ---------------------------------------------------------------------
-- Generic helpers
-- ---------------------------------------------------------------------
create or replace function public.set_updated_at()
returns trigger
language plpgsql
set search_path = ''
as $$
begin
  new.updated_at := now();
  return new;
end;
$$;

-- ---------------------------------------------------------------------
-- Reference data: cities, colleges
-- ---------------------------------------------------------------------
create table if not exists public.cities (
  id          uuid primary key default gen_random_uuid(),
  name        text not null,
  state       text,
  slug        text not null unique,
  is_active   boolean not null default true,
  sort_order  int not null default 100,
  created_at  timestamptz not null default now()
);

-- Approved institutions only. Suggestions go to college_suggestions.
create table if not exists public.colleges (
  id           uuid primary key default gen_random_uuid(),
  city_id      uuid not null references public.cities(id) on delete restrict,
  name         text not null check (length(btrim(name)) between 3 and 150),
  short_code   text,
  is_verified  boolean not null default false,   -- show a verified tick ONLY when true
  verified_at  timestamptz,
  source_url   text,
  is_active    boolean not null default true,
  created_at   timestamptz not null default now()
);
create unique index if not exists colleges_city_name_uq
  on public.colleges (city_id, lower(btrim(name)));
create index if not exists colleges_name_trgm
  on public.colleges using gin (name extensions.gin_trgm_ops);

create table if not exists public.college_suggestions (
  id           uuid primary key default gen_random_uuid(),
  user_id      uuid not null references auth.users(id) on delete cascade,
  city_id      uuid not null references public.cities(id) on delete restrict,
  name         text not null check (length(btrim(name)) between 3 and 150),
  note         text check (note is null or length(note) <= 500),
  status       public.suggestion_status not null default 'pending',
  created_at   timestamptz not null default now(),
  reviewed_at  timestamptz
);
create unique index if not exists college_suggestions_pending_uq
  on public.college_suggestions (city_id, lower(btrim(name))) where status = 'pending';
create index if not exists college_suggestions_user_idx
  on public.college_suggestions (user_id, status);

-- ---------------------------------------------------------------------
-- Profiles (one row per auth user)
-- ---------------------------------------------------------------------
create table if not exists public.profiles (
  id                        uuid primary key references auth.users(id) on delete cascade,
  full_name                 text,
  phone                     text,
  city_id                   uuid references public.cities(id) on delete set null,
  college_id                uuid references public.colleges(id) on delete set null,
  custom_college_name       text,
  language                  text not null default 'en',
  include_in_college_stats  boolean not null default true,
  onboarding_completed      boolean not null default false,
  created_at                timestamptz not null default now(),
  updated_at                timestamptz not null default now()
);
-- If a profiles table already existed, make sure our columns are there.
alter table public.profiles add column if not exists full_name text;
alter table public.profiles add column if not exists phone text;
alter table public.profiles add column if not exists city_id uuid references public.cities(id) on delete set null;
alter table public.profiles add column if not exists college_id uuid references public.colleges(id) on delete set null;
alter table public.profiles add column if not exists custom_college_name text;
alter table public.profiles add column if not exists language text not null default 'en';
alter table public.profiles add column if not exists include_in_college_stats boolean not null default true;
alter table public.profiles add column if not exists onboarding_completed boolean not null default false;
alter table public.profiles add column if not exists created_at timestamptz not null default now();
alter table public.profiles add column if not exists updated_at timestamptz not null default now();

do $$ begin
  alter table public.profiles add constraint profiles_full_name_len
    check (full_name is null or length(btrim(full_name)) between 1 and 80);
exception when duplicate_object then null; end $$;
do $$ begin
  -- E.164 format, entered by the user. "Verified" status is NEVER stored here:
  -- it comes from auth.users.phone_confirmed_at via public.is_phone_verified().
  alter table public.profiles add constraint profiles_phone_e164
    check (phone is null or phone ~ '^\+[1-9][0-9]{7,14}$');
exception when duplicate_object then null; end $$;

drop trigger if exists dfi_profiles_updated_at on public.profiles;
create trigger dfi_profiles_updated_at
  before update on public.profiles
  for each row execute function public.set_updated_at();

create or replace function public.dfi_handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  insert into public.profiles (id, full_name)
  values (
    new.id,
    nullif(btrim(coalesce(new.raw_user_meta_data->>'full_name', new.raw_user_meta_data->>'name', '')), '')
  )
  on conflict (id) do nothing;
  return new;
end;
$$;

drop trigger if exists dfi_on_auth_user_created on auth.users;
create trigger dfi_on_auth_user_created
  after insert on auth.users
  for each row execute function public.dfi_handle_new_user();

-- ---------------------------------------------------------------------
-- Help content (readable without an account)
-- ---------------------------------------------------------------------
create table if not exists public.helplines (
  id               uuid primary key default gen_random_uuid(),
  name             text not null unique,
  number           text not null,
  description      text,
  hours            text,
  languages        text[],
  source_url       text,
  is_emergency     boolean not null default false,
  is_verified      boolean not null default false,   -- set true ONLY after checking the official source
  last_verified_at timestamptz,
  is_active        boolean not null default true,
  sort_order       int not null default 100
);

create table if not exists public.support_centres (
  id           uuid primary key default gen_random_uuid(),
  city_id      uuid references public.cities(id) on delete set null,
  name         text not null check (length(btrim(name)) between 3 and 200),
  type         public.support_centre_type not null,
  address      text,
  phone        text,
  website      text check (website is null or website ~* '^https?://'),
  hours        text,
  lat          double precision check (lat is null or lat between -90 and 90),
  lng          double precision check (lng is null or lng between -180 and 180),
  source_url   text,
  is_verified  boolean not null default false,
  verified_at  timestamptz,
  is_active    boolean not null default true,
  created_at   timestamptz not null default now()
);
create unique index if not exists support_centres_city_name_uq
  on public.support_centres (city_id, lower(btrim(name)));
create index if not exists support_centres_city_idx on public.support_centres (city_id, type);

-- ---------------------------------------------------------------------
-- Explore: activities / events / clubs / volunteering, mentors
-- ---------------------------------------------------------------------
create table if not exists public.activities (
  id               uuid primary key default gen_random_uuid(),
  kind             public.activity_kind not null default 'activity',
  category         public.activity_category not null default 'other',
  title            text not null check (length(btrim(title)) between 3 and 120),
  description      text check (description is null or length(description) <= 2000),
  organiser_name   text not null check (length(btrim(organiser_name)) between 2 and 120),
  city_id          uuid references public.cities(id) on delete set null,
  college_id       uuid references public.colleges(id) on delete set null,
  venue            text,
  starts_at        timestamptz,
  ends_at          timestamptz,
  recurrence_text  text,                      -- e.g. 'Every Saturday · 5:00 PM'
  eligibility      text,
  registration_url text check (registration_url is null or registration_url ~* '^https?://'),
  contact          text,
  cover_image_path text,                      -- path in the 'activity-images' storage bucket
  status           public.listing_status not null default 'pending',
  is_verified      boolean not null default false,
  is_demo          boolean not null default false,   -- app must show a "Demo" tag when true
  created_by       uuid references auth.users(id) on delete set null,
  created_at       timestamptz not null default now(),
  updated_at       timestamptz not null default now(),
  check (ends_at is null or starts_at is null or ends_at >= starts_at)
);
create index if not exists activities_feed_idx on public.activities (status, starts_at);
create index if not exists activities_city_idx on public.activities (city_id, category);
create unique index if not exists activities_demo_uq
  on public.activities (lower(title), lower(organiser_name)) where is_demo;

drop trigger if exists dfi_activities_updated_at on public.activities;
create trigger dfi_activities_updated_at
  before update on public.activities
  for each row execute function public.set_updated_at();

create table if not exists public.activity_signups (
  id           uuid primary key default gen_random_uuid(),
  activity_id  uuid not null references public.activities(id) on delete cascade,
  user_id      uuid not null references auth.users(id) on delete cascade,
  created_at   timestamptz not null default now(),
  unique (activity_id, user_id)
);
create index if not exists activity_signups_user_idx on public.activity_signups (user_id, created_at);

create table if not exists public.mentors (
  id            uuid primary key default gen_random_uuid(),
  display_name  text not null,
  role_title    text,
  focus         text[],
  bio           text,
  city_id       uuid references public.cities(id) on delete set null,
  is_verified   boolean not null default false,
  is_active     boolean not null default false,   -- keep false until a mentor is genuinely onboarded
  created_at    timestamptz not null default now()
);

create table if not exists public.mentor_requests (
  id          uuid primary key default gen_random_uuid(),
  mentor_id   uuid not null references public.mentors(id) on delete cascade,
  user_id     uuid not null references auth.users(id) on delete cascade,
  message     text check (message is null or length(message) <= 500),
  status      text not null default 'sent',
  created_at  timestamptz not null default now()
);

-- ---------------------------------------------------------------------
-- Play: stories, attempts, choices, badges
-- ---------------------------------------------------------------------
create table if not exists public.stories (
  id                 text primary key,
  title              text not null,
  pack               text not null,                -- campus | hostel | online | sports | family | exams
  description        text,
  estimated_seconds  int not null default 35 check (estimated_seconds between 10 and 600),
  sort_order         int not null default 100,
  cover_image_key    text,                         -- maps to a bundled drawable, e.g. story_farewell_cover
  scenes             jsonb not null check (jsonb_typeof(scenes) = 'array'),
  version            int not null default 1,
  is_published       boolean not null default false,
  created_at         timestamptz not null default now(),
  updated_at         timestamptz not null default now()
);
drop trigger if exists dfi_stories_updated_at on public.stories;
create trigger dfi_stories_updated_at
  before update on public.stories
  for each row execute function public.set_updated_at();

create table if not exists public.story_attempts (
  id            uuid primary key default gen_random_uuid(),
  user_id       uuid not null references auth.users(id) on delete cascade,
  story_id      text not null references public.stories(id) on delete restrict,
  started_at    timestamptz not null default now(),
  completed_at  timestamptz,
  total_points  int not null default 0 check (total_points >= 0)
);
create index if not exists story_attempts_user_idx on public.story_attempts (user_id, completed_at);

create table if not exists public.choice_events (
  id          uuid primary key default gen_random_uuid(),
  seq         bigint generated always as identity,
  attempt_id  uuid not null references public.story_attempts(id) on delete cascade,
  user_id     uuid not null references auth.users(id) on delete cascade,
  scene_id    text not null,
  choice_id   text not null,
  tag         text,
  points      int not null default 0 check (points >= 0),
  next_scene  text,
  created_at  timestamptz not null default now(),
  unique (attempt_id, scene_id)                 -- one answer per scene = no duplicate points
);
create index if not exists choice_events_user_idx on public.choice_events (user_id, seq desc);

create table if not exists public.badges (
  id           text primary key,
  name         text not null,
  description  text,
  icon_key     text,
  sort_order   int not null default 100,
  is_active    boolean not null default true
);

create table if not exists public.user_badges (
  user_id    uuid not null references auth.users(id) on delete cascade,
  badge_id   text not null references public.badges(id) on delete cascade,
  earned_at  timestamptz not null default now(),
  primary key (user_id, badge_id)
);

-- ---------------------------------------------------------------------
-- Track Impact: anonymous surveys + private self-assessments
-- ---------------------------------------------------------------------
create table if not exists public.surveys (
  id         uuid primary key default gen_random_uuid(),
  month_key  date not null unique,                 -- first day of the month
  title      text not null,
  questions  jsonb not null check (jsonb_typeof(questions) = 'array'),
  is_active  boolean not null default true
);

-- Holds ONLY "this user already answered this survey". No answers here.
create table if not exists public.survey_submissions (
  survey_id  uuid not null references public.surveys(id) on delete cascade,
  user_id    uuid not null references auth.users(id) on delete cascade,
  primary key (survey_id, user_id)
);

-- Holds answers with NO user id and NO timestamp (only the month).
create table if not exists public.survey_responses (
  id          uuid primary key default gen_random_uuid(),
  survey_id   uuid not null references public.surveys(id) on delete cascade,
  month_key   date not null,
  city_id     uuid references public.cities(id) on delete set null,
  college_id  uuid references public.colleges(id) on delete set null,
  answers     jsonb not null check (jsonb_typeof(answers) = 'object')
);
create index if not exists survey_responses_idx on public.survey_responses (survey_id, college_id);

create table if not exists public.self_assessments (
  id          uuid primary key default gen_random_uuid(),
  user_id     uuid not null references auth.users(id) on delete cascade,
  kind        text not null check (kind in ('before','after')),
  confidence  smallint not null check (confidence between 1 and 5),
  created_at  timestamptz not null default now()
);
create unique index if not exists self_assessments_before_uq
  on public.self_assessments (user_id) where kind = 'before';
create index if not exists self_assessments_user_idx on public.self_assessments (user_id, created_at);

-- ---------------------------------------------------------------------
-- App configuration (public read)
-- ---------------------------------------------------------------------
create table if not exists public.app_config (
  key         text primary key,
  value       jsonb not null,
  updated_at  timestamptz not null default now()
);

-- ---------------------------------------------------------------------
-- Guards (triggers)
-- ---------------------------------------------------------------------
-- A college suggestion is rejected if the college is already listed, and a user
-- may have at most 5 pending suggestions.
create or replace function public.dfi_college_suggestion_guard()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  new.user_id := coalesce(new.user_id, auth.uid());
  new.name := btrim(new.name);

  if exists (
    select 1 from public.colleges c
    where c.city_id = new.city_id and lower(btrim(c.name)) = lower(new.name)
  ) then
    raise exception 'This college is already listed.' using errcode = '23505';
  end if;

  if (select count(*) from public.college_suggestions s
      where s.user_id = new.user_id and s.status = 'pending') >= 5 then
    raise exception 'You already have 5 suggestions waiting for review.' using errcode = '23514';
  end if;

  return new;
end;
$$;

drop trigger if exists dfi_college_suggestion_guard on public.college_suggestions;
create trigger dfi_college_suggestion_guard
  before insert on public.college_suggestions
  for each row execute function public.dfi_college_suggestion_guard();
