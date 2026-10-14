# ANTIGRAVITY MASTER PROMPT v3 — Drug-Free India
### Redesign + illustrated image system + Supabase backend (MY Bharat VBYLD 2027 · Hack for Social Cause)

---

## 0. How you must work (read first)

You are a senior Android engineer, product designer, UI/UX specialist, accessibility expert and software architect.

1. **Inspect the existing repository before changing anything.** Identify framework, language, build setup, navigation, design system, state management, database, authentication, backend, the existing single-player game, and what is fully working, partly working, mocked or broken. Do not assume Compose, XML, Flutter, Firebase or Supabase until you have read the code. If the project is Kotlin + Jetpack Compose, continue with it.
2. **Modify the real project. Do not build a separate prototype.** Preserve working architecture, business logic, game logic, data models, navigation, and backend integrations wherever possible. Create a git branch or backup before touching critical files.
3. Post a **short implementation plan as an artifact**, then **execute it**. Do not stop at a plan, mockup or screenshots.
4. Work in the phases in section 17. After every phase run `./gradlew assembleDebug`, fix errors, run the app on the emulator, take screenshots of changed screens and compare them with the checklists.
5. Ask me nothing unless you are blocked. Where something is unspecified, choose a sensible default and note it in the report.
6. **Report honestly.** List what is finished, partial, mocked or untested. Never claim tests passed if they were not run.
7. Prefer a small number of excellent, working screens over many unfinished ones. No dead buttons, no lorem ipsum, no placeholder boxes.

---

## 1. Product

**App:** Drug-Free India. **Tagline:** *Say No. Build a Better You.* **Welcome-screen line:** *Healthier Minds. Safer Choices. Stronger Communities.*

**Mission:** help young people practise confident refusal skills, recognise misinformation, find trustworthy support, discover positive activities and join healthier communities. It must feel like a **trustworthy youth wellbeing product**, not a government portal, a childish game, a medical diagnosis app or a promotional site.

**Audience:** college students and young adults, youth-club members and volunteers, people at awareness programmes, anyone seeking reliable support information.

**Modules (bottom navigation, five items):** Home · Play · Help · Explore · Profile. **Track Impact** is NOT a sixth tab; it opens from Home and Profile.

**Design philosophy:** *Calm Health-Tech + Editorial Minimal + Subtle Gamification.* Warm, spacious, readable, understated. Avoid: grids of identical square tiles, too many floating cards, gradients on every screen, rainbow colours, thick shadows, oversized headings, repeated banners, fake notifications/events/activity/statistics, clinical-looking dashboards.

---

## 2. Backend: Supabase (already created)

| Item | Value |
|---|---|
| Project URL | `https://isrxwxwhkfdiyjofsmrt.supabase.co` |
| Project ref | `isrxwxwhkfdiyjofsmrt` |
| Publishable (client) key | `sb_publishable_Psy0EmnFkTsT3ggyjYqXXw_ZoNeASYS` |

**How to use these:**
- Put them in `local.properties` (git-ignored) and expose them through `BuildConfig` (`SUPABASE_URL`, `SUPABASE_PUBLISHABLE_KEY`). Commit a `local.defaults.properties` with placeholders. **Do not hard-code them in Kotlin source.** Also read `GOOGLE_WEB_CLIENT_ID` and `MAPS_API_KEY` (optional) the same way.
  ```
  SUPABASE_URL=https://isrxwxwhkfdiyjofsmrt.supabase.co
  SUPABASE_PUBLISHABLE_KEY=sb_publishable_Psy0EmnFkTsT3ggyjYqXXw_ZoNeASYS
  GOOGLE_WEB_CLIENT_ID=<from Google Cloud>
  MAPS_API_KEY=<optional>
  ```
- The publishable key is **public by design**. It is only safe because every table has Row Level Security. **Never** put a `secret` or `service_role` key in the app, in logs, or in the repo. If a library rejects the `sb_publishable_…` key format, update the library to the latest stable version first; the legacy anon key (Dashboard → Project Settings → API Keys → Legacy) is a fallback.
- Use **supabase-kt** (latest stable; check Maven Central / the GitHub releases for the current BOM and coordinates) with modules Auth, Postgrest and Storage, plus the Ktor OkHttp engine. If the project already uses another Supabase client, keep it.
- **I (the human) run the SQL.** The SQL is in `/supabase` (copy those files into the repo under `supabase/`): `01_schema.sql`, `02_rls_policies.sql`, `03_functions.sql`, `04_seed.sql`, `05_dev_demo_seed.sql` (demo only), `99_release_checks.sql`. Do **not** try to modify the live database from the app or from scripts. Read the SQL, build the Kotlin data layer to match it exactly, and tell me if you need any change (then write a new numbered SQL file, e.g. `06_….sql`, instead of editing old ones).
- If the existing project already has a Supabase schema, **inspect it first** and adapt. Never reset, drop or migrate data casually.

### 2.1 Data contract (what the app reads and writes)

