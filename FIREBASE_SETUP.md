# Firebase Setup

`app/google-services.json` is intentionally not committed. The app still builds and runs in local-first mode without it, but Firebase Authentication and Firestore sync require your own Firebase project config.

## Configure Firebase

1. Open the Firebase Console.
2. Create or open a Firebase project.
3. Add an Android app with package name:

```text
com.echoes.app
```

4. Download `google-services.json`.
5. Place it at:

```text
app/google-services.json
```

6. Do not commit this file. It is ignored by Git.

## Enable Authentication

1. In Firebase Console, open Authentication.
2. Open Sign-in method.
3. Enable Email/Password.

## Enable Firestore

1. In Firebase Console, open Firestore Database.
2. Create a database.
3. Open Rules.
4. Copy the contents of this repo's `firestore.rules`.
5. Publish the rules.

## Verify Locally

Run:

```powershell
.\gradlew.bat assembleDebug --no-daemon
```

Then install/run the app, register a test account, create a capsule, open My Archive, and use the sync action. Firestore should show capsule records under:

```text
users/{firebaseUid}/capsules/{capsuleId}
```
