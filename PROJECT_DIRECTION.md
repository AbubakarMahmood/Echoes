# Echoes Project Direction

## What The Brief Actually Rewards

The brief is cumulative. That is the most important thing all three takes noticed correctly.

You do not get extra credit for jumping straight to flashy features if the lower-band evidence is weak or missing. The final build still needs to visibly contain:

- Room-based local persistence
- text capsule creation
- list/archive browsing
- CRUD
- Git history
- a normalized data model
- one extra media type
- MVVM with ViewModel plus LiveData or StateFlow
- time unlock
- filtering/sorting
- Firebase where appropriate
- location unlock
- notifications
- security/input validation
- Material/accessibility/performance work

That ladder is real. Build upward without deleting earlier evidence.

## What Each AI Got Right

### Claude

Best at:

- turning the idea into a full SRS
- giving you a serious requirements baseline
- mapping features, data model, testing, security, and milestones into one coherent document
- showing what a polished planning artefact can look like

Use Claude for:

- document structure
- requirement numbering
- section ideas for architecture, security, ethics, and testing
- data model inspiration

Do not copy Claude blindly because it over-scopes the app fast.

Main overreach in Claude:

- admin role and event-management screens
- analytics dashboard as if it is baseline rather than stretch
- both image and audio treated like normal scope
- Hilt, Paging, LeakCanary, Maps, detailed Firebase rules, exact dependency versions
- heavy "production system" behavior for what is still coursework

Claude is strongest as a planning reference, not as your literal scope contract.

### ChatGPT

Best at:

- staying closest to the brief
- separating safe first-class scope from risky 80-100 stretch
- warning against unapproved libraries and unnecessary framework inflation
- recommending image first, audio later
- treating map support as helpful but not automatically mandatory if it was not clearly taught or approved
- keeping the system student-project sized instead of enterprise sized

Use ChatGPT for:

- deciding what to build first
- reducing scope risk
- picking the safest architecture and feature order
- report framing around cumulative grading

This is the most practically useful take for execution.

### Gemini

Best at:

- naming the "ladder effect" clearly
- forcing the lower-band features to remain visible in the final app
- arguing for Room plus Firebase instead of Room or Firebase
- giving strong report advice around architecture comparison, ethics, and Git evidence

Use Gemini for:

- the cumulative-marking mindset
- report strategy
- the warning that version control evidence matters more than people think

Main overreach in Gemini:

- very confident wording where the brief is softer
- MPAndroidChart as if it is the obvious analytics answer
- AlarmManager + FCM combo stated too confidently
- Google Maps treated as more mandatory than the brief really proves

Gemini is useful as a strategic warning system, not as the final implementation spec.

## Combined Expert Opinion

If this were my submission, I would optimize for a strong 70-79 core that already looks deliberate and research-aware, then add one clean 80-100 extension only after the core is stable.

That means:

1. Keep the final app obviously cumulative.
2. Prefer fewer technologies used well over a giant stack used badly.
3. Treat Room as non-negotiable.
4. Add Firebase because the brief explicitly allows and rewards it.
5. Pick image as the required extra media type before audio.
6. Make location use foreground, explicit, and consent-based.
7. Use one serious stretch feature, not five half-finished ones.

## Recommended Scope

### Safe Core For A Strong First-Class Submission

- email/password auth
- user profile basics
- Room entities for users, capsules, unlock conditions, comments/favourites if needed locally
- text capsule CRUD
- image attachment
- archive/feed list
- time-based unlocking
- location-based unlocking
- filtering/sorting
- Firebase auth plus Firestore sync for shared capsules
- favourites and comments
- local or Firebase-backed notifications
- privacy explainer
- input validation
- Material-aligned UI with accessibility attention
- proof of performance work such as background threading, indexing, caching, or media compression

### Best 80-100 Extension Options

Pick one primary extension:

- offline-first sync with visible sync state or queued writes
- a simple analytics/visualization screen
- richer storytelling with audio as an optional advanced media path

My recommendation:

Make offline-first sync plus a clear architecture/report evaluation your main 80-100 move. It is closer to the brief, easier to justify academically, and more valuable than forcing a flashy chart or a bloated admin system.

If time remains, then add a lightweight analytics screen.

## What I Would Not Treat As Core

- admin panels
- moderation back-office
- Hilt unless it was clearly covered and you want the extra setup cost
- both image and audio in the first serious milestone
- a map-first UX if you do not already know the Maps SDK well
- exotic libraries just because an AI named them

## Tech Stack Recommendation

This is the stack I think makes the most sense from the brief plus the three takes:

- Kotlin
- Android SDK API 26+
- XML layouts with Material Components
- Navigation Component
- MVVM with ViewModel and StateFlow
- Room
- DataStore
- Firebase Authentication
- Firestore
- Firebase Cloud Messaging only where it is actually worth the setup
- WorkManager for sync/background tasks
- FusedLocationProviderClient
- image picker/camera support only if covered and needed

Conditional tools, not automatic defaults:

- Google Maps SDK
- Firebase Storage
- Coil or Glide
- CameraX
- audio recording APIs
- chart libraries
- Hilt

The rule is simple: if it was taught, approved, or clearly justified, use it. If not, keep the stack lean.

## Architecture Direction

Use a local-first setup:

- Room is the main local persistence layer
- repositories mediate local and remote data
- ViewModels expose UI state
- Firestore mirrors shared/discoverable data
- WorkManager handles retry/sync work

That gives you the cleanest answer to the brief's local storage plus Firebase plus advanced architecture requirements.

## Feature Order That Makes Sense

1. Local Room model, CRUD, and archive list
2. Image support and normalized unlock-condition data
3. MVVM refactor if not already done from the start
4. Time unlock plus filter/sort
5. Firebase auth and shared data
6. favourites/comments and discovery feed
7. location unlock
8. notifications, privacy, accessibility, performance polish
9. one stretch feature only after everything above is stable

This order matters more than most people realize. It stops the project from collapsing under feature debt.

## Report Angle

The report should not just describe features. It should argue for decisions.

Best argument path:

- local-only would satisfy early marks but is weak for community discovery
- cloud-first alone is risky for reliability and ignores the explicit Room requirement
- therefore local-first with cloud sync is the best fit for this coursework and this campus use case

That is the strongest combined point across the three takes and the one most worth carrying into the report.

## Git Direction

Use Git like an actual development log, not a last-minute backup.

Recommended approach:

- keep `main` stable
- use short-lived feature branches
- merge back only when a feature works end-to-end
- commit at meaningful checkpoints with real messages

Examples:

- `chore: initialize android project`
- `feat: add room capsule entity and dao`
- `feat: implement text capsule crud`
- `feat: add date-based unlock logic`
- `feat: add firebase auth flow`
- `feat: add favourites and comments`
- `fix: handle denied location permission`
- `docs: add architecture and data model notes`

## Bottom Line

Claude gave the richest raw material.
ChatGPT gave the safest implementation judgment.
Gemini gave the strongest warning about cumulative marking and Git evidence.

The best combined stance is:

- build a lean but complete cumulative app
- keep Room at the center
- use Firebase as an enhancement, not a replacement
- choose image before audio
- keep location consent-based and non-creepy
- save stretch work for after the first-class baseline is genuinely done