| Feature | Supabase object | Access |
|---|---|---|
| Cities / colleges | tables `cities`, `colleges` | read (public) |
| Suggest a college | table `college_suggestions` | insert own (`user_id`, `city_id`, `name`, optional `note`); never shown as approved |
| Profile | table `profiles` (`full_name`, `phone`, `city_id`, `college_id`, `custom_college_name`, `language`, `include_in_college_stats`, `onboarding_completed`) | read/update own row. The row is auto-created at sign-up |
| Phone verified? | RPC `is_phone_verified()` | read-only boolean. **Never store or invent "verified"** |
| Helplines | table `helplines` | read (public, works before sign-in) |
| Support centres | table `support_centres` | read (public). Hide Call/Directions if `phone`/`address`/`lat,lng` are null |
| Activities / events / clubs | table `activities` (`status = 'published'`) | read; insert own as `pending` only |
| Join an activity | table `activity_signups` | insert/select/delete own |
| Mentors | table `mentors` | read verified+active only (none seeded; hide the tab if empty) |
| Stories | table `stories` (`scenes` JSONB) | read; cache in Room; bundled JSON as offline fallback |
| Play | RPCs `start_attempt(p_story, p_restart)`, `attempt_state(p_attempt)`, `submit_choice(p_attempt, p_scene, p_choice)`, `complete_attempt(p_attempt)` | **only way** to record scores |
| Home / Profile numbers | RPC `my_stats()` → `{scenarios_completed, scenarios_total, total_points, streak_days, practice_score, practice_score_delta, badges_earned, activities_joined_this_month}` | read |
| Trend chart | RPC `my_progress_series(p_months)` → `month, practice_score, answers, scenarios_completed` | read |
| Badges | tables `badges`, `user_badges` | read (awarding is server-side) |
| Leaderboard | RPC `college_leaderboard(p_city, p_limit)` | aggregates only; empty until the minimum cohort is met |
| Monthly survey | table `surveys` (read), table `survey_submissions` (read own), RPC `submit_survey(p_survey, p_answers)` | one answer per user per survey |
| Community / college impact | RPC `community_impact(p_college, p_survey)` → `{insufficient_data, min_cohort, month, responses, questions[]}` | aggregates only |
| Before/after self-check | table `self_assessments` (`kind` = `before` or `after`, `confidence` 1–5) | insert/select/delete own |
| Config | table `app_config` (`min_cohort`, `levels`, `privacy_policy_url`, `terms_url`) | read |
| Delete account | RPC `delete_my_account()` | then clear local data and sign out |
| Activity cover images | Storage bucket `activity-images` (public read) | read via `cover_image_path` |

**Trusted contacts are NOT in Supabase.** They stay on the device in encrypted storage (Android Keystore AES-GCM with Room or EncryptedSharedPreferences) and are never uploaded.

**Practice score** (shown on Home/Profile/Impact as "Practice score") = share of the user's last 20 scenario choices that were assertive (`calm_no`, `redirect`, `exit_plan`, `trusted_contact`). It is `null` until 5 choices exist, so show "Play a few scenarios to see your practice score". Always label it *"A practice indicator based on your scenario choices, not a clinical or behavioural measure."* Never present it as a measure of drug use or safety.

### 2.2 Authentication (do not fake it)

- Preserve any existing auth. Otherwise implement with Supabase Auth: **Google** (native Credential Manager sign-in with the Web client ID, exchanged for a Supabase session), **phone OTP** and **email OTP**.
- Phone OTP needs an SMS provider enabled in Supabase (in India this also needs DLT registration). If the provider is not enabled, show an honest message ("Phone verification isn't available right now. Continue with Google or email."). **Never mark a phone as verified without a successful OTP.** The verified state comes only from `is_phone_verified()`.
- Flow: Welcome → Create account (name + phone `+91`, or Continue with Google) → (Verify code if phone) → Location & college → Home. "Sign in" reuses the same methods. Persist the session securely and restore it on launch. Handle cancel, offline, expired code, resend (30 s cooldown), and rate limits with clear messages.
- Never log tokens, codes, phone numbers or emails.
- Deep link scheme `com.drugfreeindia.app://login-callback` for any browser-based OAuth fallback.

**Supabase dashboard steps for me (list them in your final report):** run SQL files in order; enable Google provider (Web client ID + secret); enable Email OTP; enable Phone provider only if an SMS provider is available; add the redirect URL above; add your debug and release SHA-1 to the Google Cloud Android client.

---

## 3. Reference design

I attach a **UI reference image** (save it as `docs/reference/ui_reference.png`). It shows 10 screens. Use it for layout, hierarchy, colour balance, typography, spacing and imagery. **Do not copy its errors**: its text is too small to read, one chip label is glitched, and several numbers are fake. Build a coherent, readable, responsive app.

What the reference shows:
1. **Brand sheet:** leaf-and-hands logo, "Drug-Free India", "Healthier Minds | Safer Choices | Stronger Communities", the four modules, script line "Say No. Build a Better You."
2. **Welcome:** logo + name + one-line tagline at the top; a **large illustration of students walking toward misty mountains at sunrise** filling the lower ~60% and fading into the background; teal "Get Started"; "Already have an account? Sign in".
3. **Create account:** back arrow; "Create your account"; helper text; Full name; Phone with a `+91` prefix box; teal "Next"; divider "Or continue with"; Google button; Terms & Privacy line.
4. **Where are you located:** city field with a pin; searchable college dropdown (Pondicherry University, NIT Puducherry, Sri Manakula Vinayagar Engineering College, Other); a tinted "Can't find your college? Suggest it" card.
5. **Home:** greeting + bell + avatar; **hero "Weekly challenge" card with an illustration of a young man and mountains**; a practice-score ring; a streak tile with M T W T F S S dots; a "Next activity" row with Join.
6. **Play & practise:** points chip and level; hero "Say No with Confidence"; "More games (coming soon)" with two locked cards.
7. **Gameplay:** close; "Scene 1 of 3"; points chip; segmented progress; **a wide scene illustration**; pressure bar + circular timer; narration line; dialogue card with speaker avatar; "What will you do?"; three answer rows (icon, title, subtitle, chevron); Submit.
8. **Get help:** "Quick exit" chip; hero "You are not alone" with a **hands-holding-a-sprout illustration**; list rows; "Private by design" note.
9. **Explore & connect:** segmented Activities/Events/Mentors; search; filter chips; "Happening in your city" with **illustrated cards with date badges**; "Popular clubs" list with avatars.
10. **Track impact:** segmented My progress/Community/Institution; survey card; practice-score ring; trend chart (May–Oct); small tiles. **Profile & settings:** avatar with initials, level progress, stats tiles, badges, account rows.

