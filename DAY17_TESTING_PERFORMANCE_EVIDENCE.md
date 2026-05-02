# Day 17 - Performance, Testing, And Report Evidence

## Outcome

Day 17 moved the most grade-useful logic into testable domain rules, added JVM unit tests, verified the debug build, and collected evidence notes for the final report.

## Testing Proof

Commands run:

```powershell
.\gradlew.bat testDebugUnitTest --no-daemon
.\gradlew.bat assembleDebug --no-daemon
```

Results:

- `testDebugUnitTest`: `BUILD SUCCESSFUL in 52s`
- `CapsuleInputRulesTest`: 3 tests, 0 failures, 0 errors
- `CapsuleUnlockRulesTest`: 3 tests, 0 failures, 0 errors
- `assembleDebug`: `BUILD SUCCESSFUL in 20s`

Generated test report:

- `app/build/reports/tests/testDebugUnitTest/index.html`
- `app/build/test-results/testDebugUnitTest/TEST-com.echoes.app.domain.CapsuleInputRulesTest.xml`
- `app/build/test-results/testDebugUnitTest/TEST-com.echoes.app.domain.CapsuleUnlockRulesTest.xml`

## Performance Notes

- Room/database, image import, location unlock, social, delete, and cloud-adjacent repository work remains inside coroutine `Dispatchers.IO` boundaries.
- Validation and unlock decision logic now lives in pure domain objects instead of being duplicated directly inside ViewModels and repository branches.
- The new domain rules avoid Android framework calls, which keeps the fast unit tests on the JVM and avoids needing an emulator for core logic.
- Location distance calculation still uses Android's `Location.distanceBetween`, but the radius decision is now testable separately through `CapsuleUnlockRules.isLocationWithinRadius`.

## Stability Fix

Fixed a notification edge case from Day 16:

- If a future time-locked capsule title is edited before it unlocks, the pending unlock notification is refreshed so it uses the updated title.

## Runtime Evidence Boundary

`adb devices` was checked after the build:

```text
List of devices attached
```

No device/emulator was attached, so runtime screenshots and notification screenshots are still not honestly available from this machine state.

## Git Evidence Snapshot

```text
b4238e9 (HEAD -> feat/day17-testing-performance-evidence) test: add coverage for core unlock rules
4b62278 (origin/feat/day16-notifications-validation-a11y, feat/day16-notifications-validation-a11y) feat: add unlock notifications and accessibility polish
e5d2e48 (origin/feat/day15-location-unlock, feat/day15-location-unlock) feat: add consent-based location unlock
b19dd53 (origin/feat/day13-14-discovery-social, feat/day13-14-discovery-social) feat: add discovery favorites and comments
7e908ee (origin/feat/day12-cloud-capsule-sync, feat/day12-cloud-capsule-sync) feat: add firestore capsule sync
c742267 (origin/feat/day11-firebase-auth, feat/day11-firebase-auth) feat: add firebase auth entry flow
98473d0 (origin/feat/day10-filter-sort, feat/day10-filter-sort) feat: add archive filter and sort controls
2abb509 (origin/feat/day9-time-unlock, feat/day9-time-unlock) feat: add time unlock support
6efb5b4 (origin/feat/day8-mvvm-wiring, feat/day8-mvvm-wiring) refactor: wire capsule screens through viewmodels
a9d0b69 feat: add capsule repository and viewmodels
```
