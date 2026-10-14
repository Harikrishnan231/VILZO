-- =====================================================================
-- Drug-Free India · Seed data                   (file 4 of 5)
-- Safe to re-run (ON CONFLICT DO NOTHING).
--
-- HONESTY RULES BUILT INTO THIS FILE
--   * Nothing here is marked verified. is_verified = false everywhere until YOUR TEAM checks it.
--   * Helpline numbers, centre names and college names must be checked against official sources
--     before release. Run 99_release_checks.sql to list everything still unverified.
--   * Support centres are seeded with NAME + CITY only (no invented phone numbers or addresses).
--     The app must hide the Call / Directions buttons when phone / address is null.
--   * No fake events, no fake impact statistics, no fake participant counts.
-- =====================================================================

-- ---------- cities ----------
insert into public.cities (name, state, slug, sort_order) values
  ('Puducherry', 'Puducherry',   'puducherry', 1),
  ('Karaikal',   'Puducherry',   'karaikal',   2),
  ('Chennai',    'Tamil Nadu',   'chennai',    3),
  ('Coimbatore', 'Tamil Nadu',   'coimbatore', 4),
  ('Bengaluru',  'Karnataka',    'bengaluru',  5),
  ('Hyderabad',  'Telangana',    'hyderabad',  6),
  ('Kochi',      'Kerala',       'kochi',      7),
  ('Mumbai',     'Maharashtra',  'mumbai',     8),
  ('Pune',       'Maharashtra',  'pune',       9),
  ('Delhi',      'Delhi',        'delhi',     10),
  ('Ghaziabad',  'Uttar Pradesh','ghaziabad', 11),
  ('Kolkata',    'West Bengal',  'kolkata',   12)
on conflict do nothing;

-- ---------- colleges (curated suggestions; the team should review the list) ----------
insert into public.colleges (city_id, name)
select c.id, v.name
from (values
  ('puducherry', 'Pondicherry University'),
  ('puducherry', 'Puducherry Technological University'),
  ('puducherry', 'Sri Manakula Vinayagar Engineering College'),
  ('puducherry', 'Jawaharlal Institute of Postgraduate Medical Education and Research (JIPMER)'),
  ('karaikal',   'National Institute of Technology Puducherry'),
  ('chennai',    'Indian Institute of Technology Madras'),
  ('chennai',    'Anna University, Guindy Campus'),
  ('chennai',    'Loyola College'),
  ('chennai',    'Madras Christian College'),
  ('bengaluru',  'Indian Institute of Science'),
  ('bengaluru',  'Bangalore University'),
  ('bengaluru',  'Christ (Deemed to be University)'),
  ('hyderabad',  'University of Hyderabad'),
  ('hyderabad',  'Osmania University'),
  ('mumbai',     'University of Mumbai'),
  ('mumbai',     'Indian Institute of Technology Bombay'),
  ('pune',       'Savitribai Phule Pune University'),
  ('delhi',      'University of Delhi'),
  ('delhi',      'Jawaharlal Nehru University'),
  ('delhi',      'Indian Institute of Technology Delhi'),
  ('kolkata',    'University of Calcutta'),
  ('kochi',      'Cochin University of Science and Technology')
) as v(slug, name)
join public.cities c on c.slug = v.slug
on conflict do nothing;

-- ---------- helplines (ALL UNVERIFIED until you confirm each one) ----------
insert into public.helplines (name, number, description, hours, is_emergency, is_verified, sort_order) values
  ('Emergency Response Support System (ERSS)', '112',
   'India''s single emergency number for police, fire and ambulance.',
   '24x7', true, false, 1),
  ('Tele-MANAS', '14416',
   'Free tele mental-health counselling and support.',
   '24x7', false, false, 2),
  ('Drug-dependence helpline (from project concept)', '1933',
   'Listed in the project concept as a national drug-dependence helpline. Confirm the official operator, hours and languages before release.',
   null, false, false, 3),
  ('De-addiction helpline (from project concept)', '14446',
   'Listed in the project concept as a de-addiction helpline. Confirm the official operator, hours and languages before release.',
   null, false, false, 4)
on conflict do nothing;

-- ---------- support centres (name + city + type only; add verified details yourself) ----------
insert into public.support_centres (city_id, name, type)
select c.id, v.name, v.type::public.support_centre_type
from (values
  ('bengaluru',  'NIMHANS, Centre for Addiction Medicine',                         'government'),
  ('ghaziabad',  'National Drug Dependence Treatment Centre (NDDTC), AIIMS',       'government'),
  ('chennai',    'Institute of Mental Health, Kilpauk',                            'government'),
  ('puducherry', 'JIPMER, Department of Psychiatry',                               'hospital')
) as v(slug, name, type)
join public.cities c on c.slug = v.slug
on conflict do nothing;