---

## 4. Design system

### 4.1 Colour tokens (semantic; no hard-coded colours in screens)

| Token | Hex |
|---|---|
| `background` | `#F6F8FA` |
| `surface` | `#FFFFFF` |
| `onSurface` (primary text) | `#0F1B2D` |
| `onSurfaceVariant` (secondary text) | `#617184` |
| `primary` (teal) | `#0E9F8E` |
| `primaryDark` | `#087F70` |
| `primarySoft` | `#DDF2EE` |
| `outline` (subtle border) | `#E3E9EE` |
| `surfaceMuted` | `#EEF2F5` |
| `warning` (restrained amber, only when needed) | `#B7791F` |
| `error` (muted red, only for genuine errors) | `#C0392B` |
| `success` | `#1E8E5A` |

Teal is the single brand accent. Add no other hues except inside illustrations. Provide a carefully designed **dark palette** only if the architecture makes it practical (otherwise light only); never simply invert colours.

### 4.2 Typography — Plus Jakarta Sans (bundled)

Bundle static TTFs (Regular 400, Medium 500, SemiBold 600, Bold 700, ExtraBold 800) in `res/font` with a system-sans fallback, and **verify on screen that the font is actually rendering** (the current build falls back to Roboto). Set `includeFontPadding = false`.

| Style | Size / line | Weight |
|---|---|---|
| Screen title | 28 / 34sp | Bold, −0.4sp |
| Section title | 18 / 24sp | SemiBold |
| Card title | 16 / 22sp | SemiBold |
| Body | 15 / 22sp | Regular |
| Body small | 14 / 20sp | Regular |
| Supporting / caption | 12–13 / 16–18sp | Medium |
| Button / tab label | 14–15 / 20sp | SemiBold |
| Stat number | 28–44sp | Bold/ExtraBold, tabular figures |

Tabs, buttons and chips are single-line (`maxLines = 1`, no wrapping). Everything must survive system font scale 1.3 and 1.5 without clipping or overlap.

### 4.3 Shape, spacing, elevation
- 4dp spacing grid; screen side padding 20dp; 12dp gap between cards; 28dp between sections.
- Radii: hero 24dp, cards 16–20dp, compact tiles 12dp, inputs 14dp, buttons 14dp (primary CTAs may be pill), chips 10dp. **Do not use one radius for everything.**
- Cards sit on the `background` with `surface` fill and either a 1dp `outline` border or a very soft shadow (blur 16, y 4, ≤8% navy). No heavy shadows. Use cards only where grouping or interaction needs them; prefer lists with dividers, editorial sections and whitespace.
- Minimum touch target 48dp. Respect status/navigation bar insets, the keyboard and display cutouts.

### 4.4 Bottom navigation (rebuild)
Five equal-width destinations, docked at the bottom, height 64dp + inset, `surface` with a 1dp top hairline. Icon + short label always visible. **Selected = filled icon + label in teal and a 3dp×20dp teal indicator above the icon. No expanding pill, no width changes, no floating bar.** Unselected = `onSurfaceVariant`. Content must never be hidden behind it (apply bottom padding everywhere). Preserve each tab's state and scroll position.

### 4.5 Iconography
One established icon set (Material Symbols Rounded, weight 500), 24dp. Icons aid recognition; do not decorate every line.

### 4.6 Motion
Subtle only: 150–250ms fades/slides, light springs on press, ring/bar fill on first view, count-up for stats. Respect system "remove animations" and the in-app "Reduce motion". Haptics only for selection and correct-answer feedback. No heavy confetti.

### 4.7 Reusable components
Buttons (primary/secondary/ghost), text and search fields, section heading, list row, meaningful card, scenario answer row, `ProgressRing` (centre content inside, no clipping), `Sparkline`, segmented tabs, chips, badge, avatar (initials), empty / loading / error states, confirmation dialog, bottom sheet, `AppImage` (see section 5). Use shared theme tokens only. Don't create a component for every trivial element.

---

## 5. Image system (illustrations in every key screen)

I want the app to look as beautiful as the reference: **illustrated images on the welcome screen, the home hero, the story scenes, help, explore and empty states.** Illustrations must be restrained and high quality, not stock photography.

