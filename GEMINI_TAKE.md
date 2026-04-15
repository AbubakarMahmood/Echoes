This is an excellent and highly strategic prompt. You have perfectly identified the most common and dangerous trap in university marking rubrics: \*\*The Ladder Effect\*\*. 



In cumulative rubrics like this one, if you jump straight to a top-tier feature (like Firebase Cloud Storage) and discard the lower-tier requirement (like Room SQLite), you will lose the marks for the lower band, effectively capping your grade. 



To achieve the \*\*First Class (80-100)\*\* band safely in a single pass, your application must act like a set of Russian nesting dolls. The 80-100 architecture must \*encapsulate\* the 70-79 features, which encapsulate the 60-69 features, all the way down to the 40-49 baseline. 



Since you are low on tokens, I have taken full control to engineer a \*\*"zero-iteration, comprehensive solution."\*\* Below is an expertly structured \*\*Software Requirements Specification (SRS)\*\* and \*\*Tech Stack Architecture\*\* designed to explicitly solve the "Ladder" constraint using an \*\*Offline-First Cloud Sync Strategy\*\*. This proves to the marker that you have mastered \*both\* local storage and cloud integration simultaneously.



\---



\# 🏗️ PART 1: STRATEGIC TECHNOLOGY STACK

\*Every technology is mapped directly to the grading rubric to ensure absolutely no marks are left on the table.\*



\### 1. Core Development \& Architecture

\*   \*\*Language:\*\* Kotlin \*(Mandated)\*.

\*   \*\*Target SDK:\*\* Min API 26 (Android 8.0) \*(Mandated)\*.

\*   \*\*Architecture Pattern:\*\* MVVM (Model-View-ViewModel) + \*\*Repository Pattern\*\*. 

&#x20;   \*   \*The Ladder Solution:\* The 60-69 band requires MVVM. The Repository pattern is the secret bridge. It acts as the mediator between your 40-49 requirement (Room) and your 60-69 requirement (Firebase), ensuring neither replaces the other.

\*   \*\*Concurrency:\*\* Kotlin Coroutines and `Flow`/`StateFlow` for non-blocking UI (Addresses MLO 2).



\### 2. Data Persistence (The Hybrid Sync Strategy)

\*   \*\*Local Database (Bands 40-59):\*\* \*\*Room Persistence Library (SQLite).\*\* \*Do not replace this.\* It must act as the "Single Source of Truth." All UI components observe Room.

\*   \*\*Cloud Backend (Bands 60-100):\*\* \*\*Firebase Authentication\*\*, \*\*Firebase Firestore\*\* (NoSQL for metadata), and \*\*Firebase Cloud Storage\*\* (for images/audio).

\*   \*\*Integration Engine (Band 80-100):\*\* Android \*\*WorkManager\*\*. When a user creates a capsule, it saves to Room instantly (offline capability). `WorkManager` runs in the background to sync it to Firestore when the network is available.



\### 3. Hardware, Sensors \& Media

\*   \*\*Media Capture (Bands 50-59 \& 80-100):\*\* `CameraX` API for photos and `MediaRecorder` API for audio. 

\*   \*\*Location Services (Bands 70-79):\*\* `FusedLocationProviderClient` (Google Play Services) for battery-efficient geofencing, and Google Maps SDK for the campus map view.

\*   \*\*Notifications (Bands 70-79):\*\* Firebase Cloud Messaging (FCM) + `AlarmManager` for time-based local push notifications.



\### 4. UI/UX \& Data Visualization

\*   \*\*UI Framework:\*\* XML Layouts with Jetpack Navigation Component. 

\*   \*\*Design System (Bands 70-79):\*\* \*\*Material Design 3 (MDC)\*\* for components and strict adherence to Accessibility (A11y) guidelines (content descriptions, dynamic colors).

\*   \*\*Innovation/Analytics (Band 80-100):\*\* \*\*MPAndroidChart\*\* library. (Used to fulfill the "analytical or visualisation features" requirement by plotting user discovery patterns).



\---



\# 📄 PART 2: SOFTWARE REQUIREMENTS SPECIFICATION (SRS)



\## 1. Introduction

\*\*1.1 Purpose\*\*

The purpose of this document is to define the software requirements for \*\*Echoes\*\*, a mobile application for the St Mary’s University community. Echoes allows students, staff, and alumni to create, discover, and interact with digital "time capsules" bound to specific dates, campus locations, and university events.



\*\*1.2 Scope\*\*

