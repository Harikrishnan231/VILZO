-- =====================================================================
-- Drug-Free India · Real Explore listings        (file 7)
-- Run AFTER files 01-04. Safe to re-run.
--
-- What this adds
--   * extra columns on public.activities so the Explore card can show a "Visit official page"
--     button, the date as written by the organiser, and how sure we are about the date
--   * 11 real listings (hackathons, sports, art, music, dance, volunteering, a tech community)
--
-- HOW THESE WERE COLLECTED: each entry was read from the organiser's own page or an official
-- government / IEEE page on 17 October 2026 (source_checked_on). Dates can change.
--   date_confidence = 'confirmed' : the organiser published the date
--   date_confidence = 'expected'  : the event repeats every year and the 2026-27 dates are not
--                                   published yet, so the date shown follows the last edition
--   date_confidence = 'ongoing'   : no fixed date (volunteering / community)
-- is_verified stays FALSE on purpose: the verified tick is for things YOUR team has confirmed.
-- The app shows a "From official page" chip instead, using source_checked_on.
--
-- IMAGES: do not hotlink organiser images. cover_image_key points at an illustration bundled
-- in the app (see the prompt). Remove nothing here if you add your own art later.
-- =====================================================================

alter table public.activities add column if not exists source_url          text
  check (source_url is null or source_url ~* '^https?://');
alter table public.activities add column if not exists source_checked_on   date;
alter table public.activities add column if not exists date_label          text;
alter table public.activities add column if not exists date_confidence     text
  check (date_confidence is null or date_confidence in ('confirmed','expected','ongoing'));
alter table public.activities add column if not exists tags                text[] not null default '{}';
alter table public.activities add column if not exists cover_image_key     text;
alter table public.activities add column if not exists location_text       text;
alter table public.activities add column if not exists region              text
  check (region is null or region in ('puducherry_area','tamil_nadu','elsewhere_in_india','online'));

create index if not exists activities_tags_idx on public.activities using gin (tags);
create unique index if not exists activities_real_uq
  on public.activities (lower(title), lower(organiser_name)) where not is_demo;

-- extra cities used by these listings
insert into public.cities (name, state, slug, sort_order) values
  ('Auroville',   'Tamil Nadu', 'auroville',   13),
  ('Mamallapuram','Tamil Nadu', 'mamallapuram',14),
  ('Panaji',      'Goa',        'panaji',      15)
on conflict do nothing;

insert into public.activities
  (kind, category, title, description, organiser_name, city_id, venue, location_text, region,
   starts_at, ends_at, date_label, date_confidence, recurrence_text, eligibility,
   registration_url, source_url, source_checked_on, tags, cover_image_key,
   status, is_verified, is_demo)
select
  v.kind::public.activity_kind, v.category::public.activity_category, v.title, v.description,
  v.organiser, ci.id, v.venue, v.location_text, v.region,
  v.starts_at::timestamptz, v.ends_at::timestamptz, v.date_label, v.date_confidence,
  v.recurrence, v.eligibility, v.link, v.link, date '2026-10-17', v.tags, v.cover,
  'published', false, false