### 5.1 How to produce the images
1. **If you have an image-generation tool, generate every image below** using the style bible and per-image prompts, review them against the checklist in 5.5, regenerate any that fail, and save them to the paths below.
2. **If you cannot generate images**, do both of these: (a) write `docs/image-prompts.md` containing the full prompt for every asset (copy the table below) so I can generate them in any image tool and drop the files in; (b) implement a **code-drawn vector fallback** for each key (Compose Canvas: layered hills, sun, simple flat characters, string lights, leaves) so the app is still beautiful and fully working. Filenames and keys stay identical, so my files replace the fallback automatically.
3. Do **not** invent remote image URLs, download random web images, or add image dependencies you don't need. Reuse valid existing assets in the repo first.

### 5.2 Style bible (prepend to every prompt)
> **STYLE:** Flat editorial illustration with soft gradients and a very subtle paper grain; calm, optimistic, premium; layered misty hills and gentle greenery; palette mostly teal `#0E9F8E`, deep teal `#087F70`, mint `#DDF2EE`, navy `#0F1B2D`, cream, with small warm accents (sunrise peach `#F6D9B8`, soft mustard) only in light and clothing; diverse Indian young adults aged 18–22 with friendly, simple faces and natural proportions; clean composition with open space for UI overlays; **no text, letters, logos or watermarks anywhere**.
> **NEGATIVE:** no substances, smoking, vaping, alcohol, pills, syringes or any paraphernalia, and no held objects in "peer pressure" scenes (use an inviting open hand only); no violence, fear, shame or stigma; no photorealism; no gore; no brand names; no real people or celebrities.

**Characters (keep identical across images):**
- **Arjun:** short black hair, teal zip jacket over a white tee, dark backpack, calm expression.
- **Rohan:** slightly taller, wavy hair, blue-grey shirt, confident friendly grin.
- **Meera:** long braid, mustard kurta, warm supportive smile.
- **Group of four students** (for Welcome): two women, two men, varied heights and clothing, backpacks, seen from behind or three-quarter back.

### 5.3 Asset manifest
Format **WebP** (lossy quality 80; use alpha where noted), saved in `app/src/main/res/drawable-nodpi/` with these exact lowercase snake_case names. Size targets: ≤150 KB each, `welcome_hero` ≤250 KB, total ≤6 MB. Priority: **P0** = needed for a competition demo, P1 = very desirable, P2 = nice to have.

| Key / filename | Size | Pri | Used on | Prompt (after STYLE) |
|---|---|---|---|---|
| `welcome_hero` | 1080×1440 (3:4) | P0 | Welcome | Four diverse young Indian students with backpacks walking together on a path toward misty green mountains at sunrise, seen from behind; layered hills fading to a pale mint-cream sky at the top so it blends into an off-white UI background. |
| `hero_challenge` | 720×720, alpha | P0 | Home hero, Play hero | Arjun in half profile, looking up with quiet confidence, a soft misty-mountain landscape behind him, rounded shape that sits at the bottom-right of a card. |
| `story_farewell_cover` | 720×480 | P0 | Play list, hero | Night terrace party with warm string lights and silhouettes of friends, Arjun in the foreground looking thoughtful. |
| `farewell_s1` | 1080×720 | P0 | Gameplay scene 1 | Rooftop farewell party at night with string lights and a small crowd; Rohan in the foreground, leaning in with a friendly but pushy expression and one open hand extended toward the viewer (empty hand, nothing held); Arjun partly visible at the edge. |
| `farewell_s2a` | 1080×720 | P0 | Gameplay | Arjun alone at the edge of the terrace, uneasy, party blurred and warm in the background, cool teal shadows around him. |
| `farewell_s2b` | 1080×720 | P0 | Gameplay | Arjun and Rohan face to face; Arjun calm with a polite open palm raised in a "no thanks" gesture, Rohan relaxed, a few friends in the soft background. |
| `farewell_s2r` | 1080×720 | P0 | Gameplay | Meera joining the group and pointing cheerfully toward the stairs; friends starting to turn that way; light, friendly mood. |
| `farewell_s2c` | 1080×720 | P0 | Gameplay | Arjun stepping aside from the crowd, looking at his phone screen (no readable text), city lights beyond the terrace. |
| `farewell_s3` | 1080×720 | P0 | Gameplay end | Arjun on the quiet rooftop later that night, relaxed and proud, calm skyline with a first hint of dawn. |
| `help_hero` | 600×450, alpha | P0 | Help hero, Get-help sheet | Two gentle hands cradling a small green sprout with a soft teal glow and tiny floating leaves. |
| `story_group_chat_cover` | 720×480 | P1 | Play list | Abstract phone screens with speech-bubble shapes (no readable text) and a friendly group of avatars. |
| `story_hostel_cover` | 720×480 | P1 | Play list | Hostel corridor in the evening with warm doors and two students talking. |
| `story_after_match_cover` | 720×480 | P1 | Play list | Students on a sports field at dusk after a match, sitting together. |
| `story_wedding_cover` | 720×480 | P1 | Play list | A family wedding courtyard with marigold garlands and relatives. |
| `story_senior_cover` | 720×480 | P1 | Play list | A confident senior student speaking to a first-year on campus steps. |
| `story_exam_cover` | 720×480 | P1 | Play list | A student studying at night with books and a desk lamp, calm teal light. |
| `help_friend` | 720×480 | P1 | Help a friend | Two young people sitting side by side on steps, one listening attentively to the other. |
| `onboarding_location` | 720×480 | P1 | Location & college | Stylised campus buildings with a large map pin and soft hills. |
| `cat_sports` | 800×500 | P1 | Explore fallback | Friends playing football on a green ground at golden hour. |
| `cat_art` | 800×500 | P1 | Explore fallback | Students painting at easels in a bright studio. |
| `cat_music` | 800×500 | P1 | Explore fallback | A small acoustic jam: guitar and drums in an amphitheatre. |
| `cat_dance` | 800×500 | P2 | Explore fallback | Students in a lively group dance rehearsal. |
| `cat_poetry` | 800×500 | P2 | Explore fallback | A student reading at an open-mic with a warm spotlight. |
| `cat_volunteering` | 800×500 | P1 | Explore fallback | Volunteers planting saplings in a park. |
| `cat_fitness` | 800×500 | P1 | Explore fallback | Sunrise yoga on a lawn. |
| `cat_skills` | 800×500 | P2 | Explore fallback | Students with cameras on a photo walk. |
| `cat_other` | 800×500 | P2 | Explore fallback | A group of friends on a campus lawn. |
| `impact_empty` | 640×480 | P1 | Impact empty state | A young plant growing from a tiny bar chart, soft teal tones. |
| `empty_activities` | 480×360 | P1 | Explore empty | An empty bench under a tree with a small flag, inviting. |
| `empty_centres` | 480×360 | P2 | Centres empty | A friendly map with a magnifier. |
| `empty_offline` | 480×360 | P2 | Offline state | A cloud with a pause symbol and a small plant. |

