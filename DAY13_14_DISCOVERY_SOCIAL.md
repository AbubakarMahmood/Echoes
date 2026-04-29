# Day 13-14 Discovery And Social Notes

## Day 13 Flow

The main app flow now separates:

- personal archive for the local user's saved capsules
- discovery for capsules that are currently unlocked
- detail as the shared inspection/edit/social screen

This keeps the archive personal while giving unlocked capsules a separate browse path that can later be backed by Firestore discovery data.

## Day 14 Social Data

Favourites and comments are stored locally in Room:

- `favorites` records one favourite per capsule/user pair
- `comments` stores simple capsule comments with author, body, and timestamp

Social actions are blocked for locked capsules. Once a capsule is unlocked, it can be favourited and commented on from detail.

## Report Notes

This is still local-first. The social layer is intentionally Room-backed today, with Firestore sync left as a later extension once real Firebase config and rules are active.

Runtime screenshots still need a connected emulator/device.