-- ---------- badges ----------
insert into public.badges (id, name, description, icon_key, sort_order) values
  ('first_steps',    'First Steps',    'Completed your first scenario.',                 'badge_first_steps',    1),
  ('calm_no',        'Calm No',        'Said no calmly and respectfully.',               'badge_calm_no',        2),
  ('think_first',    'Think First',    'Changed the plan instead of going along.',       'badge_think_first',    3),
  ('exit_plan',      'Exit Plan',      'Stepped away from a pressure situation.',        'badge_exit_plan',      4),
  ('trusted_friend', 'Trusted Friend', 'Reached out to someone you trust.',              'badge_trusted_friend', 5),
  ('streak_7',       '7-Day Streak',   'Practised on 7 days in a row.',                  'badge_streak_7',       6),
  ('team_player',    'Team Player',    'Unlocks with the upcoming multiplayer modes.',   'badge_team_player',    7)
on conflict do nothing;

-- ---------- app config ----------
insert into public.app_config (key, value) values
  ('min_cohort', '10'::jsonb),
  ('levels', '[
      {"level":1,"name":"Starter",   "min_points":0},
      {"level":2,"name":"Steady",    "min_points":30},
      {"level":3,"name":"Confident", "min_points":80},
      {"level":4,"name":"Champion",  "min_points":150}
   ]'::jsonb),
  ('privacy_policy_url', 'null'::jsonb),
  ('terms_url', 'null'::jsonb)
on conflict do nothing;