The logo mark (leaf and hands) already exists in the repo; reuse it for the splash, app icon and headers.

### 5.4 Image engineering
- `ImageAssets.kt`: an enum or map `key → @DrawableRes` plus alt text, resolved safely (`getIdentifier` fallback) so a **missing file never crashes the app**.
- `AppImage(key, modifier, contentScale, contentDescription)`: rounded clipping, a subtle gradient placeholder (`primarySoft → background`) with the category icon while loading or when missing, 200 ms crossfade. Decorative images use `contentDescription = null`; meaningful ones get the alt text from the table.
- Story JSON `imageKey` and `cover_image_key` values map to these keys (`farewell_s1`, `story_farewell_cover`, …). New stories can reuse `cat_*` or `story_*` fallbacks.
- **Core images are bundled (offline-first).** Only activity covers may come from Supabase Storage (`activity-images/<cover_image_path>`) via Coil, with the matching `cat_*` image as fallback.
- Keep the layout stable while images load (reserve aspect ratio). Never stretch; use `ContentScale.Crop` with sensible alignment.
- Do not put text over an image without a scrim that gives ≥4.5:1 contrast.

### 5.5 Image review checklist
Style consistent across all images; characters recognisably the same; no text/logos; no substances or paraphernalia; no stigma; clear space where UI overlays sit; file sizes within budget; every key referenced by code exists or has a vector fallback.

### 5.6 Where images appear (implement all)

| Screen | Images |
|---|---|
| Splash | Logo mark on `background` (Android 12 splash API), then a 300 ms fade into Welcome |
| Welcome | `welcome_hero` (lower ~60%, soft top fade), logo above |
| Create account / OTP | Logo mark only (keep forms clean) |
| Location & college | `onboarding_location` as a compact header (collapses when the keyboard opens) |
| Home | `hero_challenge` inside the weekly-challenge hero; small avatar initials; `empty_activities` if no upcoming activity |
| Play | `hero_challenge` (different crop) in the featured card; `story_*_cover` on each scenario row/tile |
| Gameplay | One scene illustration per scene (`imageKey`), full width, 24dp radius, ~28% of screen height |
| Result | No image; a clean medallion drawn in Compose |
| Help | `help_hero` in the hero card; `help_friend` in the Help-a-friend screen |
| Centres | A real map (see 8.4); `empty_centres` when no results |
| Explore | Event/activity cover from Storage or the `cat_*` fallback; `empty_activities` when empty |
| Track Impact | `impact_empty` before enough data exists |
| Profile | Initials avatar; badge icons are vector; no photo upload |
| Offline / error states | `empty_offline` |

---

## 6. Navigation and global behaviour
- Splash → (first launch) Welcome → account → location & college → Home; later launches go straight to Home when a session exists.
- Correct back behaviour everywhere; state preserved across tabs; deep links not required.
- **Quick exit** (Help screens only): replaces the screen with a neutral, ordinary-looking screen (for example a study planner) and clears the back stack. Label it "Quick exit"; do not promise it deletes history or guarantees privacy. It must be tested.
- Every visible control has a defined behaviour. Controls for unfinished features are removed or clearly labelled "Coming soon".
- Offline-first: Room caches cities, colleges, helplines, centres, stories, activities, config. Bundled JSON seeds (same content as `04_seed.sql`) let Help and Play work on first launch with no network. Show a discreet "Offline" indicator and sync when back online.

---

## 7. Screen specifications

### 7.1 Welcome
Logo, "Drug-Free India", *Healthier Minds. Safer Choices. Stronger Communities.*, `welcome_hero`, primary **Get Started**, "Already have an account? **Sign in**". No statistics, no banners.

### 7.2 Create account (and Sign in)
Fields: Full name, Phone with `+91` (country-code support), optional Email where the auth flow needs it. **Next** (sends the OTP when the phone provider is available), **Continue with Google**, "Already have an account? Sign in", Terms and Privacy links (URLs from `app_config`, show a placeholder page if null). Inline validation, keyboard-safe layout, loading/success/cancel/error states. OTP screen: 6 boxes, auto-advance, paste support, resend timer, "Change number".

