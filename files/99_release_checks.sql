-- =====================================================================
-- Drug-Free India · Release checks (run in the SQL Editor before a public release)
-- Every query should return ZERO rows (or the numbers you expect).
-- =====================================================================

-- 1) Tables in `public` WITHOUT row level security (must be empty)
select schemaname, tablename
from pg_tables
where schemaname = 'public' and not rowsecurity;

-- 2) Helplines nobody has verified yet (verify the number, hours and source, then set is_verified = true)
select name, number, hours
from public.helplines
where is_active and not is_verified;

-- 3) Support centres still unverified or missing contact details
select name, phone is null as missing_phone, address is null as missing_address
from public.support_centres
where is_active and (not is_verified or phone is null or address is null);

-- 4) Colleges not yet verified (fine for a demo, check before launch)
select name from public.colleges where is_active and not is_verified order by name;

-- 5) Demo activities still published (must be 0 for a real launch)
select title from public.activities where is_demo;

-- 6) Policies that allow writes to scoring tables from the app (must be empty)
select tablename, policyname, cmd
from pg_policies
where schemaname = 'public'
  and tablename in ('story_attempts','choice_events','user_badges','survey_submissions','survey_responses')
  and cmd in ('INSERT','UPDATE','DELETE','ALL');

-- 7) Privacy and terms links must be set in app_config
select key, value from public.app_config where key in ('privacy_policy_url','terms_url') and value = 'null'::jsonb;