Echoes provides an offline-first, multimedia-rich environment for preserving campus culture. It securely handles user-generated content via local persistence (Room) that synchronizes with a cloud backend (Firebase) to facilitate community discovery, while strictly adhering to ethical data handling, performance optimization, and user consent.



\---



\## 2. Progressive Functional Requirements (FR)

\*These requirements are written cumulatively. The lower bands are explicitly carried forward into the upper bands.\*



\### Phase 1: Core Foundation (Fulfills 40-49 "Third Class")

\*   \*\*FR-1.1 Local Text Creation:\*\* The system shall allow users to create a time capsule containing a Title and Text Body.

\*   \*\*FR-1.2 Local Persistence:\*\* The system MUST store created capsules locally on the device using the Room (SQLite) database.

\*   \*\*FR-1.3 Basic View:\*\* The system shall provide a scrollable List View (`RecyclerView`) of all locally saved capsules. \*(Note: This List View must remain in the final app alongside the Map View).\*

\*   \*\*FR-1.4 Basic CRUD:\*\* The system shall allow users to Read, Update, and Delete their capsules locally.



\### Phase 2: Media \& Normalization (Fulfills 50-59 "Lower Second")

\*   \*\*FR-2.1 Media Attachment:\*\* The system shall allow users to attach exactly one additional media type (either an Image via Camera/Gallery, OR an Audio recording via Microphone) to their text capsule. Local file URIs will be saved to Room.

\*   \*\*FR-2.2 Database Normalization:\*\* The Room schema shall be expanded to include normalized metadata: `CreatorID`, `CreationTimestamp`, `UnlockConditionType`, and `IsLocked` (Boolean). 



\### Phase 3: Architecture \& Cloud (Fulfills 60-69 "Upper Second")

\*   \*\*FR-3.1 MVVM Refactoring:\*\* The system shall separate all UI logic from business logic using `ViewModel` and `StateFlow`.

\*   \*\*FR-3.2 Authentication:\*\* The system shall allow users to register and log in via Firebase Authentication.

\*   \*\*FR-3.3 Time-Based Unlocking:\*\* Users shall be able to set a future Date/Time unlock condition. The UI must hide the capsule's content until the system clock surpasses this date.

\*   \*\*FR-3.4 Filtering:\*\* The List View shall include tabs/chips to filter capsules by: \*Locked\*, \*Unlocked\*, and \*My Archives\*.

\*   \*\*FR-3.5 Cloud Mirroring:\*\* When a public capsule is saved to Room, the system shall mirror this data to Firebase Firestore (text) and Firebase Storage (media).



\### Phase 4: Sensors, Security \& UX (Fulfills 70-79 "First Class Baseline")

\*   \*\*FR-4.1 Location-Based Unlocking:\*\* Users shall be able to drop a pin on a St Mary's campus map. The system shall use the device's GPS to verify the user is physically within a 50-meter radius of the pin before unlocking the capsule.

\*   \*\*FR-4.2 Push Notifications:\*\* The system shall trigger a push notification (via `AlarmManager`) when a time or location condition is met (e.g., "A new memory at the Student Union has unlocked!").

\*   \*\*FR-4.3 Input Security:\*\* The system shall sanitize text inputs and utilize Firebase Security Rules to ensure users can only edit/delete their own cloud records.

\*   \*\*FR-4.4 Material \& Accessibility:\*\* The UI must implement Material Design guidelines, ensuring high color contrast and `contentDescription` tags for TalkBack screen readers.



\### Phase 5: Innovation \& Community (Fulfills 80-100 "Top First Class")

\*   \*\*FR-5.1 Advanced Offline-First Sync:\*\* The system shall handle offline-to-online transitions flawlessly via `WorkManager`. (e.g., If a user creates a capsule with no internet, it saves to Room. When WiFi returns, the background worker automatically pushes it to Firebase).

\*   \*\*FR-5.2 Campus Map Visualisation:\*\* The system shall include a Google Map view plotting locked capsules as generic icons, and unlocked ones as custom thumbnails.

\*   \*\*FR-5.3 Analytics Dashboard (Innovation):\*\* The system shall provide an "Insights" view (using `MPAndroidChart`) showing a bar chart of how many capsules the community has created/discovered per week, revealing patterns in campus activity.

\*   \*\*FR-5.4 Community Interaction:\*\* Users shall be able to "Favourite" and leave "Comments" on unlocked community capsules.



\---



\## 3. Non-Functional Requirements (NFRs)

\*   \*\*NFR-1 (Privacy \& Consent - Crucial for 80-100 Report):\*\* The application MUST display a clear, non-technical explanation of why Location and Camera permissions are needed within the university context \*before\* requesting OS-level permissions.