### 7.3 Location & college
City field (list from `cities`, manual choice always works). Optional "Use my location" with an explanation first; use coarse location only to pick the nearest city on the device, **never send coordinates**. Searchable college dropdown filtered by city (`colleges`), plus **Other: enter college name** (saved privately in `profiles.custom_college_name`) and a **Suggest college** form that inserts into `college_suggestions` (handle the "already listed" and "5 pending" errors with friendly text). Pending suggestions are labelled "Pending review" and never appear as approved. Show a confirmation of the chosen city and college. Handle empty/loading/offline/error states. Never invent colleges or show a verified tick unless `is_verified = true`.

### 7.4 Home (calm, personal, not an analytics dashboard)
1. **Header:** small "Good evening" + first name, bell only if notifications exist, avatar.
2. **Hero:** "Say No with Confidence", "Small choices make a big difference.", short explanation, `hero_challenge`, primary **Start challenge** (opens the featured story). The strongest element on the screen, not the whole screen.
3. **Progress (compact, real data only from `my_stats()`):** practice-score ring (with the info sheet from 2.1), streak with a 7-day strip (only if `streak_days` is real), scenarios `x of y`. New users see "Start your first scenario" instead of zeros pretending to be progress.
4. **Next activity:** one upcoming published activity (title, organiser, date, place, **Join**). Show a "Demo" tag if `is_demo`. If none: `empty_activities` + "No upcoming activities yet." + **Explore**.
5. **Track Impact entry:** a small unobtrusive row linking to Track Impact.
No repeated grid of big square cards.

### 7.5 Play
**Landing:** "Play & Practise" / "Build your confidence for real-life moments."; points and level chip (level from `app_config.levels` and `total_points`); featured entry into the existing single-player game; scenario list from `stories` with cover images, duration and completion/best-score state; achievements strip. **"More games — Coming soon":** *Who's Spreading the Myth?* and *Pressure Battle*, locked, with a small "Coming soon" label; tapping shows a short message that the mode is planned. **Do not build multiplayer rooms, networking, timers or shared scoring.**

**Gameplay (preserve the existing game logic; present it like reference screen 7):**
- Top bar: close, "Scene 1 of 3", points chip; segmented progress below it (inside safe area).
- Scene illustration (`imageKey`), narration line, **dialogue card** with speaker initial avatar and the quote (narration and dialogue are separate fields; see story JSON `narration`, `speaker`, `quote`).
- **Pressure bar + circular timer.** The timer is a nudge only: when it ends, no choice is auto-picked and nothing is penalised; show "Take your time". Provide **Relaxed mode** (no timer) in settings and on the start sheet.
- "What will you do?" with 3–4 answer rows (title + subtitle + chevron), **not a grid of tiles**. The selected row is outlined in teal; **Submit** confirms. Feedback appears in place, constructive and non-shaming, with "why this helps", then **Continue**.
- Call `submit_choice` for every answer. Server-side scoring is authoritative; the UI may show the local result immediately and reconcile. Repeated taps never add points (the server returns `duplicate: true`). Never claim any response guarantees safety.
- **Resume:** if the app is backgrounded or killed, `start_attempt` returns the unfinished attempt and `attempt_state` says which scene is next.
- **Offline:** let the user play locally, keep the answers in a queue, and replay them through the RPCs in order when online; if the server rejects, keep the local result but do not count it toward leaderboards. Say "Saved on this device; will sync".
- **Results:** call `complete_attempt` only after the last scene. Show scenarios completed, genuine points, new badges (from the response), skills practised (from tags), **one** positive takeaway, **Replay** and **Back to Play**. Skipped scenarios are never "completed".
- No personal disclosure is required to play.

