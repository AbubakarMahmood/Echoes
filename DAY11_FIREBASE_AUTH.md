# Day 11 Firebase Auth Notes

## What Changed

Day 11 adds an authentication entry screen before the existing capsule flow.

The screen supports:

- Firebase email/password registration
- Firebase email/password sign-in
- a local-only continue path so the Room-based coursework flow remains usable before Firebase config is added

## Firebase Config Boundary

`google-services.json` is intentionally ignored by Git because it is environment-specific project config. To enable real Firebase auth:

1. Create or open the Firebase project.
2. Register the Android app with package `com.echoes.app`.
3. Download `google-services.json`.
4. Place it at `app/google-services.json`.
5. Enable Email/Password sign-in in Firebase Authentication.

The Gradle build applies the Google Services plugin only when `app/google-services.json` exists, so the project still builds cleanly before that file is available.

## Verification

- `./gradlew.bat assembleDebug --no-daemon` passes.
- Runtime screenshots still need a connected emulator/device.
