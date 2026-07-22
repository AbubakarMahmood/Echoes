# Echoes

Echoes is a Kotlin Android time-capsule app. It lets a user create personal memory capsules, store them locally, unlock them by time or location, browse their archive, and optionally mirror capsule records to Firebase.

The project is intentionally local-first: Room is the source of truth, while Firebase Authentication and Cloud Firestore are used as an authenticated sync layer when project credentials are supplied locally.

## Features

- Create text capsules with title, story, visibility, and unlock metadata.
- Attach images from the gallery or camera and store them as app-managed local files.
- Browse a personal archive backed by Room persistence.
- View, edit, and delete saved capsules.
- Filter and sort archive records by lock state, content type, and date.
- Unlock capsules immediately, by future date/time, or by foreground location check.
- Schedule local notifications for future time unlocks.
- Sign in or register with Firebase Authentication when Firebase config is available.
- Sync local capsule records to owner-private Firestore paths.
- Discover unlocked capsules and interact with favourites and comments.
- View Archive Insights for local capsule totals, lock state, media split, unlock type distribution, and recent activity.

## Tech Stack

- Kotlin
- Android SDK 35, minimum SDK 26
- Room with KSP
- MVVM with ViewModel and StateFlow
- Material Components
- Navigation component
- Firebase Authentication
- Cloud Firestore
- Gradle wrapper

## Architecture

Echoes follows a simple layered Android architecture:

```text
Fragments
  -> ViewModels
    -> Repositories
      -> Room DAOs / Firebase SDK / Android services
```

Fragments handle UI events, permissions, navigation, image picking, camera capture, and location requests. ViewModels hold screen state and expose events using Kotlin flows. Repositories coordinate database writes, image storage, unlock resolution, notification scheduling, and cloud sync. Domain rule objects keep validation and unlock checks testable without Android UI dependencies.

Room stores users, capsules, unlock conditions, comments, and favourites as separate tables. This keeps capsule content, unlock metadata, and social state independent instead of flattening everything into one record.

## Firebase Configuration

Firebase credentials are not committed to this repository.

The app builds and runs in local-first mode without Firebase config. Firebase Authentication and Firestore sync require your own `google-services.json` file at:

```text
app/google-services.json
```

That file is ignored by Git. See [FIREBASE_SETUP.md](FIREBASE_SETUP.md) for the exact setup steps, including Authentication, Firestore, and rules configuration.

Firestore rules for this project are provided in [firestore.rules](firestore.rules). Capsule records are written under owner-private paths:

```text
users/{firebaseUid}/capsules/{capsuleId}
```

## Build and Test

Use the included Gradle wrapper from the project root.

```powershell
.\gradlew.bat assembleDebug --no-daemon
```

Run unit tests:

```powershell
.\gradlew.bat testDebugUnitTest --no-daemon
```

Install on a connected Android device:

```powershell
adb devices
.\gradlew.bat installDebug --no-daemon
```

## Project Structure

```text
app/src/main/java/com/echoes/app
  data/
    auth/       Firebase auth session handling
    cloud/      Firestore sync repository and sync result model
    local/      Room database, DAOs, entities, and models
    repository/ Capsule repository for local persistence workflows
  domain/       Validation and unlock rules
  notifications/Local unlock notification scheduling and receiver
  ui/           Fragments and ViewModels for app screens
  util/         Image storage, metadata formatting, dates, and location helper
```

## Security and Privacy Notes

- `app/google-services.json` is intentionally ignored.
- Location use is foreground-only and user-triggered.
- Notification permission is requested only when a future unlock can notify the user.
- Firestore sync is owner-private and guarded by security rules.
- Local images are stored on-device and are not uploaded to Firebase Storage in this version.

## Current Scope

This is a personal project, not a production deployment. The implemented scope focuses on local persistence, Android architecture, unlock mechanics, Firebase integration, and privacy-aware defaults.

## Repository Boundary

This public repository contains the independently implemented application and its operational documentation. Third-party briefs, grading material, submission evidence, and private service configuration are intentionally excluded. Dependencies and generated Gradle wrapper files remain governed by their own upstream licenses.

Known future improvements include WorkManager-backed background sync, Firebase Storage image backup, conflict handling, richer analytics visualisation, and public discovery moderation.
