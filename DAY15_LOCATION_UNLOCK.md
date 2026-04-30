# Day 15 Location Unlock Notes

## Consent Boundary

Location is foreground-only and user-triggered.

- Create requests location only when the user taps `Use current location as unlock spot`.
- Detail requests location only when the user taps `Check my location`.
- Denial keeps the app usable; the capsule stays locked and the user gets a clear message.

## Data Shape

The existing `unlock_conditions` table already had location columns, so Day 15 uses:

- `conditionType = LOCATION`
- `latitude`
- `longitude`
- `radiusMeters`
- `satisfiedAt` after a successful check

Room remains the local source of truth. Firestore mirroring now includes the location unlock fields as part of the capsule record.

## Unlock Behaviour

Location capsules are created locked. The app compares the current foreground location against the saved target radius only when the user manually checks the capsule. If the user is close enough, Room updates the capsule to unlocked and records `satisfiedAt`.

## Verification

- Build gate: `./gradlew.bat assembleDebug --no-daemon`
- Runtime screenshots still need a connected emulator/device with location services available.