from (values
  -- ---------------- HACKATHONS ----------------
  ('event','skills',
   'Viksit Bharat Young Leaders Dialogue 2027: National Dialogue (includes Hack for Social Cause)',
   'The national round of the Ministry of Youth Affairs and Sports youth festival. Its six tracks include Hack for Social Cause, a technology hackathon for social problems. Entry is through the MY Bharat portal stages (district and state rounds run in Nov-Dec 2026).',
   'MY Bharat, Ministry of Youth Affairs & Sports', 'delhi', 'Bharat Mandapam', 'New Delhi', 'elsewhere_in_india',
   '2027-01-10 00:00+05:30', '2027-01-12 23:59+05:30', '10-12 Jan 2027', 'confirmed', null,
   'Age 15-29. Register on the MY Bharat portal; selection is staged.',
   'https://mybharat.gov.in/vbyld-2027', array['hackathon','national','youth'], 'evt_vbyld_hack'),

  ('event','skills',
   'Smart India Hackathon 2026: Grand Finale',
   'India''s largest student hackathon (Ministry of Education Innovation Cell and AICTE). The 36-hour Grand Finale is held at nodal centres across India in December 2026. Only teams nominated through their college SPOC take part.',
   'Ministry of Education Innovation Cell & AICTE', null, 'Nodal centres across India (assigned to finalists)', 'Across India', 'elsewhere_in_india',
   '2026-12-01 00:00+05:30', null, 'December 2026 (exact dates on sih.gov.in)', 'expected', null,
   'Teams of 6 from the same institution, nominated by the college SPOC.',
   'https://www.sih.gov.in/', array['hackathon','national','students'], 'evt_sih_finale'),

  ('event','skills',
   'HackVerse: The Intercollegiate Challenge (IEEE)',
   'A two-day intercollegiate hackathon by an IEEE student branch, bringing students from different colleges to compete, collaborate and build impactful solutions, with mentorship.',
   'IEEE student branch, RNS Institute of Technology', 'bengaluru', 'RNS Institute of Technology, Channasandra', 'Bengaluru, Karnataka', 'elsewhere_in_india',
   '2026-11-28 10:00+05:30', '2026-11-29 17:00+05:30', '28-29 Nov 2026', 'confirmed', null,
   'Students from any institution. Check the page for team rules and fees.',
   'https://events.vtools.ieee.org/m/557799', array['hackathon','ieee','students'], 'evt_hackverse_ieee'),

  -- ---------------- SPORTS / FITNESS ----------------
  ('event','sports',
   'Auroville Marathon 2027',
   'A non-commercial "run for the joy of running" through Auroville with 10 km, half-marathon and full-marathon distances. Every finisher is treated as a winner. Registration is online only and opens on 14 Nov 2026; bib collection is on 13 Feb.',
   'Auroville Runners with the Auroville community', 'auroville', 'Auroville', 'Auroville, near Puducherry', 'puducherry_area',
   '2027-02-14 00:00+05:30', null, 'Sun 14 Feb 2027 (registration opens 14 Nov 2026)', 'confirmed', null,
   'Check the minimum age for each distance on the official site.',
   'https://www.aurovillemarathon.com/', array['running','marathon','sports'], 'evt_auroville_marathon'),

  ('event','fitness',
   'International Yoga Festival, Puducherry (32nd edition)',
   'Held every year by the Puducherry Tourism Department with talks, workshops, yogasana competitions and cultural programmes. It has run on 4-7 January each year since 1993. The 2027 dates are not announced yet.',
   'Department of Tourism, Government of Puducherry', 'puducherry', 'Puducherry (venue announced each year)', 'Puducherry', 'puducherry_area',
   '2027-01-04 00:00+05:30', '2027-01-07 23:59+05:30', 'Usually 4-7 Jan (2027 dates to be confirmed)', 'expected', null,
   'Open to the public; registration is online or by form (see the official page).',
   'https://pondytourism.py.gov.in/yoga', array['yoga','festival','wellbeing'], 'evt_yoga_festival_pondy'),

  -- ---------------- ART / MUSIC / DANCE ----------------
  ('event','art',
   'Serendipity Arts Festival, Panaji',
   'A large December arts festival across heritage buildings and riverfront spaces in Panaji: visual arts, crafts, theatre, music, dance, photography and programming for children. Dates for the 2026 edition are not published yet.',
   'Serendipity Arts Foundation', 'panaji', 'Venues across Panaji', 'Panaji, Goa', 'elsewhere_in_india',
   '2026-12-01 00:00+05:30', null, 'December 2026 (dates to be announced)', 'expected', null,
   'Open to all; check the official site for programme and any registration.',
   'https://serendipityartsfestival.com/', array['arts','festival','multi-art'], 'evt_serendipity_arts'),

  ('event','music',
   'Madras Music Season: Music Academy annual concerts',
   'Chennai''s December Carnatic music season. The Music Academy''s annual conference and concerts ran from 15 Dec 2025 to 1 Jan 2026 last year; the 2026-27 schedule is not published yet.',
   'The Music Academy, Madras', 'chennai', 'The Music Academy, Chennai', 'Chennai, Tamil Nadu', 'tamil_nadu',
   '2026-12-15 00:00+05:30', '2027-01-01 23:59+05:30', 'Mid-Dec 2026 to early Jan 2027 (dates to be confirmed)', 'expected', null,
   'Ticketed concerts; see the Academy''s site for sales and student options.',
   'https://musicacademymadras.in/', array['music','carnatic','festival'], 'evt_madras_music_season'),

  ('event','dance',
   'Indian Dance Festival, Mamallapuram',
   'A month-long festival of classical and folk dance staged against the Shore Temple at Mamallapuram, organised by Tamil Nadu Tourism. Last year it ran from 21 Dec to 19 Jan; the 2026-27 dates are announced each year.',
   'Department of Tourism, Government of Tamil Nadu', 'mamallapuram', 'Shore Temple complex', 'Mamallapuram, Tamil Nadu', 'tamil_nadu',
   '2026-12-21 00:00+05:30', '2027-01-19 23:59+05:30', 'Late Dec 2026 to mid/late Jan 2027 (dates to be confirmed)', 'expected', null,
   'Open to visitors; check the official page for timings and entry details.',
   'https://tamilnadutourism.tn.gov.in/events/indian-dance-festival', array['dance','festival','classical'], 'evt_mamallapuram_dance'),

  -- ---------------- VOLUNTEERING / COMMUNITY ----------------
  ('volunteering','volunteering',
   'Residential volunteering at Sadhana Forest, Auroville',
   'Year-round seva (selfless service) planting, mulching and watering trees and working on water conservation in a reforestation community. Volunteers commit to about 35 hours of seva a week. The community is vegan and substance-free for everyone, volunteers included.',
   'Sadhana Forest', 'auroville', 'Sadhana Forest, Auroville', 'Auroville, near Puducherry', 'puducherry_area',
   null, null, 'Year-round', 'ongoing', 'Ongoing: apply on the volunteer page',
   'Read the volunteer expectations on the official page before applying.',
   'https://sadhanaforest.org/volunteer/', array['volunteering','environment','substance-free'], 'evt_sadhana_forest'),

  ('volunteering','volunteering',
   'Volunteer with Auroville Village Action Group (AVAG)',
   'AVAG welcomes volunteers with skills such as fundraising, content writing, audio-visual communication, monitoring and evaluation, social research, marketing and training. A minimum commitment of 3 months is required. Apply by email as described on the page.',
   'Auroville Village Action Group', 'auroville', 'Villages around Auroville', 'Auroville area, near Puducherry', 'puducherry_area',
   null, null, 'Ongoing', 'ongoing', 'Ongoing: minimum 3 months',
   'Skills-based volunteers; see the page for how to apply.',
   'https://villageaction.in/get-involved', array['volunteering','community','skills'], 'evt_avag_volunteer'),

  ('club','skills',
   'FOSS United Pondicherry chapter',
   'The Pondicherry chapter of FOSS United, a community for free and open source software. Established in January 2025; follow the chapter page for meetups and volunteer roles.',
   'FOSS United, Pondicherry chapter', 'puducherry', 'Puducherry (venues vary)', 'Puducherry', 'puducherry_area',
   null, null, 'Community meetups (see the chapter page)', 'ongoing', 'Follow the chapter page for dates',
   'Open to anyone interested in open source.',
   'https://fossunited.org/c/pondicherry', array['tech','community','open-source','volunteering'], 'evt_foss_pondicherry'),

  ('volunteering','volunteering',
   'Robin Hood Army: Student Community Volunteer Drives',
   'A volunteer-driven, zero-funds movement by students and young professionals across India who distribute surplus food to people in need and run the Robin Hood Academy to teach children.',
   'Robin Hood Army', null, 'Local chapter drives across Indian cities', 'Pan-India chapters', 'elsewhere_in_india',
   null, null, 'Every weekend', 'ongoing', 'Weekly weekend drives',
   'Open to all students and volunteers. Join via the official chapter signup.',
   'https://robinhoodarmy.com', array['volunteering','food-drive','youth','social-impact'], 'act_robin_hood_army'),

  ('activity','sports',
   'Khelo India Campus Sports & Athletics Club',
   'National initiative by the Ministry of Youth Affairs and Sports to foster grassroots sports culture, university competitions, and fitness tracking across colleges in India.',
   'Sports Authority of India & MYAS', null, 'Colleges & SAI University training complexes', 'Nationwide', 'elsewhere_in_india',
   null, null, 'Ongoing seasonal leagues', 'ongoing', 'Regular campus training & regional meets',
   'College students enrolled in higher educational institutions.',
   'https://kheloindia.gov.in', array['sports','fitness','athletics','national'], 'act_khelo_india')
) as v(kind, category, title, description, organiser, city_slug, venue, location_text, region,
       starts_at, ends_at, date_label, date_confidence, recurrence, eligibility, link, tags, cover)
left join public.cities ci on ci.slug = v.city_slug
on conflict do nothing;
