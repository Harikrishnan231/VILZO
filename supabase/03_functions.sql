-- =====================================================================
-- Drug-Free India · RPC functions               (file 3 of 5)
--
-- All functions are SECURITY DEFINER with an empty search_path, so every object is
-- schema-qualified. They are the ONLY way the app can write scores, badges and survey data.
-- Call them from the app with supabase.postgrest.rpc("function_name", params).
-- =====================================================================

-- ---------------------------------------------------------------------
-- Small helpers
-- ---------------------------------------------------------------------
create or replace function public.cfg_int(p_key text, p_default int)
returns int
language sql
stable
security definer
set search_path = ''
as $$
  select coalesce((select (c.value #>> '{}')::int from public.app_config c where c.key = p_key), p_default);
$$;

-- True only if the signed-in user really confirmed their phone with an OTP.
create or replace function public.is_phone_verified()
returns boolean
language sql
stable
security definer
set search_path = ''
as $$
  select coalesce((select u.phone_confirmed_at is not null from auth.users u where u.id = auth.uid()), false);
$$;

-- ---------------------------------------------------------------------
-- Game: start / answer / resume / complete
-- ---------------------------------------------------------------------
create or replace function public.start_attempt(p_story text, p_restart boolean default false)
returns uuid
language plpgsql
security definer
set search_path = ''
as $$
declare
  v_uid uuid := auth.uid();
  v_id  uuid;
begin
  if v_uid is null then raise exception 'not authenticated' using errcode = '28000'; end if;
  if not exists (select 1 from public.stories s where s.id = p_story and s.is_published) then
    raise exception 'story unavailable' using errcode = 'P0002';
  end if;

  if not p_restart then
    select a.id into v_id
    from public.story_attempts a
    where a.user_id = v_uid and a.story_id = p_story and a.completed_at is null
      and a.started_at > now() - interval '1 day'
    order by a.started_at desc
    limit 1;
  end if;

  if v_id is null then
    insert into public.story_attempts (user_id, story_id) values (v_uid, p_story) returning id into v_id;
  end if;
  return v_id;
end;
$$;

-- Where should an interrupted attempt continue?
create or replace function public.attempt_state(p_attempt uuid)
returns jsonb
language plpgsql
stable
security definer
set search_path = ''
as $$
declare
  v_uid uuid := auth.uid();
  v_attempt public.story_attempts;
  v_first text;
  v_next text;
  v_total int;
  v_answered int;
begin
  if v_uid is null then raise exception 'not authenticated' using errcode = '28000'; end if;
  select * into v_attempt from public.story_attempts where id = p_attempt and user_id = v_uid;
  if not found then raise exception 'attempt not found' using errcode = 'P0002'; end if;

  select s.scenes->0->>'id' into v_first from public.stories s where s.id = v_attempt.story_id;
  select e.next_scene into v_next from public.choice_events e where e.attempt_id = p_attempt order by e.seq desc limit 1;
  select coalesce(sum(e.points), 0), count(*) into v_total, v_answered from public.choice_events e where e.attempt_id = p_attempt;

  return jsonb_build_object(
    'next_scene', coalesce(v_next, v_first),
    'total_points', v_total,
    'answered', v_answered,
    'completed', v_attempt.completed_at is not null
  );
end;
$$;

-- Scores are looked up SERVER-SIDE from the story JSON. The client cannot choose its own points.
-- One answer per scene per attempt (unique constraint) → repeating a tap cannot add points.
create or replace function public.submit_choice(p_attempt uuid, p_scene text, p_choice text)
returns jsonb
language plpgsql
security definer
set search_path = ''
as $$
declare
  v_uid      uuid := auth.uid();
  v_attempt  public.story_attempts;
  v_scenes   jsonb;
  v_choice   jsonb;
  v_expected text;
  v_points   int;
  v_rows     int;
  v_total    int;
begin
  if v_uid is null then raise exception 'not authenticated' using errcode = '28000'; end if;

  select * into v_attempt from public.story_attempts where id = p_attempt and user_id = v_uid;
  if not found then raise exception 'attempt not found' using errcode = 'P0002'; end if;
  if v_attempt.completed_at is not null then raise exception 'attempt already completed'; end if;

  -- Same scene answered again? Return the earlier result, award nothing.
  if exists (select 1 from public.choice_events e where e.attempt_id = p_attempt and e.scene_id = p_scene) then
    select coalesce(sum(e.points), 0) into v_total from public.choice_events e where e.attempt_id = p_attempt;
    return jsonb_build_object('accepted', false, 'duplicate', true, 'points', 0, 'total_points', v_total);
  end if;

  select s.scenes into v_scenes from public.stories s where s.id = v_attempt.story_id and s.is_published;
  if v_scenes is null then raise exception 'story unavailable' using errcode = 'P0002'; end if;

  -- Scenes must be answered in story order (no skipping ahead, no forged scenes).
  select e.next_scene into v_expected from public.choice_events e where e.attempt_id = p_attempt order by e.seq desc limit 1;
  if v_expected is null then v_expected := v_scenes->0->>'id'; end if;
  if p_scene <> v_expected then raise exception 'scene out of order'; end if;

  select c into v_choice
  from jsonb_array_elements(v_scenes) as sc,
       jsonb_array_elements(coalesce(sc->'choices', '[]'::jsonb)) as c
  where sc->>'id' = p_scene and c->>'id' = p_choice
  limit 1;
  if v_choice is null then raise exception 'invalid choice'; end if;

  v_points := greatest(coalesce((v_choice->>'points')::int, 0), 0);

  insert into public.choice_events (attempt_id, user_id, scene_id, choice_id, tag, points, next_scene)
  values (p_attempt, v_uid, p_scene, p_choice, v_choice->>'tag', v_points, v_choice->>'next')
  on conflict (attempt_id, scene_id) do nothing;
  get diagnostics v_rows = row_count;

  select coalesce(sum(e.points), 0) into v_total from public.choice_events e where e.attempt_id = p_attempt;

  return jsonb_build_object(
    'accepted', v_rows = 1,
    'duplicate', v_rows = 0,
    'points', case when v_rows = 1 then v_points else 0 end,
    'total_points', v_total,
    'tag', v_choice->>'tag',
    'next', v_choice->>'next',
    'feedback', v_choice->>'feedback'
  );
end;
$$;

-- Streak = consecutive days (India time) with at least one COMPLETED scenario,
-- counted up to today or yesterday. Real data only.
create or replace function public.my_streak()
returns int
language sql
stable
security definer
set search_path = ''
as $$
  with days as (
    select distinct (a.completed_at at time zone 'Asia/Kolkata')::date as d
    from public.story_attempts a
    where a.user_id = auth.uid() and a.completed_at is not null
  ),
  grp as (
    select d, d - (row_number() over (order by d))::int as g from days
  ),
  runs as (
    select max(d) as last_day, count(*)::int as n from grp group by g
  )
  select coalesce(
    (select r.n from runs r
      where r.last_day >= ((now() at time zone 'Asia/Kolkata')::date - 1)
      order by r.last_day desc limit 1),
    0);
$$;

-- Marks the attempt complete ONLY if the player really reached an ending scene.
create or replace function public.complete_attempt(p_attempt uuid)
returns jsonb
language plpgsql
security definer
set search_path = ''
as $$
declare
  v_uid        uuid := auth.uid();
  v_attempt    public.story_attempts;
  v_scenes     jsonb;
  v_last_next  text;
  v_at_end     boolean;
  v_total      int;
  v_candidates text[] := array['first_steps'];
  v_new        text[];
  v_streak     int;
begin
  if v_uid is null then raise exception 'not authenticated' using errcode = '28000'; end if;

  select * into v_attempt from public.story_attempts where id = p_attempt and user_id = v_uid;
  if not found then raise exception 'attempt not found' using errcode = 'P0002'; end if;

  -- Already completed: return the stored result, award nothing twice.
  if v_attempt.completed_at is not null then
    return jsonb_build_object('total_points', v_attempt.total_points, 'new_badges', '[]'::jsonb,
                              'streak', public.my_streak(), 'already_completed', true);
  end if;

  select s.scenes into v_scenes from public.stories s where s.id = v_attempt.story_id;

  select e.next_scene into v_last_next from public.choice_events e where e.attempt_id = p_attempt order by e.seq desc limit 1;
  if v_last_next is null then raise exception 'no answers recorded'; end if;

  select exists (
    select 1 from jsonb_array_elements(v_scenes) as sc
    where sc->>'id' = v_last_next and coalesce(jsonb_array_length(sc->'choices'), 0) = 0
  ) into v_at_end;
  if not v_at_end then raise exception 'scenario not finished'; end if;

  select coalesce(sum(e.points), 0) into v_total from public.choice_events e where e.attempt_id = p_attempt;

  update public.story_attempts set completed_at = now(), total_points = v_total where id = p_attempt;

  if exists (select 1 from public.choice_events e where e.attempt_id = p_attempt and e.tag = 'calm_no') then
    v_candidates := array_append(v_candidates, 'calm_no');
  end if;
  if exists (select 1 from public.choice_events e where e.attempt_id = p_attempt and e.tag = 'exit_plan') then
    v_candidates := array_append(v_candidates, 'exit_plan');
  end if;
  if exists (select 1 from public.choice_events e where e.attempt_id = p_attempt and e.tag = 'redirect') then
    v_candidates := array_append(v_candidates, 'think_first');
  end if;
  if exists (select 1 from public.choice_events e where e.attempt_id = p_attempt and e.tag = 'trusted_contact') then
    v_candidates := array_append(v_candidates, 'trusted_friend');
  end if;

  v_streak := public.my_streak();
  if v_streak >= 7 then v_candidates := array_append(v_candidates, 'streak_7'); end if;

  with ins as (
    insert into public.user_badges (user_id, badge_id)
    select v_uid, b from unnest(v_candidates) as b
    where exists (select 1 from public.badges bd where bd.id = b and bd.is_active)
    on conflict do nothing
    returning badge_id
  )
  select coalesce(array_agg(badge_id), '{}') into v_new from ins;

  return jsonb_build_object('total_points', v_total, 'new_badges', to_jsonb(v_new),
                            'streak', v_streak, 'already_completed', false);
end;
$$;

-- ---------------------------------------------------------------------
-- Home / Profile / Track Impact: personal numbers (real data only)
-- ---------------------------------------------------------------------
-- practice_score = share of your last 20 scenario choices that were assertive
-- (calm_no, redirect, exit_plan, trusted_contact). Hidden (null) until 5 choices exist.
-- It is a practice indicator, NOT a clinical or behavioural measure. Say so in the UI.
create or replace function public.my_stats()
returns jsonb
language plpgsql
stable
security definer
set search_path = ''
as $$
declare
  v_uid    uuid := auth.uid();
  v_good   text[] := array['calm_no','redirect','exit_plan','trusted_contact'];
  v_n1 int; v_g1 int; v_n2 int; v_g2 int;
  v_score  int;
  v_prev   int;
  v_points int;
  v_done   int;
  v_total  int;
  v_badges int;
  v_joined int;
begin
  if v_uid is null then raise exception 'not authenticated' using errcode = '28000'; end if;

  with ev as (
    select e.tag, row_number() over (order by e.seq desc) as rn
    from public.choice_events e where e.user_id = v_uid
  )
  select
    count(*) filter (where rn <= 20),
    count(*) filter (where rn <= 20 and tag = any(v_good)),
    count(*) filter (where rn > 20 and rn <= 40),
    count(*) filter (where rn > 20 and rn <= 40 and tag = any(v_good))
  into v_n1, v_g1, v_n2, v_g2
  from ev;

  v_score := case when v_n1 >= 5 then round(100.0 * v_g1 / v_n1)::int end;
  v_prev  := case when v_n2 >= 5 then round(100.0 * v_g2 / v_n2)::int end;

  select coalesce(sum(t.best), 0), count(*) into v_points, v_done
  from (
    select max(a.total_points) as best
    from public.story_attempts a
    where a.user_id = v_uid and a.completed_at is not null
    group by a.story_id
  ) t;

  select count(*) into v_total from public.stories s where s.is_published;
  select count(*) into v_badges from public.user_badges b where b.user_id = v_uid;
  select count(*) into v_joined from public.activity_signups g
   where g.user_id = v_uid and g.created_at >= date_trunc('month', now());

  return jsonb_build_object(
    'scenarios_completed', v_done,
    'scenarios_total', v_total,
    'total_points', v_points,
    'streak_days', public.my_streak(),
    'practice_score', v_score,
    'practice_score_delta', case when v_score is not null and v_prev is not null then v_score - v_prev end,
    'badges_earned', v_badges,
    'activities_joined_this_month', v_joined
  );
end;
$$;

-- Month-by-month practice score for the "trend" chart (null where there is too little data).
create or replace function public.my_progress_series(p_months int default 6)
returns table (month date, practice_score int, answers int, scenarios_completed int)
language sql
stable
security definer
set search_path = ''
as $$
  with months as (
    select (date_trunc('month', now() at time zone 'Asia/Kolkata') - make_interval(months => g))::date as m
    from generate_series(0, greatest(least(coalesce(p_months, 6), 24), 1) - 1) as g
  )
  select
    mo.m,
    (select case when count(*) >= 3 then
              round(100.0 * count(*) filter (where e.tag = any(array['calm_no','redirect','exit_plan','trusted_contact'])) / count(*))::int
            end
       from public.choice_events e
      where e.user_id = auth.uid()
        and (e.created_at at time zone 'Asia/Kolkata')::date >= mo.m
        and (e.created_at at time zone 'Asia/Kolkata')::date < (mo.m + interval '1 month')::date),
    (select count(*)::int from public.choice_events e
      where e.user_id = auth.uid()
        and (e.created_at at time zone 'Asia/Kolkata')::date >= mo.m
        and (e.created_at at time zone 'Asia/Kolkata')::date < (mo.m + interval '1 month')::date),
    (select count(*)::int from public.story_attempts a
      where a.user_id = auth.uid() and a.completed_at is not null
        and (a.completed_at at time zone 'Asia/Kolkata')::date >= mo.m
        and (a.completed_at at time zone 'Asia/Kolkata')::date < (mo.m + interval '1 month')::date)
  from months mo
  order by mo.m;
$$;

-- ---------------------------------------------------------------------
-- College leaderboard: aggregates only, above a minimum cohort (app_config.min_cohort, default 10).
-- No individual names, no individual scores. Users can opt out (profiles.include_in_college_stats).
-- ---------------------------------------------------------------------
create or replace function public.college_leaderboard(p_city uuid default null, p_limit int default 20)
returns table (college_id uuid, college_name text, city_name text, participants int, points bigint)
language sql
stable
security definer
set search_path = ''
as $$
  with best as (
    select a.user_id, a.story_id, max(a.total_points) as pts
    from public.story_attempts a
    where a.completed_at is not null
    group by a.user_id, a.story_id
  ),
  per_user as (
    select b.user_id, sum(b.pts) as pts from best b group by b.user_id
  )
  select c.id, c.name, ci.name, count(*)::int, sum(pu.pts)::bigint
  from per_user pu
  join public.profiles p on p.id = pu.user_id and p.include_in_college_stats
  join public.colleges c on c.id = p.college_id and c.is_active
  join public.cities ci on ci.id = c.city_id
  where p_city is null or c.city_id = p_city
  group by c.id, c.name, ci.name
  having count(*) >= public.cfg_int('min_cohort', 10)
  order by sum(pu.pts) desc, count(*) desc
  limit greatest(least(coalesce(p_limit, 20), 50), 1);
$$;

-- ---------------------------------------------------------------------
-- Anonymous surveys
-- ---------------------------------------------------------------------
create or replace function public.submit_survey(p_survey uuid, p_answers jsonb)
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
  v_uid     uuid := auth.uid();
  v_survey  public.surveys;
  v_city    uuid;
  v_college uuid;
  v_share   boolean;
begin
  if v_uid is null then raise exception 'not authenticated' using errcode = '28000'; end if;
  if p_answers is null or jsonb_typeof(p_answers) <> 'object' then raise exception 'invalid answers'; end if;

  select * into v_survey from public.surveys s where s.id = p_survey and s.is_active;
  if not found then raise exception 'survey unavailable' using errcode = 'P0002'; end if;

  -- Every answer must be a real option of a real question.
  if exists (
    select 1
    from jsonb_each_text(p_answers) as a
    where not exists (
      select 1
      from jsonb_array_elements(v_survey.questions) as q,
           jsonb_array_elements(q->'options') as o
      where q->>'id' = a.key and o->>'id' = a.value
    )
  ) then
    raise exception 'invalid answers';
  end if;

  -- Step 1: remember only that this user has answered (no answers stored here).
  begin
    insert into public.survey_submissions (survey_id, user_id) values (p_survey, v_uid);
  exception when unique_violation then
    raise exception 'already submitted' using errcode = '23505';
  end;

  select p.city_id, p.college_id, p.include_in_college_stats into v_city, v_college, v_share
  from public.profiles p where p.id = v_uid;

  -- Step 2: store the answers with NO user id and NO timestamp.
  insert into public.survey_responses (survey_id, month_key, city_id, college_id, answers)
  values (p_survey, v_survey.month_key, v_city, case when coalesce(v_share, true) then v_college end, p_answers);
end;
$$;

-- Aggregated results. Returns {"insufficient_data": true} until enough people have answered.
create or replace function public.community_impact(p_college uuid default null, p_survey uuid default null)
returns jsonb
language plpgsql
stable
security definer
set search_path = ''
as $$
declare
  v_min       int := public.cfg_int('min_cohort', 10);
  v_survey    public.surveys;
  v_total     int;
  v_questions jsonb;
begin
  if auth.uid() is null then raise exception 'not authenticated' using errcode = '28000'; end if;

  if p_survey is not null then
    select * into v_survey from public.surveys s where s.id = p_survey;
  else
    select * into v_survey from public.surveys s order by s.month_key desc limit 1;
  end if;
  if not found then
    return jsonb_build_object('insufficient_data', true, 'min_cohort', v_min);
  end if;

  select count(*) into v_total from public.survey_responses r
   where r.survey_id = v_survey.id and (p_college is null or r.college_id = p_college);

  if v_total < v_min then
    return jsonb_build_object('insufficient_data', true, 'min_cohort', v_min);
  end if;

  select coalesce(jsonb_agg(
           jsonb_build_object(
             'question_id', q->>'id',
             'prompt', q->>'prompt',
             'options', (
               select jsonb_agg(jsonb_build_object(
                        'option_id', o->>'id',
                        'label', o->>'label',
                        'count', (select count(*) from public.survey_responses r
                                   where r.survey_id = v_survey.id
                                     and (p_college is null or r.college_id = p_college)
                                     and r.answers->>(q->>'id') = o->>'id')))
               from jsonb_array_elements(q->'options') as o)
           )), '[]'::jsonb)
  into v_questions
  from jsonb_array_elements(v_survey.questions) as q;

  return jsonb_build_object(
    'insufficient_data', false,
    'month', v_survey.month_key,
    'responses', v_total,
    'questions', v_questions
  );
end;
$$;

-- ---------------------------------------------------------------------
-- Account deletion (removes the auth user; related rows cascade).
-- Survey responses stay because they were never linked to the user.
-- ---------------------------------------------------------------------
create or replace function public.delete_my_account()
returns void
language plpgsql
security definer
set search_path = ''
as $$
begin
  if auth.uid() is null then raise exception 'not authenticated' using errcode = '28000'; end if;
  delete from auth.users where id = auth.uid();
end;
$$;

-- ---------------------------------------------------------------------
-- Who may call what
-- ---------------------------------------------------------------------
revoke all on function
  public.cfg_int(text, int),
  public.is_phone_verified(),
  public.start_attempt(text, boolean),
  public.attempt_state(uuid),
  public.submit_choice(uuid, text, text),
  public.my_streak(),
  public.complete_attempt(uuid),
  public.my_stats(),
  public.my_progress_series(int),
  public.college_leaderboard(uuid, int),
  public.submit_survey(uuid, jsonb),
  public.community_impact(uuid, uuid),
  public.delete_my_account()
from public, anon;

grant execute on function
  public.cfg_int(text, int),
  public.is_phone_verified(),
  public.start_attempt(text, boolean),
  public.attempt_state(uuid),
  public.submit_choice(uuid, text, text),
  public.my_streak(),
  public.complete_attempt(uuid),
  public.my_stats(),
  public.my_progress_series(int),
  public.college_leaderboard(uuid, int),
  public.submit_survey(uuid, jsonb),
  public.community_impact(uuid, uuid),
  public.delete_my_account()
to authenticated;
