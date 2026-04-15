# Echoes 18-Day Build Plan

This plan is deliberately paced.

The goal is not "finish as fast as possible."
The goal is "finish steadily, understand what you are building, and never let the project become a panic pile."

Each day is outcome-based, not pass-based.
If something takes 1 pass or 10 passes, that does not matter.
The only question is whether that day's required outcome exists and works.

## How To Use This Plan

- Do not pull future work into today just because today went well.
- Do not start the next day until today's outcome is actually working.
- If a day slips, move the whole plan by a day and protect the later buffer/stretch work first.
- Keep `main` clean and use feature branches.
- Make at least 2 meaningful commits per day you work.

## Daily Definition Of Done

Every day should end with all 4 of these:

- the feature or artifact for the day exists
- it runs or is viewable without obvious breakage
- it is committed to git with sensible messages
- you saved proof for the report if relevant, such as screenshots, diagrams, notes, or code excerpts

## Day 1 - Project Skeleton

Must be done:

- create the Android project
- set min SDK to 26
- set up basic package structure
- add Navigation Component
- add Material theme
- confirm the app launches

Stop line:

- app opens to a placeholder home or welcome screen without crashing

Evidence to save:

- app launch screenshot
- initial package structure screenshot

Suggested commits:

- `chore: initialize android project`
- `chore: add base navigation and theme`

## Day 2 - Local Data Model

Must be done:

- define the core Room entities
- include user, capsule, and unlock-condition structure
- create the database class
- create the first DAO interfaces

Stop line:

- project builds with Room entities and DAOs in place

Evidence to save:

- ERD or entity table draft
- screenshot of entities and DAO structure

Suggested commits:

- `feat: add room entities for capsules and unlock conditions`
- `feat: add database and dao interfaces`

## Day 3 - Text Capsule Create Flow

Must be done:

- build the create capsule screen for text capsules
- allow save to Room
- validate required fields at a basic level

Stop line:

- you can create a text capsule and it is stored locally

Evidence to save:

- create form screenshot
- saved capsule visible in database inspector or app UI

Suggested commits:

- `feat: add text capsule creation form`
- `feat: save text capsules to room`

## Day 4 - Archive/List View

Must be done:

- build the personal archive list
- load saved capsules from Room
- show title, status, and timestamp
- support opening capsule detail

Stop line:

- a saved capsule appears in the archive after app restart

Evidence to save:

- archive screen screenshot
- short note proving persistence after restart

Suggested commits:

- `feat: add archive list screen`
- `feat: load local capsules from room`

## Day 5 - Edit And Delete

Must be done:

- open a saved capsule
- edit title/body
- delete capsule safely
- keep archive list in sync after edits

Stop line:

- full local text CRUD works end-to-end

Evidence to save:

- before/after edit screenshot
- delete flow screenshot

Suggested commits:

- `feat: add capsule detail and edit flow`
- `feat: implement delete for local capsules`

## Day 6 - Normalized Metadata

Must be done:

- expand the model with proper metadata
- include owner id, created timestamp, updated timestamp, unlock type, lock status
- update forms and storage logic accordingly

Stop line:

- existing CRUD still works with the richer schema

Evidence to save:

- updated ERD
- screenshot of metadata fields in code or UI

Suggested commits:

- `refactor: normalize capsule metadata model`
- `feat: persist unlock status and timestamps`

## Day 7 - Image Support

Must be done:

- add one extra media type
- choose image
- allow selecting or capturing an image
- store the local reference with the capsule
- show the image in detail view

Stop line:

- a capsule with text plus image can be created and reopened locally

Evidence to save:

- image capsule creation screenshot
- image shown in detail screen

Suggested commits:

- `feat: add image attachment support`
- `feat: display image capsules in detail view`

## Day 8 - MVVM Wiring

Must be done:

- move UI logic out of activities/fragments
- add ViewModels for the main flows
- expose state using StateFlow or LiveData
- keep repositories clearly separated from UI

Stop line:

- create, list, and detail flows work through ViewModels

Evidence to save:

- architecture diagram draft
- one code excerpt showing screen -> viewmodel -> repository

Suggested commits:

- `refactor: move capsule flows into viewmodels`
- `refactor: introduce repository layer`

## Day 9 - Time Unlock

Must be done:

- add future date/time unlock option
- store it in the unlock-condition model
- keep locked capsules visually distinct
- show unlock state in archive and detail

Stop line:

- a future-dated capsule is locked now and visibly recognized as such

Evidence to save:

- locked capsule screenshot
- code excerpt for time unlock evaluation

Suggested commits:

- `feat: add time-based unlock condition`
- `feat: show locked and unlocked capsule states`

## Day 10 - Filter And Sort

Must be done:

- filter locked vs unlocked
- filter my items or relevant archive groupings
- add at least one useful sort option, such as newest first

Stop line:

- archive view supports the expected enhanced browsing features

Evidence to save:

