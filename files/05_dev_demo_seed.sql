-- =====================================================================
-- Drug-Free India · DEMO activities              (file 5 of 5, OPTIONAL)
--
-- For development and live demos ONLY, so the Explore screen is not empty.
-- Every row has is_demo = true. The app MUST show a small "Demo" tag on these and
-- must never present them as real, verified listings.
--
-- Remove them before the real launch:
--   delete from public.activities where is_demo;
-- =====================================================================

insert into public.activities
  (kind, category, title, description, organiser_name, city_id, college_id, venue,
   starts_at, ends_at, recurrence_text, eligibility, status, is_verified, is_demo)
select
  v.kind::public.activity_kind,
  v.category::public.activity_category,
  v.title, v.description, v.organiser, ci.id, co.id, v.venue,
  case when v.start_in_days is null then null
       else date_trunc('day', now()) + make_interval(days => v.start_in_days, hours => v.start_hour) end,
  null,
  v.recurrence, v.eligibility, 'published', false, true
from (values
  ('event',       'art',          'Campus Art Fest',                       'A day of live painting, sketching and a student art exhibition.',               'Demo · Art Circle',        'puducherry', 'Pondicherry University',                      'Main auditorium (demo)',   5, 10, null,                                  'Open to all students'),
  ('event',       'sports',       'Football Tryouts',                      'Open tryouts for the campus football team. Bring your boots.',                  'Demo · Sports Club',       'karaikal',   'National Institute of Technology Puducherry', 'Sports ground (demo)',     7, 16, null,                                  'Open to all students'),
  ('club',        'sports',       'Weekend Football League',               'Friendly weekend matches for all skill levels.',                                'Demo · Sports Club',       'puducherry', null,                                          'City ground (demo)',    null, 0, 'Every Saturday · 5:00 PM – 7:00 PM', 'Open to all'),
  ('volunteering','volunteering', 'Youth Community Clean-up & Tree Plantation', 'Join a community clean-up and plant saplings with fellow volunteers.',  'Demo · Green Volunteers',  'puducherry', null,                                          'Riverfront park (demo)', null, 0, 'First Sunday of each month · 7:30 AM', 'Open to all'),
  ('club',        'poetry',       'Spoken Word & Open Mic Poetry',         'Share or enjoy original poetry in a relaxed open mic.',                         'Demo · Literary Club',     'puducherry', null,                                          'Library gazebo (demo)', null, 0, 'Every Thursday · 5:30 PM',           'Open to all students'),
  ('activity',    'fitness',      'Sunrise Yoga & Breathwork',             'A gentle morning session to start the day with a clear mind.',                  'Demo · Wellness Club',     'puducherry', null,                                          'Sports pavilion (demo)', null, 0, 'Every morning · 6:30 AM',            'Open to all'),
  ('club',        'music',        'Acoustic Jam & Music Band',             'Bring an instrument or just listen. All genres welcome.',                       'Demo · Music Society',     'puducherry', null,                                          'Amphitheatre (demo)',   null, 0, 'Every Friday · 6:00 PM – 8:00 PM',   'Open to all students'),
  ('club',        'art',          'Watercolour & Canvas Circle',           'Relaxed weekly painting sessions. Materials provided.',                         'Demo · Art Circle',        'puducherry', null,                                          'Fine Arts studio (demo)', null, 0, 'Every Sunday · 10:30 AM – 1:00 PM',  'Open to all'),
  ('club',        'skills',       'Photography Walk Club',                 'Monthly photo walks around the city. Phone cameras welcome.',                   'Demo · Photography Club',  'puducherry', null,                                          'Meeting point announced (demo)', null, 0, 'Second Saturday of each month',   'Open to all'),
  ('club',        'volunteering', 'Green Campus Club',                     'Campus sustainability projects and awareness drives.',                          'Demo · Green Campus Club', 'puducherry', null,                                          'Campus (demo)',         null, 0, 'Meets every other Wednesday',        'Open to all students')
) as v(kind, category, title, description, organiser, city_slug, college_name, venue, start_in_days, start_hour, recurrence, eligibility)
join public.cities ci on ci.slug = v.city_slug
left join public.colleges co on co.city_id = ci.id and lower(btrim(co.name)) = lower(btrim(coalesce(v.college_name, '')))
on conflict do nothing;
