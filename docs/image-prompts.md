# Image System & Prompts Bible

## 1. Style Bible (Prepend to Every Prompt)

> Modern, thoughtful editorial 2D vector-style illustration, flat color with soft subtle gradients, warm and clean youth-wellbeing aesthetic. Clean lines, contemporary Indian college students (18–22 years old), diverse and authentic Indian clothing (casual kurtas, hoodies, college t-shirts, jeans). Calm, empowering, respectful atmosphere.
> 
> **Color palette:** primary deep teal `#0E9F8E`, warm off-white `#F6F8FA`, soft navy line accents `#0F1B2D`, gentle amber highlights.
> 
> **CRITICAL CONTENT RULES (STRICT):**
> - NEVER depict any drugs, pills, syringes, powder, smoke, vapes, rolled paper, alcohol, beer bottles, cocktail glasses, or red plastic cups.
> - Hand positions must be strictly natural: holding a textbook, notebook, smartphone, or hands casually resting in pockets or gestures.
> - No police, handcuffs, hospital beds, or gloomy/shaming imagery.
> - Composition must feel optimistic, relational, and clear.

---

## 2. Generated Asset Manifest & Prompts

| Key | Usage | Dimensions | Target Aspect Ratio | Format | Budget | Generated / Source Artifact |
|---|---|---|---|---|---|---|
| `welcome_hero` | Welcome Screen lower ~60% hero | 1080×1440 | 3:4 | WebP | ≤250 KB | `welcome_hero_1791976803352` (44.1 KB) |
| `hero_challenge` | Home weekly challenge card & Play featured | 720×720 | 1:1 | WebP | ≤150 KB | `hero_challenge_1791976841682` (11.3 KB) |
| `help_hero` | Help screen hero banner ("You are not alone") | 600×450 | 4:3 | WebP | ≤150 KB | `help_hero_1791977155606` (4.4 KB) |
| `farewell_s1` | Farewell party: Terrace scene, peer pressure | 1080×720 | 3:2 | WebP | ≤150 KB | `farewell_s1_1791976938029` (51.6 KB) |
| `farewell_s2a` | Reflection: Arjun stepping aside to talk | 1080×720 | 3:2 | WebP | ≤150 KB | `farewell_s2a_1791977030257` (28.7 KB) |
| `farewell_s2b` | Confident refusal: Calmly standing ground | 1080×720 | 3:2 | WebP | ≤150 KB | `farewell_s2b_1791976961411` (34.9 KB) |
| `farewell_s2r` | Alternative plan: Suggesting something fun | 1080×720 | 3:2 | WebP | ≤150 KB | `farewell_s2r_1791977069833` (50.5 KB) |
| `farewell_s2c` | Stepping away: Walking away or texting | 1080×720 | 3:2 | WebP | ≤150 KB | `farewell_s2c_1791977091485` (28.8 KB) |
| `farewell_s3` | Resolution: Leaving relaxed with friends | 1080×720 | 3:2 | WebP | ≤150 KB | `farewell_s3_1791977135119` (29.1 KB) |
| `story_farewell_cover` | Story Card cover: The Farewell Party | 720×480 | 3:2 | WebP | ≤150 KB | `story_farewell_cover_1791977186512` (20.9 KB) |
| `story_group_chat_cover` | Story Card cover: Group Chat Dare | 720×480 | 3:2 | WebP | ≤150 KB | `story_group_chat_cover_1791977238964` (23.5 KB) |
| `story_hostel_cover` | Story Card cover: Hostel Night | 720×480 | 3:2 | WebP | ≤150 KB | `story_hostel_cover_1791977259209` (15.0 KB) |
| `story_after_match_cover`| Story Card cover: After the Match | 720×480 | 3:2 | WebP | ≤150 KB | `story_after_match_cover_1791977282699` (19.4 KB) |
| `story_wedding_cover` | Story Card cover: Cousin's Wedding | 720×480 | 3:2 | WebP | ≤150 KB | `story_wedding_cover_1791977330075` (52.9 KB) |
| `story_senior_cover` | Story Card cover: The Cool Senior | 720×480 | 3:2 | WebP | ≤150 KB | `story_senior_cover_1791977351285` (29.3 KB) |
| `story_exam_cover` | Story Card cover: Stressed Before Exams | 720×480 | 3:2 | WebP | ≤150 KB | `story_exam_cover_1791977370807` (25.3 KB) |

---

## 3. Fallback Art System

For any screen or category without a generated raster image (such as `onboarding_location`, `empty_activities`, `empty_centres`, `impact_empty`, `cat_*` categories), the Compose `FallbackIllustration` component in `presentation/components/ImageAssets.kt` renders responsive programmatic vector art:
- `FallbackArt.HILLS`: Layered soft rolling hills with warm sunrise sun and teal horizon.
- `FallbackArt.LEAVES`: Vibrant stylized green/teal overlapping leaves symbolizing renewal.
- `FallbackArt.SUNRISE`: Warm optimistic morning glow and calming gradients.

---

## 4. Pipeline & Conversion

Images were generated, reviewed against the master prompt checklist, and converted to WebP format using `tools/convert_images.py`. All output images strictly adhere to the ≤150 KB (and ≤250 KB for welcome hero) budget and are placed in `app/src/main/res/drawable-nodpi/`.
