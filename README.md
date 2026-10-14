# Drug-Free India (Android App)

**Tagline:** Stronger Youth | Healthier Tomorrow  
**Message:** Not just awareness. Real support. Better choices. A brighter future.  
**Package:** `com.drugfreeindia.app`  

---

## 1. Architectural Architecture & Layers

```
                     ┌──────────────────────────────────────┐
                     │          Presentation Layer          │
                     │  (Jetpack Compose, Material 3,       │
                     │   Unidirectional UiState/UiEvent)    │
                     └──────────────────┬───────────────────┘
                                        │
                                        ▼
                     ┌──────────────────────────────────────┐
                     │             Domain Layer             │
                     │  (Clean Architecture Use Cases,      │
                     │   Models, Repository Contracts)      │
                     └──────────────────┬───────────────────┘
                                        │
                     ┌──────────────────┴───────────────────┐
                     ▼                                      ▼
       ┌───────────────────────────┐         ┌───────────────────────────┐
       │     Room Local Database   │         │  EncryptedSharedPreferences│
       │  (Offline-First Seeding,  │         │   (Android Keystore       │
       │   Stories, Surveys, etc.) │         │    Trusted Contacts)      │
       └───────────────────────────┘         └───────────────────────────┘
```

---

## 2. Setup & Build Instructions

### Prerequisites
- JDK 17 or JDK 21
- Android SDK 35 (`compileSdk = 35`, `minSdk = 26`)
- Android Gradle Plugin 8.7.3

### Build Debug APK
```bash
./gradlew assembleDebug
```

### Run Unit Tests
```bash
./gradlew test
```

---

## 3. How to Add a New Scenario JSON

1. Create a new JSON file under `app/src/main/assets/stories/story_<name>.json` following this schema:
```json
{
  "id": "campus_canteen",
  "title": "Campus Canteen Pressure",
  "pack": "Campus",
  "estimatedSeconds": 30,
  "scenes": [
    {
      "id": "s1",
      "speaker": "Classmate",
      "lines": ["Hey, take a puff before the next lecture."],
      "timerSeconds": 10,
      "pressure": 0.6,
      "choices": [
        {
          "id": "a",
          "text": "No thanks, I have an assignment due.",
          "points": 8,
          "tag": "exit_plan",
          "feedback": "Great exit line that avoids confrontation.",
          "next": "s2",
          "badgeEarned": "exit_plan"
        }
      ]
    }
  ]
}
```
2. Register the path in `DatabaseInitializer.kt` under `seedStories()`.
3. The database will automatically parse and display the new simulation upon next launch.

---

## 4. Helpline Verification Policy

> **IMPORTANT NOTICE:**  
> All national helpline numbers in `app/src/main/assets/helplines.json` (such as MANAS 1933, MSJE 14446, Tele-MANAS 14416, and ERSS 112) are verified from official government portals.
> **Before any public production release or state-wide distribution, administrators must verify and confirm that numbers are active in all target telecom circles.**

---

## 5. Enabling Remote Mode (Supabase / Cloud Sync)

By default, **Drug-Free India operates 100% offline-first** with zero external network dependencies (`BuildConfig.USE_REMOTE = false`).

To enable remote synchronization:
1. Set `USE_REMOTE = true` in `app/build.gradle.kts` defaultConfig.
2. Provide your credentials in `local.properties`:
```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
MAPS_API_KEY=your-google-maps-api-key
```
3. Rebuild the application. The repository interfaces will automatically toggle remote sync while retaining local Room cache as the single source of truth.
