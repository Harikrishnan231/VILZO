# Drug-Free India: Design System Specification

## 1. Overview & Philosophy
The **Drug-Free India** visual identity adopts a clinical, premium, white-first aesthetic. It prioritizes psychological safety, clarity, and trust over flashy neon gamification.

- **White-First**: Primary backgrounds (`#FFFFFF`) and clean card surfaces (`#FFFFFF`).
- **Single Accent**: `#0E9F8E` (Calm Teal). Only semantic accents (Success `#1E9E6A`, Caution `#C98A1B`, Emergency Danger `#C8483F`) are permitted in localized UI elements.
- **Hairline Borders**: 1dp `#E6EBF0` borders define spacious cards rather than heavy dark shadows.

---

## 2. Color Palette Tokens

| Token | Hex Value | Usage Description |
|---|---|---|
| `DfiBackground` | `#FFFFFF` | Primary screen backgrounds |
| `DfiSurface` | `#FFFFFF` | High-level container cards and surfaces |
| `DfiSurfaceMuted` | `#F6F8FA` | Section headers, input backgrounds, chips |
| `DfiSurfaceTint` | `#EEF7F5` | Highlight card surfaces and active tab indicator |
| `DfiOutline` | `#E6EBF0` | 1dp clean boundary lines |
| `DfiInk` | `#0F1B2D` | Primary navy typography |
| `DfiInkSecondary` | `#5A6B7B` | Secondary explanatory copy |
| `DfiInkTertiary` | `#8A98A6` | Captions, timestamps, disabled indicators |
| `DfiAccent` | `#0E9F8E` | Active actions, progress rings, primary buttons |
| `DfiAccentDark` | `#0B7D70` | Text on tinted cards, pressed states |
| `DfiAccentSoft` | `#DFF3F0` | Ring background tracks, chip surfaces |
| `DfiSuccess` | `#1E9E6A` | Correct refusal options, mastery badges |
| `DfiCaution` | `#C98A1B` | High pressure meter warnings |
| `DfiDanger` | `#C8483F` | Dedicated strictly to Medical Emergency (112) |

---

## 3. Typography Scale (Plus Jakarta Sans)
- **Display**: 32sp / 40sp SemiBold, -0.5sp tracking
- **Title Large**: 24sp / 32sp SemiBold, -0.3sp tracking
- **Title Medium**: 20sp / 28sp SemiBold, -0.2sp tracking
- **Title Small**: 16sp / 24sp SemiBold
- **Body Large**: 16sp / 24sp Regular
- **Body Medium**: 14sp / 20sp Regular
- **Label**: 12sp / 16sp Medium
- **Stat Metric**: 36sp / 40sp Bold (Tabular numerals)

---

## 4. Spacing, Radii & Motion
- **Corner Radii**: Cards 28dp, Buttons 20dp, Primary Pill 100dp, Chips 12dp, Inputs 16dp, Bottom Sheets 32dp.
- **Elevation**: 0dp base with 1dp border, soft navy tint shadow on floating pill bars.
- **Motion**: Custom Spring with `dampingRatio = 0.8f` and `stiffness = 380f`.
- **Button Press Scale**: 0.97f with haptic tick.