-- ---------- story: The Farewell Party ----------
-- imageKey / cover_image_key map to bundled drawables (see the image manifest in the Antigravity prompt).
-- The final scene (s3) has no choices = the ending. Every path must reach it.
insert into public.stories (id, title, pack, description, estimated_seconds, sort_order, cover_image_key, version, is_published, scenes)
values (
  'farewell_party',
  'The Farewell Party',
  'campus',
  'Arjun faces pressure from friends at a terrace party. Practise a calm refusal and exit plan.',
  35, 1, 'story_farewell_cover', 1, true,
  $json$
  [
    {
      "id": "s1", "imageKey": "farewell_s1",
      "narration": "Terrace farewell party. Music is playing in the background.",
      "speaker": "Rohan",
      "quote": "Come on, everyone is trying it. Don't be boring!",
      "timerSeconds": 20, "pressure": 0.6,
      "choices": [
        {"id": "a", "text": "Refuse confidently", "subtitle": "Stay firm and respectful", "points": 6, "tag": "calm_no",
         "feedback": "A calm, firm no is hard to argue with. You don't owe anyone an explanation.", "next": "s2b"},
        {"id": "b", "text": "Suggest another activity", "subtitle": "Find something else to do", "points": 5, "tag": "redirect",
         "feedback": "Changing the plan gives everyone an easy way out and keeps things friendly.", "next": "s2r"},
        {"id": "c", "text": "Leave the situation", "subtitle": "Walk away if needed", "points": 5, "tag": "exit_plan",
         "feedback": "Stepping away is always an option. You can come back later or head home.", "next": "s2c"},
        {"id": "d", "text": "Go along just this once", "subtitle": "Do what the group is doing", "points": 0, "tag": "gave_in",
         "feedback": "Many people feel this pull. Practising gives you a plan for next time. Let's look at what you could do.", "next": "s2a"}
      ]
    },
    {
      "id": "s2a", "imageKey": "farewell_s2a",
      "narration": "The moment passes, but Arjun feels uneasy.",
      "speaker": "Arjun",
      "quote": "I wish I had a plan. What can I do next time?",
      "timerSeconds": null, "pressure": 0.3,
      "choices": [
        {"id": "a", "text": "Prepare a short line", "subtitle": "For example: \"Not for me, thanks.\"", "points": 2, "tag": "reflect",
         "feedback": "A ready-made line makes it much easier to speak up in the moment.", "next": "s3"},
        {"id": "b", "text": "Talk to someone I trust", "subtitle": "Share how I feel", "points": 3, "tag": "trusted_contact",
         "feedback": "Talking it through with someone you trust is a strong step.", "next": "s3"},
        {"id": "c", "text": "Skip for now", "subtitle": "I'll think about it later", "points": 0, "tag": "no_response",
         "feedback": "That's okay. You can replay this scenario any time.", "next": "s3"}
      ]
    },
    {
      "id": "s2b", "imageKey": "farewell_s2b",
      "narration": "Rohan pushes a little harder.",
      "speaker": "Rohan",
      "quote": "Bro, one time won't hurt.",
      "timerSeconds": 20, "pressure": 0.8,
      "choices": [
        {"id": "a", "text": "I'm not judging you. It's just not for me.", "subtitle": "Calm and respectful", "points": 8, "tag": "calm_no",
         "feedback": "You respected him and still held your ground. That's a strong response.", "next": "s3"},
        {"id": "b", "text": "Let's go listen to the music instead.", "subtitle": "Change the subject", "points": 6, "tag": "redirect",
         "feedback": "Redirecting keeps the mood light and moves attention elsewhere.", "next": "s3"},
        {"id": "c", "text": "That's stupid. Don't do it either.", "subtitle": "Argue back", "points": 2, "tag": "aggressive",
         "feedback": "Being firm is great, but arguing can start a fight. Try keeping it about you: \"not for me\".", "next": "s3"}
      ]
    },
    {
      "id": "s2r", "imageKey": "farewell_s2r",
      "narration": "Rohan shrugs. Meera joins the group.",
      "speaker": "Meera",
      "quote": "We were about to get some food downstairs. Coming?",
      "timerSeconds": 20, "pressure": 0.4,
      "choices": [
        {"id": "a", "text": "Join them downstairs", "subtitle": "Go along with the new plan", "points": 6, "tag": "redirect",
         "feedback": "A new plan gives everyone something better to do.", "next": "s3"},
        {"id": "b", "text": "Invite others along too", "subtitle": "Bring more friends", "points": 5, "tag": "redirect",
         "feedback": "Bringing others along makes the new plan easy to follow.", "next": "s3"}
      ]
    },
    {
      "id": "s2c", "imageKey": "farewell_s2c",
      "narration": "Arjun steps away from the crowd and takes out his phone.",
      "speaker": "Arjun",
      "quote": "I'll text Meera. She'll understand.",
      "timerSeconds": 20, "pressure": 0.4,
      "choices": [
        {"id": "a", "text": "Text Meera: \"Can you call me?\"", "subtitle": "Ask a trusted friend to help", "points": 8, "tag": "trusted_contact",
         "feedback": "Asking a trusted person for help gives you an easy, natural reason to leave.", "next": "s3"},
        {"id": "b", "text": "Quietly head home", "subtitle": "Leave without a fuss", "points": 5, "tag": "exit_plan",
         "feedback": "Leaving quietly is a valid choice. You don't need to explain yourself.", "next": "s3"}
      ]
    },
    {
      "id": "s3", "imageKey": "farewell_s3",
      "narration": "Later that night, Arjun thinks about how it went.",
      "speaker": "Arjun",
      "quote": "Saying no gets easier with practice.",
      "takeaway": "Saying no is a skill, and skills improve with practice.",
      "isEnd": true,
      "choices": []
    }
  ]
  $json$::jsonb
)
on conflict (id) do nothing;

-- ---------- this month's pulse survey ----------
-- Add one row per month (copy and change month_key). The app uses the latest active survey.
insert into public.surveys (month_key, title, questions)
values (
  date_trunc('month', now())::date,
  'Monthly pulse survey',
  $json$
  [
    {"id": "confidence", "prompt": "How confident are you in handling peer pressure?",
     "options": [
       {"id": "very", "label": "Very confident"},
       {"id": "somewhat", "label": "Somewhat confident"},
       {"id": "not", "label": "Not confident"}]},
    {"id": "help_awareness", "prompt": "Do you know where to get support if you or a friend needs it?",
     "options": [
       {"id": "yes", "label": "Yes"},
       {"id": "unsure", "label": "Not sure"},
       {"id": "no", "label": "No"}]},
    {"id": "activity", "prompt": "Did you take part in a positive activity this month?",
     "options": [
       {"id": "yes", "label": "Yes"},
       {"id": "no", "label": "No"}]},
    {"id": "risk_understanding", "prompt": "How would you rate your understanding of the risks?",
     "options": [
       {"id": "high", "label": "Good"},
       {"id": "medium", "label": "Some"},
       {"id": "low", "label": "Limited"}]}
  ]
  $json$::jsonb
)
on conflict (month_key) do nothing;