\*   \*\*NFR-2 (Performance Optimization - MLO 2):\*\* All network calls and database queries must be executed on background threads (`Dispatchers.IO`) to maintain a 60FPS UI. Images must be loaded using an efficient caching library (like `Glide` or `Coil`).

\*   \*\*NFR-3 (Data Handling):\*\* Sensitive local preferences (like session tokens) must be stored using `EncryptedSharedPreferences` or `DataStore`.



\---



\## 4. Entity Relationship / Data Model Schema

\*This explicitly satisfies the "normalised data model" (50-59 band). Map this identically in Room (`@Entity`) and Firestore.\*



\*\*Table 1: User (Firebase Auth)\*\*

\*   `userId` (PK, String), `displayName` (String), `role` (Enum: Student, Staff, Alumni)



\*\*Table 2: Capsule (Room Entity -> Maps to Firestore Document)\*\*

\*   `capsuleId` (PK, String UUID), `creatorId` (FK, String)

\*   `title` (String), `textContent` (String)

\*   `mediaUri` (String - Local URI for Room; Firebase URL for Firestore)

\*   `mediaType` (Enum: NONE, IMAGE, AUDIO)

\*   `isPublic` (Boolean - determines if it syncs to community map)

\*   `createdAt` (Timestamp)



\*\*Table 3: Unlock\_Condition (One-to-One with Capsule)\*\*

\*   `conditionId` (PK, String), `capsuleId` (FK, String)

\*   `conditionType` (Enum: TIME, LOCATION, EVENT)

\*   `unlockDate` (Timestamp, Nullable)

\*   `latitude` (Double, Nullable), `longitude` (Double, Nullable)

\*   `isUnlocked` (Boolean)



\*\*Table 4: Interactions\*\*

\*   `interactionId` (PK), `capsuleId` (FK), `userId` (FK), `type` (LIKE, COMMENT)



\---



\# ✍️ PART 3: THE 2400-WORD REPORT STRATEGY (CRITICAL)



Because the assessment is \*\*60% overall grade\*\* and heavily weights the report, a brilliant app will still fail to get an 80-100 if the report is weak. The rubric explicitly demands a "critical, research-informed evaluation." Structure your PDF exactly like this:



1\.  \*\*Introduction \& Problem Domain (200 words):\*\* Define the problem scenario at St Mary's. 

2\.  \*\*Architecture \& The Local vs. Cloud Debate (500 words):\*\* \*This is where you get your First Class marks.\* The rubric explicitly asks you to compare technical approaches. Critically evaluate a "Local-Only" vs "Cloud-First" design. Conclude that mobile network dead-zones on campus require an \*\*"Offline-First Architecture"\*\* (where Room is the source of truth, syncing to Firebase). Cite academic/industry sources on mobile architecture latency.

3\.  \*\*Data Model \& Implementation (500 words):\*\* Include a diagram of the ERD (Section 4 above). Detail how you implemented MVVM and Coroutines. Show a brief code snippet of your Location-based unlocking logic (distance calculation math).

4\.  \*\*Innovation \& Advanced Features (400 words):\*\* Detail your 80-100 band features: The Analytics chart (`MPAndroidChart`) and the Offline Sync Engine. 

5\.  \*\*Ethical, Legal \& Data Protection (600 words):\*\* \*Make or break for the top band.\* Reflect deeply on the ethics of tracking student locations. Discuss GDPR compliance. Discuss the risks of user-generated content (e.g., bullying, inappropriate photos) and how your app mitigates this (tying accounts to verified university emails, allowing users to report capsules, Right to be Forgotten).

6\.  \*\*Conclusion \& Future Enhancements (200 words):\*\* Propose realistic future features (e.g., Augmented Reality (AR) viewing of capsules on campus).



\### 🚨 Final Fail-Safe: Version Control \& Artefact Packaging

The rubric for the 80-100 band explicitly grades \*\*"Tools \& Development Practices"\*\*. 

If you submit perfect code but your Git history only has two commits ("initial commit" and "final project"), \*\*you will fail this section.\*\* 

1\. Initialise Git on day one. 

2\. Commit constantly with meaningful messages (`feat: add Room db`, `fix: crash on location deny`). 

3\. \*\*CRITICAL:\*\* Ensure the hidden `.git` folder is included in your final `.zip` submission. If this folder is missing, the examiner cannot see your commit history. Ensure your OS is set to "show hidden files" when zipping the project.

