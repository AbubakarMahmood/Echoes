# Day 12 Cloud Capsule Sync Notes

## Local-First Decision

Room remains the source of truth for capsule creation, editing, archive browsing, filtering, and detail display. Firestore is introduced as an explicit mirror step from the archive screen, not as a replacement for local persistence.

That keeps earlier coursework evidence intact:

- capsules are still created locally first
- archive reads still come from Room
- filters and sort still run against local records
- cloud sync can fail without deleting or corrupting local data

## Firestore Shape

The Day 12 sync writes owner-private capsule documents to:

```text
users/{firebaseUid}/capsules/{capsuleId}
```

Each document stores the minimum capsule fields needed for a cloud mirror:

- capsule id, title, story text, media type, and image-presence flag
- local owner id plus Firebase owner uid
- unlock type, lock state, unlock timestamp, and satisfied timestamp
- created, updated, and synced timestamps
- schema version and private sync visibility marker

Image files are not uploaded yet. Day 12 only mirrors capsule records. Firebase Storage or another media-sync path can come later if needed.

## Security Boundary

No Firebase project config or service credentials are committed.

- `app/google-services.json` stays ignored and local to each developer.
- Client Firebase API keys identify the Firebase project; they are not admin secrets.
- Firestore access should be protected by Firebase Authentication plus rules.
- `firestore.rules` includes an owner-only rule sketch for the Day 12 document path.
- Service-account keys and Admin SDK credentials must never be placed in the Android app.

## Verification

- `./gradlew.bat assembleDebug --no-daemon` is the build gate.
- Runtime Firestore proof still requires `app/google-services.json`, Email/Password auth enabled, Firestore enabled, deployed rules, and a connected emulator/device.
