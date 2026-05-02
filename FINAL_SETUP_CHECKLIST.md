# Final Setup Checklist

This file is for the parts that must happen on your machine/account after the code is complete.

## 1. Connect A Phone Or Emulator

Use this when you need runtime screenshots for location, camera, image picking, and notifications.

1. Install Android platform tools if `adb` is missing.
2. On your Android phone, open Settings.
3. Go to About phone.
4. Tap Build number 7 times to enable Developer options.
5. Go back to Settings, open Developer options, and enable USB debugging.
6. Connect the phone with a USB data cable.
7. Accept the RSA/debugging prompt on the phone.
8. In this repo, run:

```powershell
adb devices
```

9. Confirm the output shows a device id with `device`, not `unauthorized`.
10. If it shows `unauthorized`, unlock the phone, accept the prompt, and run `adb devices` again.

Optional emulator path:

1. Open Android Studio.
2. Open Device Manager.
3. Start an emulator with enough disk space.
4. Run `adb devices` and confirm it appears.

## 2. Add Firebase Credentials Safely

The app is already coded to build without Firebase credentials. Real Firebase proof starts when you add your own config.

1. Go to Firebase Console.
2. Create or open a Firebase project.
3. Add an Android app.
4. Use this package name:

```text
com.echoes.app
```

5. Download the generated `google-services.json`.
6. Place it here:

```text
app/google-services.json
```

7. Do not commit it. The repo already ignores `google-services.json`.
8. Rebuild:

```powershell
.\gradlew.bat assembleDebug --no-daemon
```

## 3. Enable Firebase Auth

1. In Firebase Console, open Authentication.
2. Click Get started if prompted.
3. Open Sign-in method.
4. Enable Email/Password.
5. Run the app.
6. Register with a test email/password.
7. Save screenshots of registration/login success.

## 4. Enable Firestore

1. In Firebase Console, open Firestore Database.
2. Create a database.
3. Choose a location.
4. Start in locked/production mode if prompted.
5. Open Rules.
6. Copy the contents of `firestore.rules` from this repo into the rules editor.
7. Publish the rules.
8. In the app, sign in with Firebase.
9. Create at least one capsule.
10. Open My Archive.
11. Tap Sync archive to Firestore.
12. In Firebase Console, confirm a document appears at:

```text
users/{yourFirebaseUid}/capsules/{capsuleId}
```

## 5. Capture Final Screenshots

Capture these after the phone/Firebase setup works:

1. Welcome readiness snapshot.
2. Firebase auth configured screen.
3. Registration or login success path.
4. Create capsule with validation visible.
5. Text plus image capsule created.
6. Archive list after app restart.
7. Archive filters and sort controls.
8. Detail screen with metadata.
9. Favourite and comment on an unlocked capsule.
10. Future time unlock notification.
11. Location permission prompt.
12. Location unlock success or denial handling.
13. Firestore document in Firebase Console.
14. Unit test report from `app/build/reports/tests/testDebugUnitTest/index.html`.
15. Final git log.

## 6. What Not To Commit

Do not commit:

- `app/google-services.json`
- keystores
- local passwords
- private Firebase screenshots that expose sensitive identifiers you do not want in the report

## 7. Quick Final Commands

Run these before the final report/export:

```powershell
git status --short --branch
.\gradlew.bat testDebugUnitTest --no-daemon
.\gradlew.bat assembleDebug --no-daemon
adb devices
git log --oneline --decorate -12
```
