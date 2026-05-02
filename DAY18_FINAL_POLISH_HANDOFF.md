# Day 18 - Final Polish And Handoff

## Outcome

Day 18 keeps the project submission-shaped instead of chasing a risky late feature. The app now shows a final readiness snapshot on the welcome screen and the repo has a concrete setup checklist for the remaining device/Firebase proof.

## Final Polish Added

- Updated the welcome screen to the Day 18 checkpoint.
- Added an in-app readiness snapshot showing:
  - Room/local storage as the source of truth.
  - Firebase/Firestore as a credential-gated mirror.
  - runtime screenshots as device-gated.
  - unit tests and debug build as completed proof.
- Added `FINAL_SETUP_CHECKLIST.md` so the remaining setup tasks are clear and repeatable.

## Final Verification

Run before submission:

```powershell
.\gradlew.bat testDebugUnitTest --no-daemon
.\gradlew.bat assembleDebug --no-daemon
adb devices
```

Expected code proof:

- Unit tests pass for core validation and unlock rules.
- Debug APK builds.
- `adb devices` lists a real device or emulator before runtime screenshots are attempted.

Results from this Day 18 pass:

- `testDebugUnitTest`: `BUILD SUCCESSFUL in 40s`
- `assembleDebug`: `BUILD SUCCESSFUL in 19s`
- `adb devices`: no attached device/emulator listed

## Remaining Honest Boundaries

- Firebase Auth and Firestore are implemented, but live proof still needs `app/google-services.json` from your Firebase project.
- `google-services.json` is intentionally ignored by git and should not be committed.
- Location, camera, image picker, and notifications need an attached phone/emulator for screenshots.
- Firestore rules exist in `firestore.rules`, but they still need to be deployed or copied into Firebase Console.

## Suggested Final Evidence Pack

- Welcome readiness snapshot screenshot.
- Auth screen screenshot showing Firebase configured.
- Register/login screenshots.
- Create capsule screenshot with validation.
- Archive screenshot with filters/sort.
- Detail screenshot with image, favourite, and comment.
- Time unlock notification screenshot.
- Location permission and location unlock screenshots.
- Firestore document screenshot under `users/{uid}/capsules/{capsuleId}`.
- Unit test report screenshot from `app/build/reports/tests/testDebugUnitTest/index.html`.
- Final git log screenshot.