### 7.6 Get Help (clarity and speed; works without an account)
Title "Get Help", "Private and free. Choose what you need." **Quick exit** chip. Hero "You are not alone" with `help_hero` and **Talk to someone** (opens a short sheet: call a trusted contact, call a helpline, find a centre). List rows (icon, title, one-line description, chevron):
1. **Trusted contacts:** up to 5, device-only, encrypted; add/edit/delete; call or message (intent) with an editable gentle message. Never uploaded.
2. **Helplines:** from `helplines`; tap to call; show hours as stored; show a subtle "Unverified" label (debug builds) when `is_verified = false`. Emergency **112** is shown clearly with context and is the only place a red icon is allowed.
3. **Find support centres:** list + **real map** (Google Maps Compose when `MAPS_API_KEY` exists, otherwise OpenStreetMap through osmdroid in an `AndroidView`; never a fake grid). Filters Government/Counselling/De-addiction/Hospital/NGO; search by city/pincode/name. Detail sheet with address, hours, **Call** and **Directions** (only when data exists), and a "Sample listing" chip when `is_verified = false`. Header text must be honest (for example "21 listings", never "Verified" unless true).
4. **Help a friend:** practical, non-judgemental guidance (listen, don't lecture, encourage professional help, where to find it) with `help_friend`; content stored locally as markdown.
5. **Privacy & safety:** plain-language explanation of what is stored, where, and who can see it.
Footer note: "Your information stays on your device unless you choose to share it."
The app must not claim to dispatch emergency services, notify anyone, or imply a call was placed. Help is never conditional on game progress, and nobody is asked for drug-use history or labelled by health status.

### 7.7 Explore (activity feed, not a catalogue of cards)
Title "Explore & connect", "Discover healthy activities and opportunities near you." Segmented **Activities · Events · Mentors** (hide Mentors if none). Search "Search activities in your city", chips (All, Sports, Arts, Music, Volunteering, Fitness, …), city and college filters. Sections: **Happening in your city** (upcoming events with date badge and cover/`cat_*` image) and **Popular clubs** (`kind = club`, compact rows with avatar). **Do not show member or participant counts** (none are available and none may be invented). Detail screen: title, organiser, date/time or recurrence, venue, eligibility, description, registration link/contact, **Join** (inserts `activity_signups`; toggles to "Joined"). Submit-an-activity form (inserts as `pending`; show "Pending review" in "My submissions"; never present unreviewed listings as verified). Show a "Demo" tag on `is_demo`. Empty state: "No activities found in your area yet." with **Change city** and **Clear filters**; also loading and error with retry.

### 7.8 Track Impact (a calm progress report)
Title "Track Impact", "Understand what you're learning and how your community is participating." Segmented **My progress · Community · Institution**.
- **Survey card:** "Monthly pulse survey · Anonymous · 1 min · Start". Optional. Explain what is collected (answers only, no name, no phone, no timestamp). One question per screen using the survey JSON; submit via `submit_survey`. After submitting show "Thanks for completing this month's survey." and hide the card.
- **My progress:** practice score ring + delta (labelled as in 2.1), trend chart from `my_progress_series(6)` with month labels, tiles: scenarios `x of y`, activities joined this month, streak. Optional **before/after self-check** (confidence 1–5) from `self_assessments`; show "what was measured and when"; never equate better scores with reduced drug use.
- **Community / Institution:** call `community_impact`. If `insufficient_data`, show `impact_empty` and "Results appear once at least {min_cohort} people have answered, to protect privacy." Otherwise show simple bars per question with counts. Institution tab uses the user's `college_id`; below it an optional **college leaderboard** from `college_leaderboard` (aggregates, transparent scoring rule, "participation and learning", never shaming lower rows). No CSV export of individual data.
- **Never invent percentages, success rates or participant totals.** If a development build needs sample charts, put them behind a debug-only toggle with a visible "Sample data" label.

### 7.9 Profile & settings
Header: initials avatar, name, college and city, level only from the real level system with progress to the next level (`app_config.levels`). Stats: streak, badges, scenarios (real). **Achievements:** earned badges and locked ones (Team Player shows "Coming with multiplayer"); "See all". Settings in grouped lists with small section headers:
- **Account:** name, phone (shows **Verified** only if `is_phone_verified()`, otherwise "Not verified" + **Verify**), email, Google status.
- **College & location:** change city/college, manage location permission.
- **Support & privacy:** trusted contacts, privacy information, **Include my college in aggregate stats** (`include_in_college_stats`), data controls.
- **Preferences:** language (English and Hindi complete; other keys wired), notifications (only if implemented), Relaxed mode, Reduce motion.
- **Actions:** **Sign out**; **Delete account** (explain consequences, require typing DELETE, call `delete_my_account()`, then wipe local data and return to Welcome). Neutral grey switch off-state.

---

## 8. Content and accuracy rules (must follow)
1. Remove any invented statistic (for example "reduces compliance by over 60%"). Use neutral wording such as *"Practising what to say before the moment arrives makes it easier to say no when it counts."* Cite a source in data before showing any research number.
2. "Verified" appears only when `is_verified = true`. Seed data is unverified; label it "Sample listing" or "Unverified" instead.
3. Replace absolute claims ("100% private", "100% anonymous") with "Private by design" and "Anonymous, aggregated insights".
4. Do not claim the app prevents addiction or reduces drug use. Do not call users addicts or classify them by health status.
5. Before release every helpline number, centre name, hours and source must be verified against official sources (run `supabase/99_release_checks.sql` and list what remains in your report). Do not invent numbers.
6. No fake events, notifications, organisers, registration links, participant counts, mentors, streaks, achievements or impact figures. Development-only mocks must be impossible to mistake for real data.
7. Format numbers with thousands separators; use consistent units.

---

## 9. Privacy and safety
Collect only what is needed. Trusted contacts and exact location never leave the device. Survey answers are anonymous by design (the SQL already enforces it). Leaderboards and impact views show aggregates only above the minimum cohort. Users can opt out of college stats and can delete their account. Keep credentials out of source; never log personal data; users can only change their own private records (RLS enforces this). No imagery that glamorises or depicts drugs, paraphernalia or stigma.

---

## 10. Architecture and quality
- Keep presentation, domain and data separated per the existing conventions (MVVM / UDF with `UiState`, `UiEvent`, `UiEffect`; Hilt; Coroutines/Flow; Navigation Compose; Room for cache).
- Repository interfaces with Supabase and local implementations; DTO ↔ domain mapping; a single error mapper that turns Postgrest/Auth/network failures into friendly, localised messages with **Retry**.
- No work on the main thread; paginate lists; avoid unnecessary recomposition and queries; optimise image decoding.
- Accessibility: screen-reader labels (rings, charts and icon buttons get descriptions), 4.5:1 text contrast, no colour-only status, dynamic type, keyboard-safe forms.
- R8/ProGuard keep rules for serialization and Room. Lint clean. Min SDK 26, latest stable target.
- Do not add multiplayer, camera-based medical analysis, AI diagnosis or treatment advice.

---

## 11. Known defects in the current build (fix as part of the redesign)
- Plus Jakarta Sans is not rendering (falls back to Roboto).
- Bottom bar changes width per tab, floats over content and hides the last cards; add bottom padding and the docked bar from 4.4.
- Tab labels wrap to two lines ("Activities & Clubs", "Community View"); use single-line segmented tabs.
- Home: the confidence ring overlaps its label and is clipped; the tile has a large empty area.
- Gameplay: no scene image, narration and dialogue merged, big empty lower half, top progress line touches the screen edge.
- Result screen: large empty middle; invented "60%" statistic.
- Centres: the "map" is a placeholder grid; the header says "20+ Verified Centres (Sample Demo Data)"; "View Details →" wraps into the hours text.
- Explore: filter chips clipped on the right; Impact: ring without a number, chart without labels, "College Participation Roster" collides with "Aggregates only", points lack separators ("14250").
- Profile: badges row cut off; reduce-motion switch shows a pink off-state.

---

## 12. Scope control: do NOT build now
Multiplayer games (*Who's Spreading the Myth?*, *Pressure Battle*), room codes, real-time networking, shared scoring, voice chat, camera-based medical analysis, AI diagnosis/treatment advice, automated swelling detection. Show only the two locked "Coming soon" cards on Play.

---

## 13. Testing checklist (run what the project supports; report what could not be run)
Build succeeds and app launches · welcome and onboarding navigation · Google sign-in follows the real flow · phone verification never claims success before the OTP · manual city selection works without location permission · college search, "Other" and "Suggest college" work · pending suggestions are not shown as approved · all five tabs work and keep state · the game starts, resumes after backgrounding and completes · repeated taps do not add points (`duplicate: true`) · scenes cannot be answered out of order · `complete_attempt` fails before the ending · badges are awarded once · locked multiplayer cards do nothing · helplines and centres use stored data and correct `tel:` links · Explore handles empty/loading/error and Join/un-join · Impact shows an honest empty state below the cohort minimum · survey cannot be submitted twice · trusted contacts never touch the network (verify with a network inspector) · account deletion and sign-out work · keyboard never hides fields · no clipped text at font scale 1.5 on a 360dp-wide device · TalkBack labels · contrast · missing images fall back gracefully · offline launch works for Help and Play.

---

## 14. Final deliverables (report at the end)
1. The architecture you discovered. 2. Screens and components you changed. 3. How the design system and image system work. 4. Features preserved. 5. Features incomplete or needing backend/dashboard work. 6. Build and test results (honest). 7. Database or configuration changes still needed from me (SQL order, auth providers, SHA-1, keys). 8. How to run the app. 9. The list of image assets generated vs fallback-drawn. 10. Next recommended improvements.

---

## 15. Final quality standard
A carefully designed, modern youth-wellbeing product: strong visual identity, calm teal-and-neutral palette, excellent typography, clear hierarchy, distinct layouts for distinct tasks, beautiful consistent illustrations, a genuinely engaging single-player game, trustworthy and accessible help, realistic community features, transparent impact reporting, a polished five-item navigation, restrained cards, working controls, no fabricated data, no misleading claims. It must remain recognisably Drug-Free India and look professional at a national-level student innovation competition.

---

## 16. Reference story (keep the existing story data; this is the shape the app and the database use)
`stories.id = farewell_party`, scenes `s1` (Rohan: "Come on, everyone is trying it. Don't be boring!"; choices: Refuse confidently, Suggest another activity, Leave the situation, Go along just this once) → `s2b` / `s2r` / `s2c` / `s2a` → `s3` (ending, no choices, takeaway "Saying no is a skill, and skills improve with practice."). Each scene has `imageKey`, `narration`, `speaker`, `quote`, optional `timerSeconds`, `pressure`, and choices with `id`, `text`, `subtitle`, `points`, `tag`, `feedback`, `next`. The full JSON is in `supabase/04_seed.sql`; load the same content from a bundled JSON asset for offline use.

---

## 17. Phases (do them in this order)

**A. Audit** — inspect the repo, note risks, baseline build, list existing features and the current game's data/logic.
**B. Design foundation** — tokens, fonts (verify!), typography, shapes, spacing, icons, docked bottom bar, reusable components, `AppImage` and the image pipeline (generate images or write `docs/image-prompts.md` + vector fallbacks).
**C. Backend foundation** — `BuildConfig` secrets, Supabase client, Auth, repositories, DTOs, Room cache, bundled seed JSON, error mapper (match the SQL in `/supabase`).
**D. Onboarding** — splash, Welcome, Create account/Sign in, OTP, Location & college, suggestion flow.
**E. Home and Play** — Home, Play landing, gameplay, results, resume/offline queue, locked multiplayer cards.
**F. Help, Explore, Track Impact, Profile** — including the real map, activity submit/join, survey, impact empty states, settings, delete account.
**G. Polish** — images everywhere per 5.6, motion, accessibility, font-scale pass, content corrections (section 8).
**H. Verification** — run the checklist in section 13, screenshots at 360dp, Pixel-class and tablet widths (font scale 1.0 and 1.5), fix regressions, write the final report.

**Definition of done:** the app builds and runs; every flow in section 7 works against Supabase (and degrades gracefully offline); every key screen shows its illustration (generated or code-drawn); no fabricated data or misleading claims remain; the checklist in section 13 is reported honestly; the report in section 14 is delivered.

Begin by inspecting the repository, then post your plan and start Phase A.
