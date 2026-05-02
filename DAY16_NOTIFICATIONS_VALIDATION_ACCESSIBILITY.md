# Day 16 - Notifications, Validation, Accessibility

## Outcome

Day 16 adds the first reliable notification path and tightens the core user-facing screens without changing the local-first architecture.

## What Changed

- Added a local notification channel named `Capsule unlocks`.
- Added a broadcast receiver and scheduler for future time-unlock capsules.
- Time-locked capsules now schedule an Android alarm when saved.
- Android 13+ notification permission is requested only when saving a future time-locked capsule.
- If notification permission is denied, the capsule still saves and still unlocks in the archive.
- Deleting a capsule cancels any pending unlock notification for that capsule.
- Create/detail validation now trims input and checks title, story, and comment maximum lengths.
- Firebase auth validation now uses Android email validation and bounded password/display-name lengths.
- Core screens gained accessibility headings, polite live regions for changing status text, counters, and clearer control descriptions.

## Validation Rules

- Capsule title: 3 to 80 characters.
- Capsule story: 10 to 2,000 characters.
- Comment: 2 to 300 characters.
- Firebase password: 6 to 128 characters.
- Firebase display name: 2 to 40 characters.

## Accessibility Checklist

- Main screen titles are marked as accessibility headings.
- Dynamic state text such as sync state, lock state, image attachment state, and unlock status uses polite live-region announcements.
- Text fields expose counters where a maximum length matters.
- Archive filter and sort groups have descriptive accessibility labels.
- Existing image previews keep content descriptions.
- Location and notification permissions are still contextual, not requested at app launch.

## Evidence To Save

- Screenshot of the notification permission prompt after saving a future time-locked capsule on Android 13+.
- Screenshot of a delivered capsule unlock notification.
- Screenshot of title/story validation errors.
- Screenshot or note from TalkBack/Accessibility Scanner showing headings and labelled controls.

## Boundary Notes

- The notification path is local-device only and scheduled from Room-backed capsule creation.
- Alarm delivery may be deferred by Android power management because the app does not request exact-alarm privileges.
- Runtime notification proof still needs an attached device or emulator.