- filter screenshot
- sort screenshot

Suggested commits:

- `feat: add archive filtering by lock state`
- `feat: add capsule sorting options`

## Day 11 - Firebase Auth

Must be done:

- connect Firebase Authentication
- implement registration
- implement login
- keep the local app structure intact after auth arrives

Stop line:

- a user can register or sign in and reach the app successfully

Evidence to save:

- registration/login screenshots
- Firebase console screenshot if useful

Suggested commits:

- `feat: add firebase authentication`
- `feat: connect auth flow to app entry screens`

## Day 12 - Cloud Capsule Sync

Must be done:

- decide what cloud data is needed now
- mirror capsules to Firestore where appropriate
- keep Room as the local base
- sync at least the minimum shared capsule data successfully

Stop line:

- a locally created eligible capsule can be seen in Firestore

Evidence to save:

- local-first architecture note
- Firestore data screenshot

Suggested commits:

- `feat: add firestore capsule sync`
- `feat: keep room as local source of truth`

## Day 13 - Discovery And Personal Archive Polish

Must be done:

- separate personal archive from shared discovery if needed
- make unlocked/shared capsules browseable
- clean up navigation between main screens
- improve obvious UI rough edges

Stop line:

- the app has a coherent flow between auth, archive, discovery, and detail

Evidence to save:

- navigation screenshots
- short UI notes for report use

Suggested commits:

- `feat: add discovery flow for unlocked capsules`
- `refactor: improve main navigation structure`

## Day 14 - Favourites And Comments

Must be done:

- allow favouriting unlocked capsules
- allow basic comments on unlocked capsules
- make the data persist locally and/or sync if already wired

Stop line:

- one unlocked capsule can be favourited and commented on end-to-end

Evidence to save:

- favourite state screenshot
- comments screenshot

Suggested commits:

- `feat: add favourites for unlocked capsules`
- `feat: add comments to capsule detail`

## Day 15 - Location Unlock With Consent

Must be done:

- add location permission request only in context
- explain why location is needed
- verify location unlock in a user-triggered way
- keep the app usable if permission is denied

Stop line:

- a location-locked capsule can be checked with foreground permission and denial is handled cleanly

Evidence to save:

- permission explanation screenshot
- granted path screenshot
- denied path screenshot

Suggested commits:

- `feat: add consent-based location unlock flow`
- `fix: handle denied location permission gracefully`

## Day 16 - Notifications, Validation, Accessibility

Must be done:

- add capsule unlock notifications
- improve input validation
- review accessibility basics such as labels, contrast, content descriptions, and touch targets

Stop line:

- notifications trigger in at least one valid scenario and the core screens feel safer and more polished

Evidence to save:

- notification screenshot
- validation errors screenshot
- accessibility checklist notes

Suggested commits:

- `feat: add unlock notifications`
- `fix: improve validation and accessibility support`

## Day 17 - Performance, Testing, And Report Evidence

Must be done:

- move heavy work off the main thread if not already done
- add at least a small set of tests for core logic
- collect screenshots, diagrams, code excerpts, and Git evidence for the report
- fix the ugliest remaining bugs

Stop line:

- you have proof for performance, proof for testing, and a stable core app

Evidence to save:

- profiler screenshot or notes
- test output screenshot
- exported git log

Suggested commits:

- `test: add coverage for core unlock logic`
- `chore: collect report evidence and fix stability issues`

## Day 18 - Buffer Or Stretch Day

Default use of this day:

- fix anything unfinished from earlier days
- polish rough UX
- clean code
- improve documentation

Only if Days 1 to 17 are genuinely stable:

- add one stretch feature
- best option is visible offline-first sync state
- second-best option is a lightweight insights screen
- do not start audio unless the rest is already solid

Stop line:

- the app is submission-shaped, not just feature-shaped

Evidence to save:

- final flow screenshots
- final git log
- final branch summary

Suggested commits:

- `chore: polish project for submission`
- `feat: add final stretch enhancement`

## What Not To Do

- do not start audio early
- do not build admin tooling unless everything core is already done
- do not disappear into refactoring before the current day's outcome exists
- do not add libraries just because an AI mentioned them
- do not let a flashy stretch feature break the lower-band evidence

## The Calm Version Of This Plan

If you feel overloaded on any day, reduce the goal to the smallest working version of that day's outcome.

Examples:

- Day 7 image support can be gallery-only first
- Day 15 location unlock can be verification-button based, not background geofencing
- Day 16 notifications can start with one reliable notification path
- Day 18 can be pure cleanup with zero new features

That still counts as disciplined progress.

## My Recommended Priority Rule

If time gets tight, protect these in this order:

1. local Room CRUD
2. normalized model plus one extra media type
3. MVVM plus time unlock plus filtering/sorting
4. Firebase auth and cloud storage of relevant shared data
5. location unlock plus notifications plus validation
6. favourites/comments
7. stretch features

That order keeps the grade ladder intact.
