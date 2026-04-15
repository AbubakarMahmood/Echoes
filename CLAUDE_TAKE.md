# Software Requirements Specification

## Echoes: Digital Time Capsule Application

---

| Field | Detail |
|---|---|
| **Document Title** | Software Requirements Specification — Echoes: Digital Time Capsule Application |
| **Version** | 1.0 |
| **Date** | 15 April 2026 |
| **Author** | Student, CPS5012 Assessment 1 |
| **Module** | CPS5012 Mobile Application Development |
| **Institution** | St Mary's University, Twickenham, London |
| **Module Convenor** | Prins Butt |
| **Classification** | Academic — Individual Assessment |

---

## Revision History

| Version | Date | Author | Description |
|---|---|---|---|
| 0.1 | 01 April 2026 | Student | Initial draft — Introduction and Overall Description |
| 0.2 | 05 April 2026 | Student | Functional and non-functional requirements added |
| 0.3 | 10 April 2026 | Student | Data model, architecture, security, and testing sections added |
| 1.0 | 15 April 2026 | Student | Final release — all sections complete, reviewed, and formatted |

---

## Table of Contents

1. [Introduction](#1-introduction)
   - 1.1 [Purpose of the Document](#11-purpose-of-the-document)
   - 1.2 [Scope](#12-scope)
   - 1.3 [Definitions, Acronyms, and Abbreviations](#13-definitions-acronyms-and-abbreviations)
   - 1.4 [References](#14-references)
   - 1.5 [Overview of the Document](#15-overview-of-the-document)
2. [Overall Description](#2-overall-description)
   - 2.1 [Product Perspective](#21-product-perspective)
   - 2.2 [Product Functions](#22-product-functions)
   - 2.3 [User Classes and Characteristics](#23-user-classes-and-characteristics)
   - 2.4 [Operating Environment](#24-operating-environment)
   - 2.5 [Design and Implementation Constraints](#25-design-and-implementation-constraints)
   - 2.6 [Assumptions and Dependencies](#26-assumptions-and-dependencies)
   - 2.7 [Apportioning of Requirements](#27-apportioning-of-requirements)
3. [Specific Requirements](#3-specific-requirements)
   - 3.1 [Functional Requirements](#31-functional-requirements)
   - 3.2 [Non-Functional Requirements](#32-non-functional-requirements)
   - 3.3 [External Interface Requirements](#33-external-interface-requirements)
   - 3.4 [System Features](#34-system-features)
4. [Data Model](#4-data-model)
   - 4.1 [Conceptual Entity-Relationship Description](#41-conceptual-entity-relationship-description)
   - 4.2 [Room Database Entities](#42-room-database-entities)
   - 4.3 [Firestore Collection Structure](#43-firestore-collection-structure)
   - 4.4 [DataStore Keys](#44-datastore-keys)
5. [Architecture Design](#5-architecture-design)
   - 5.1 [Architecture Pattern](#51-architecture-pattern-mvvm--clean-architecture)
   - 5.2 [Layer Descriptions](#52-layer-descriptions)
   - 5.3 [Module Structure](#53-module-structure)
   - 5.4 [Dependency Injection Strategy](#54-dependency-injection-strategy)
   - 5.5 [Data Flow Diagram](#55-data-flow-diagram)
   - 5.6 [Offline-First Strategy](#56-offline-first-strategy)
   - 5.7 [Background Processing](#57-background-processing)
6. [Security Requirements](#6-security-requirements)
   - 6.1 [Authentication](#61-authentication)
   - 6.2 [Data Encryption](#62-data-encryption)
   - 6.3 [Input Validation and Sanitisation](#63-input-validation-and-sanitisation)
   - 6.4 [Network Security](#64-network-security)
   - 6.5 [Privacy](#65-privacy)
7. [Ethical Considerations](#7-ethical-considerations)
   - 7.1 [User-Generated Content Moderation](#71-user-generated-content-moderation)
   - 7.2 [Location Privacy](#72-location-privacy)
   - 7.3 [Data Protection](#73-data-protection)
   - 7.4 [Accessibility and Inclusion](#74-accessibility-and-inclusion)
   - 7.5 [Responsible Content Policy](#75-responsible-content-policy)
8. [Testing Strategy](#8-testing-strategy)
   - 8.1 [Unit Testing](#81-unit-testing)
   - 8.2 [Integration Testing](#82-integration-testing)
   - 8.3 [UI Testing](#83-ui-testing)
   - 8.4 [Flow Testing](#84-flow-testing)
   - 8.5 [Performance Testing](#85-performance-testing)
   - 8.6 [Test Coverage Targets](#86-test-coverage-targets)
9. [Constraints and Limitations](#9-constraints-and-limitations)
10. [Tech Stack Summary Table](#10-tech-stack-summary-table)
11. [Project Milestones / Development Roadmap](#11-project-milestones--development-roadmap)
12. [Glossary](#12-glossary)
13. [Appendices](#13-appendices)
    - A: [Screen Inventory](#appendix-a-screen-inventory)
    - B: [Permission Manifest](#appendix-b-permission-manifest)
    - C: [Firebase Security Rules](#appendix-c-firebase-security-rules)
    - D: [Gradle Dependencies List](#appendix-d-gradle-dependencies-list)

---

## 1. Introduction

### 1.1 Purpose of the Document

This Software Requirements Specification (SRS) defines the functional and non-functional requirements for **Echoes**, an Android application enabling the St Mary's University community to create, share, and discover digital time capsules. The document adheres to the structure recommended by IEEE Standard 830-1998 (*IEEE Recommended Practice for Software Requirements Specifications*) and serves the following purposes:

1. **Development guide** — Provide an unambiguous, complete reference for the sole developer during the design, implementation, and testing phases.
2. **Assessment artefact** — Demonstrate requirements engineering rigour at the First Class (80–100) band for CPS5012 Assessment 1.
3. **Traceability baseline** — Establish numbered requirement identifiers (FR-*nnn*, NFR-*nnn*) that can be traced forward to implementation artefacts and test cases.
4. **Stakeholder communication** — Communicate the product vision, scope, and constraints to the module convenor and any future maintainers.

### 1.2 Scope

**Echoes** is a native Android application written in Kotlin targeting Android 8.0 (API level 26) and above. The application allows members of the St Mary's University community — students, staff, and alumni — to create digital "time capsules" containing text, photographs, and audio recordings. Each capsule is bound to one or more unlock conditions: a future date, proximity to a campus location (GPS geofence), or a university-defined event. Capsules remain sealed until their conditions are satisfied, at which point they become discoverable to the wider community.

**The system will:**

- Provide secure user registration and authentication via Firebase Authentication.
- Support creation of multimedia time capsules (text, image, and audio).
- Enforce configurable unlock conditions (date-based, location-based, event-based).
- Synchronise data between a local Room database and Cloud Firestore with an offline-first architecture.
- Deliver push notifications when capsule unlock conditions are met.
- Present a campus map view showing unlocked capsule locations.
- Enable community interaction through favourites and comments on unlocked capsules.
- Provide a personal analytics/insights dashboard.
- Comply with GDPR requirements through consent-based data handling.

**The system will not:**

- Provide a web or iOS client (Android only, as per assessment scope).
- Implement real-time chat or direct messaging between users.
- Provide administrative content moderation tooling beyond a simple flagging mechanism (a full back-office is outside assessment scope).
- Support video recording as a capsule media type (constrained by storage and processing requirements within the Firebase free tier).

### 1.3 Definitions, Acronyms, and Abbreviations

| Term | Definition |
|---|---|
| **API** | Application Programming Interface |
| **BOM** | Bill of Materials — a Gradle mechanism for managing compatible library versions |
| **CRUD** | Create, Read, Update, Delete |
| **DAO** | Data Access Object — Room interface for database queries |
| **DI** | Dependency Injection |
| **FCM** | Firebase Cloud Messaging |
| **FR** | Functional Requirement |
| **GDPR** | General Data Protection Regulation (EU 2016/679) |
| **Geofence** | A virtual perimeter defined by GPS coordinates and a radius |
| **GPS** | Global Positioning System |
| **HTTPS** | Hypertext Transfer Protocol Secure |
| **IEEE** | Institute of Electrical and Electronics Engineers |
| **JSON** | JavaScript Object Notation |
| **KDoc** | Kotlin documentation comments (analogous to Javadoc) |
| **MVVM** | Model-View-ViewModel architectural pattern |
| **NFR** | Non-Functional Requirement |
| **ORM** | Object-Relational Mapping |
| **REST** | Representational State Transfer |
| **SDK** | Software Development Kit |
| **SRS** | Software Requirements Specification |
| **SQL** | Structured Query Language |
| **SQLite** | Embedded relational database engine used by Android |
| **StateFlow** | A Kotlin coroutines-based observable state holder |
| **TalkBack** | Android's built-in screen reader for accessibility |
| **UI** | User Interface |
| **URI** | Uniform Resource Identifier |
| **UUID** | Universally Unique Identifier |
| **WCAG** | Web Content Accessibility Guidelines |
| **WorkManager** | Jetpack library for deferrable, guaranteed background work |

### 1.4 References

| ID | Reference |
|---|---|
| [1] | IEEE Std 830-1998, *IEEE Recommended Practice for Software Requirements Specifications*, IEEE, 1998. |
| [2] | Android Developers, "Guide to app architecture," https://developer.android.com/topic/architecture |
| [3] | Android Developers, "Save data in a local database using Room," https://developer.android.com/training/data-storage/room |
| [4] | Firebase Documentation, "Firebase Authentication," https://firebase.google.com/docs/auth |
| [5] | Firebase Documentation, "Cloud Firestore," https://firebase.google.com/docs/firestore |
| [6] | Firebase Documentation, "Firebase Cloud Messaging," https://firebase.google.com/docs/cloud-messaging |
| [7] | Firebase Documentation, "Firebase Storage," https://firebase.google.com/docs/storage |
| [8] | Google Developers, "Maps SDK for Android," https://developers.google.com/maps/documentation/android-sdk |
| [9] | Android Developers, "WorkManager," https://developer.android.com/topic/libraries/architecture/workmanager |
| [10] | Material Design 3, "Material You — Design Guidelines," https://m3.material.io/ |
| [11] | European Parliament, "General Data Protection Regulation (GDPR)," Regulation (EU) 2016/679. |
| [12] | W3C, "Web Content Accessibility Guidelines (WCAG) 2.1," https://www.w3.org/TR/WCAG21/ |
| [13] | Sommerville, I., *Software Engineering*, 10th ed., Pearson, 2016. |
| [14] | Android Developers, "CameraX Overview," https://developer.android.com/training/camerax |
| [15] | Android Developers, "Dependency injection with Hilt," https://developer.android.com/training/dependency-injection/hilt-android |
| [16] | Android Developers, "Paging library overview," https://developer.android.com/topic/libraries/architecture/paging/v3-overview |
| [17] | CPS5012 Module Handbook, "Assessment 1 Brief — Mobile Application Development," St Mary's University, 2025–2026. |

### 1.5 Overview of the Document

The remainder of this document is organised as follows:

- **Section 2** provides an overall description of the product, including its context, primary functions, user classes, operating environment, constraints, and assumptions.
- **Section 3** specifies detailed functional requirements (with full use cases and acceptance criteria), non-functional requirements (with measurable targets), external interface requirements, and a prioritised system features summary.
- **Section 4** presents the data model, including Room entity definitions, Firestore collection structures, and DataStore key specifications.
- **Section 5** describes the architectural design, covering the MVVM + Clean Architecture pattern, module structure, dependency injection, data flow, offline-first strategy, and background processing.
- **Section 6** addresses security requirements in detail.
- **Section 7** discusses ethical considerations surrounding user-generated content, location privacy, data protection, accessibility, and content policy.
- **Section 8** outlines the testing strategy across unit, integration, UI, and performance testing layers.
- **Section 9** documents constraints and known limitations.
- **Section 10** provides a consolidated tech stack reference table.
- **Section 11** presents the development roadmap and milestones.
- **Section 12** is a glossary.
- **Section 13** contains appendices: screen inventory, permission manifest, Firebase security rules, and Gradle dependencies.

---

## 2. Overall Description

### 2.1 Product Perspective

Echoes is a **standalone native Android application** with a cloud backend provided by Google Firebase. It is not a replacement for, or extension of, any existing university system. The application operates independently but integrates with the following external services:

- **Firebase Authentication** — Identity management (email/password).
- **Cloud Firestore** — Cloud document database for capsule synchronisation and community data.
- **Firebase Cloud Storage** — Binary media storage (images, audio files).
- **Firebase Cloud Messaging** — Push notification delivery.
- **Google Maps SDK** — Campus map rendering and capsule marker display.
- **Android Location Services (Fused Location Provider)** — GPS positioning for geofence evaluation.

The application follows an **offline-first architecture**: the local Room database is the single source of truth, and Cloud Firestore serves as a synchronised cloud backup. Users can create, view, and manage their capsules without an active internet connection; synchronisation occurs automatically when connectivity is restored, orchestrated by WorkManager.

### 2.2 Product Functions

At a high level, Echoes provides the following groups of functions:

1. **User Management** — Registration, authentication, profile management, and account deletion.
2. **Capsule Lifecycle** — Create, read, update, and delete time capsules containing text, images, or audio.
3. **Unlock Conditions** — Configure and evaluate date-based, location-based, and event-based unlock conditions.
4. **Discovery** — Browse, search, filter, and sort unlocked community capsules; view capsules on a campus map.
5. **Community Engagement** — Favourite and comment on unlocked capsules; view featured capsules for university events.
6. **Notifications** — Push notifications for date-triggered and proximity-triggered capsule unlocks; in-app notification centre.
7. **Personal Archive** — Dedicated view of the authenticated user's own capsules with filtering and sorting.
8. **Analytics & Insights** — Visual dashboard showing capsule creation trends, popular locations, and media type breakdowns.
9. **Settings & Privacy** — Location consent management, notification preferences, data export, and account deletion (GDPR compliance).

### 2.3 User Classes and Characteristics

| User Class | Description | Technical Proficiency | Frequency of Use |
|---|---|---|---|
| **Student** | Current undergraduate or postgraduate student at St Mary's University. Primary creator and consumer of capsules. | Low to moderate — familiar with mobile applications but not necessarily with technical concepts. | Daily to weekly during term time; may peak around university events (e.g., freshers' week, graduation). |
| **Staff** | Academic and administrative staff members. May create capsules related to departmental milestones, teaching memories, or campus events. | Moderate — comfortable with productivity applications. | Weekly to monthly. |
| **Alumni** | Former students. Primarily consumers who revisit campus capsules; may create capsules during alumni events. | Low to moderate. | Occasional — peaks during alumni weekends and graduation ceremonies. |
| **Admin** | A privileged user (likely a staff member or student union representative) who can define university events that trigger mass capsule unlocking and manage featured capsules. | Moderate to high — understands the application's event management features. | As needed — typically before major university events. |

All user classes interact through the same mobile application interface. Admin functionality is distinguished by a role flag on the User entity, which conditionally displays event management and featured capsule controls.

### 2.4 Operating Environment

| Aspect | Specification |
|---|---|
| **Platform** | Android (native) |
| **Minimum API Level** | API 26 (Android 8.0 Oreo) |
| **Target API Level** | API 35 (Android 15) |
| **Language** | Kotlin 2.0+ |
| **Device Type** | Smartphones and tablets with GPS, camera, and microphone hardware |
| **Minimum RAM** | 2 GB |
| **Connectivity** | Internet required for initial registration, cloud sync, and push notifications; core functionality available offline |
| **Location Services** | GPS / Fused Location Provider required for location-based unlock conditions |
| **Google Play Services** | Required for Firebase, Google Maps, and Fused Location Provider |

### 2.5 Design and Implementation Constraints

1. **Language constraint** — The application must be developed entirely in Kotlin, as mandated by the CPS5012 module.
2. **Architecture constraint** — MVVM architecture is required; the implementation extends this with Clean Architecture layers.
3. **Library constraint** — Only module-approved and widely-adopted open-source libraries may be used (Jetpack, Firebase, Google Maps SDK, Glide/Coil, Hilt).
4. **API level constraint** — Minimum API 26 ensures access to critical APIs (notification channels, background execution limits) while covering approximately 97% of active Android devices.
5. **Firebase free tier** — The application must operate within the Firebase Spark (free) plan limits: 1 GiB Firestore storage, 10 GiB/month network egress, 5 GB Firebase Storage, and 50,000 daily FCM messages.
6. **Individual work** — The project is an individual assessment; no collaborative development is permitted.
7. **Submission deadline** — All artefacts must be submitted by 8 May 2026.
8. **Privacy regulation** — The application must comply with GDPR, including explicit consent for location data collection and a mechanism for data export/deletion.

### 2.6 Assumptions and Dependencies

**Assumptions:**

1. Users have a valid St Mary's University email address for registration (although the system does not enforce a specific email domain — this is a design decision to allow alumni with personal emails).
2. The target device has a functional GPS sensor, rear-facing camera, and built-in microphone.
3. Google Play Services are installed and up to date on the target device.
4. The Firebase project will remain within free tier limits during development and assessment.
5. The module convenor's assessment environment has internet connectivity for testing cloud features.
6. Users grant the necessary runtime permissions (location, camera, microphone, notifications) when prompted; the application degrades gracefully if permissions are denied.

**Dependencies:**

1. Firebase platform availability (Authentication, Firestore, Storage, FCM).
2. Google Maps Platform API key provisioning.
3. Android Gradle Plugin and Jetpack library compatibility (tested against AGP 8.7+).
4. Google Play Services on user devices.

### 2.7 Apportioning of Requirements

Requirements are categorised by implementation priority aligned with the CPS5012 assessment grade bands. This phased approach ensures that core functionality is delivered first, with progressive enhancement toward the First Class target.

| Priority | Grade Band | Features |
|---|---|---|
| **P0 — Critical (MVP)** | Third Class (40–49) | User registration/login, text capsule CRUD, Room persistence, Git version control |
| **P1 — High** | Lower Second (50–59) | Normalised data model, image/audio capsule support, full CRUD, persistent Room storage |
| **P2 — High** | Upper Second (60–69) | MVVM + ViewModel + StateFlow, date-based unlocking, filtering/sorting, Firebase Auth, Firestore sync |
| **P3 — Medium** | First (70–79) | Location-based unlocking (geofence), push notifications (FCM), Material Design 3 accessibility, coroutines, image caching |
| **P4 — Medium-Low** | First (80–100) | Community features (favourites, comments), featured capsules, map view, analytics/insights, offline-first + WorkManager sync, GDPR consent flow, campus event integration |

---

## 3. Specific Requirements

### 3.1 Functional Requirements

#### 3.1.1 Authentication and User Management

---

**FR-001: User Registration**

| Field | Detail |
|---|---|
| **ID** | FR-001 |
| **Title** | User Registration |
| **Priority** | P0 — Critical |
| **Description** | The system shall allow a new user to create an account using an email address and password. Upon successful registration, a corresponding user record shall be created in both the local Room database and Cloud Firestore. |
| **Actors** | Unregistered User |
| **Preconditions** | The user has installed the application and has internet connectivity. |
| **Main Flow** | 1. User launches the application and selects "Create Account." 2. User enters a display name, email address, and password. 3. The system validates inputs (see FR-001-AC below). 4. The system calls Firebase Authentication to create the account. 5. Upon success, the system creates a User record in the local Room database. 6. The system enqueues a WorkManager task to sync the User record to Firestore. 7. The system navigates the user to the consent flow (FR-028), then to the Home screen. |
| **Alternative Flows** | **AF-1:** If the email is already registered, the system displays an inline error: "An account with this email already exists." **AF-2:** If the network is unavailable, the system displays: "Internet connection required to create an account." **AF-3:** If Firebase returns an error, the system displays the localised error message. |
| **Postconditions** | A new user account exists in Firebase Auth and a User record exists in Room. The user is authenticated and redirected to the Home screen. |
| **Acceptance Criteria** | **FR-001-AC1:** Display name must be 2–50 characters, alphanumeric and spaces only. **FR-001-AC2:** Email must match a valid email regex pattern. **FR-001-AC3:** Password must be at least 8 characters, containing at least one uppercase letter, one lowercase letter, and one digit. **FR-001-AC4:** On successful registration, the user is signed in automatically. **FR-001-AC5:** The Firestore `users` collection contains a document with the new user's UID within 30 seconds of registration (assuming connectivity). |

---

**FR-002: User Login**

| Field | Detail |
|---|---|
| **ID** | FR-002 |
| **Title** | User Login |
| **Priority** | P0 — Critical |
| **Description** | The system shall allow a registered user to authenticate using their email and password. |
| **Actors** | Registered User |
| **Preconditions** | The user has a registered account and has internet connectivity. |
| **Main Flow** | 1. User opens the application and is presented with the Login screen. 2. User enters their email and password. 3. The system validates inputs are non-empty. 4. The system calls Firebase Authentication `signInWithEmailAndPassword`. 5. On success, the system loads the user's local data from Room (or fetches from Firestore if first login on this device). 6. The system navigates to the Home screen. |
| **Alternative Flows** | **AF-1:** Invalid credentials — the system displays "Incorrect email or password." **AF-2:** Network unavailable — the system displays "Internet connection required to sign in." **AF-3:** Account disabled — the system displays "This account has been disabled. Contact support." |
| **Postconditions** | The user is authenticated. The Firebase Auth session token is active. Local data is loaded. |
| **Acceptance Criteria** | **FR-002-AC1:** Login completes within 3 seconds on a stable connection. **FR-002-AC2:** Subsequent app launches with a valid session token skip the login screen (automatic re-authentication). **FR-002-AC3:** A loading indicator is displayed during authentication. |

---

**FR-003: User Logout**

| Field | Detail |
|---|---|
| **ID** | FR-003 |
| **Title** | User Logout |
| **Priority** | P0 — Critical |
| **Description** | The system shall allow an authenticated user to sign out, clearing the active session. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is currently authenticated. |
| **Main Flow** | 1. User navigates to Settings and selects "Sign Out." 2. The system displays a confirmation dialog: "Are you sure you want to sign out?" 3. User confirms. 4. The system calls Firebase Auth `signOut()`. 5. The system clears the in-memory authentication state. 6. The system navigates to the Login screen. |
| **Alternative Flows** | **AF-1:** User cancels the confirmation dialog — no action is taken. |
| **Postconditions** | The Firebase Auth session is invalidated. The user is returned to the Login screen. Local Room data is retained for offline access upon next login. |
| **Acceptance Criteria** | **FR-003-AC1:** After logout, attempting to navigate to any authenticated screen redirects to Login. **FR-003-AC2:** Local Room data is not deleted on logout (preserved for re-login). |

---

**FR-004: Password Reset**

| Field | Detail |
|---|---|
| **ID** | FR-004 |
| **Title** | Password Reset |
| **Priority** | P1 — High |
| **Description** | The system shall allow a user to request a password reset email via Firebase Authentication. |
| **Actors** | Registered User (authenticated or unauthenticated) |
| **Preconditions** | The user knows their registered email address and has internet connectivity. |
| **Main Flow** | 1. User selects "Forgot Password?" on the Login screen. 2. User enters their registered email address. 3. The system calls Firebase Auth `sendPasswordResetEmail`. 4. The system displays: "A password reset link has been sent to your email." 5. The user resets their password via the email link (handled by Firebase). |
| **Alternative Flows** | **AF-1:** Email not found — the system displays the same success message (to prevent email enumeration attacks). **AF-2:** Network unavailable — the system displays an error. |
| **Postconditions** | A password reset email is sent (if the account exists). |
| **Acceptance Criteria** | **FR-004-AC1:** The reset email is sent within 60 seconds. **FR-004-AC2:** The success message is displayed regardless of whether the email exists (security best practice). |

---

**FR-005: User Profile View/Edit**

| Field | Detail |
|---|---|
| **ID** | FR-005 |
| **Title** | User Profile View/Edit |
| **Priority** | P2 — High |
| **Description** | The system shall allow an authenticated user to view and edit their profile information, including display name and profile image. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. |
| **Main Flow** | 1. User navigates to the Profile screen from the bottom navigation or Settings. 2. The system displays the current display name, email (read-only), profile image, account creation date, and capsule count. 3. User taps "Edit Profile." 4. User modifies their display name and/or profile image (via gallery picker or CameraX capture). 5. The system validates the display name. 6. If a new image is selected, the system uploads it to Firebase Storage and retrieves the download URL. 7. The system updates the local Room User record and enqueues a Firestore sync. 8. The system displays a success message. |
| **Alternative Flows** | **AF-1:** Image upload fails — the system retains the previous image and displays an error. **AF-2:** Display name validation fails — inline error is shown. |
| **Postconditions** | The user's profile information is updated locally and queued for cloud sync. |
| **Acceptance Criteria** | **FR-005-AC1:** Display name changes are reflected immediately in the UI. **FR-005-AC2:** Profile image is compressed to a maximum of 512 KB before upload. **FR-005-AC3:** Email address is displayed but not editable. |

---

#### 3.1.2 Capsule Creation and Management

---

**FR-006: Create Text Capsule**

| Field | Detail |
|---|---|
| **ID** | FR-006 |
| **Title** | Create Text Capsule |
| **Priority** | P0 — Critical |
| **Description** | The system shall allow an authenticated user to create a time capsule containing a text-based story, reflection, or message. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. |
| **Main Flow** | 1. User taps the "Create Capsule" floating action button on the Home screen. 2. The system presents the capsule creation form. 3. User selects "Text" as the media type. 4. User enters a title (required, 3–100 characters). 5. User enters body text (required, 10–5000 characters). 6. User optionally selects a category tag (e.g., "Memory," "Reflection," "Milestone," "Event"). 7. User configures at least one unlock condition (see FR-013, FR-014, FR-015). 8. User toggles the "Public" switch (defaults to true; if false, only the creator can view the capsule when unlocked). 9. User taps "Seal Capsule." 10. The system validates all inputs. 11. The system creates a TimeCapsule record in Room with `isUnlocked = false`. 12. The system enqueues a Firestore sync task via WorkManager. 13. The system displays a confirmation animation and navigates to the capsule detail view. |
| **Alternative Flows** | **AF-1:** Validation fails — the system highlights invalid fields with inline error messages. **AF-2:** User cancels — the system prompts "Discard this capsule?" with Discard/Keep Editing options. |
| **Postconditions** | A new TimeCapsule record exists in Room with `mediaType = TEXT` and `isUnlocked = false`. A Firestore sync task is enqueued. |
| **Acceptance Criteria** | **FR-006-AC1:** The capsule is persisted in Room immediately. **FR-006-AC2:** The capsule appears in the user's personal archive (FR-011). **FR-006-AC3:** The capsule is not visible to other users until unlocked. **FR-006-AC4:** At least one unlock condition must be set; the form prevents submission otherwise. |

---

**FR-007: Create Photo Capsule**

| Field | Detail |
|---|---|
| **ID** | FR-007 |
| **Title** | Create Photo Capsule (CameraX Capture + Gallery Picker) |
| **Priority** | P1 — High |
| **Description** | The system shall allow an authenticated user to create a time capsule containing a photograph, either captured using the device camera or selected from the device gallery. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. The device has a camera (for capture) or stored images (for gallery selection). |
| **Main Flow** | 1. User taps "Create Capsule" and selects "Photo" as the media type. 2. The system presents two options: "Take Photo" and "Choose from Gallery." 3a. **Take Photo:** The system opens the CameraX preview. User captures a photo. The system saves the image to app-specific internal storage. 3b. **Choose from Gallery:** The system launches the AndroidX Photo Picker (API 33+) or an Intent-based gallery picker (fallback for API 26–32). User selects an image. The system copies the image to app-specific internal storage. 4. The system displays a preview of the selected image. 5. User enters a title and optional description. 6. User configures unlock conditions and visibility. 7. User taps "Seal Capsule." 8. The system compresses the image (max 1920px longest edge, JPEG quality 80). 9. The system creates the TimeCapsule record in Room with `mediaType = IMAGE` and `mediaUri` pointing to the local file. 10. The system enqueues a WorkManager task to upload the image to Firebase Storage and sync the capsule to Firestore. |
| **Alternative Flows** | **AF-1:** Camera permission denied — the system displays a rationale and offers the gallery option. **AF-2:** No images available in gallery — the system displays an informational message. **AF-3:** Image compression fails — the system logs the error and retries with lower quality settings. |
| **Postconditions** | A TimeCapsule record with `mediaType = IMAGE` exists in Room. The image file is stored locally. A background task is enqueued to upload the image to Firebase Storage. |
| **Acceptance Criteria** | **FR-007-AC1:** Captured photos are saved at a minimum resolution of 1080px on the shortest edge. **FR-007-AC2:** Gallery-picked images are copied to internal storage (not referenced by external URI). **FR-007-AC3:** Image compression reduces file size to under 2 MB. **FR-007-AC4:** The image preview is displayed before sealing. |

---

**FR-008: Create Audio Capsule**

| Field | Detail |
|---|---|
| **ID** | FR-008 |
| **Title** | Create Audio Capsule (MediaRecorder) |
| **Priority** | P1 — High |
| **Description** | The system shall allow an authenticated user to create a time capsule containing an audio recording. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. The device has a microphone. The RECORD_AUDIO permission is granted. |
| **Main Flow** | 1. User taps "Create Capsule" and selects "Audio" as the media type. 2. The system checks for RECORD_AUDIO permission; if not granted, requests it. 3. The system displays the audio recording interface with a record button, timer, and waveform visualisation. 4. User taps "Record" to start. The system begins recording using Android MediaRecorder (AAC format, 128 kbps). 5. A real-time timer displays elapsed recording time. 6. User taps "Stop" to end the recording (maximum 5 minutes enforced). 7. The system displays playback controls for review. 8. User may re-record or accept the recording. 9. User enters a title and optional description. 10. User configures unlock conditions and visibility. 11. User taps "Seal Capsule." 12. The system creates the TimeCapsule record in Room with `mediaType = AUDIO` and `mediaUri` pointing to the local AAC file. 13. The system enqueues a WorkManager task to upload the audio file to Firebase Storage. |
| **Alternative Flows** | **AF-1:** Microphone permission denied — the system displays a rationale and suggests text or photo capsule types. **AF-2:** Recording exceeds 5 minutes — the system automatically stops and notifies the user. **AF-3:** Storage insufficient — the system displays an error before recording starts. |
| **Postconditions** | A TimeCapsule record with `mediaType = AUDIO` exists in Room. The audio file is stored locally. A background task is enqueued for Firebase Storage upload. |
| **Acceptance Criteria** | **FR-008-AC1:** Audio is recorded in AAC format at 128 kbps. **FR-008-AC2:** Maximum recording duration is 5 minutes. **FR-008-AC3:** The user can preview (play back) the recording before sealing. **FR-008-AC4:** A waveform or amplitude indicator is displayed during recording. |

---

**FR-009: Edit Capsule (Before Unlock)**

| Field | Detail |
|---|---|
| **ID** | FR-009 |
| **Title** | Edit Capsule (Before Unlock) |
| **Priority** | P0 — Critical |
| **Description** | The system shall allow an authenticated user to edit a capsule they own, provided the capsule has not yet been unlocked. |
| **Actors** | Authenticated User (capsule owner) |
| **Preconditions** | The user is authenticated. The capsule exists in Room with `isUnlocked = false`. The user is the capsule owner (`userId` matches). |
| **Main Flow** | 1. User navigates to the capsule detail screen for one of their locked capsules. 2. User taps "Edit." 3. The system presents the capsule creation form pre-populated with existing data. 4. User modifies title, description, media, unlock conditions, or visibility. 5. User taps "Save Changes." 6. The system validates inputs. 7. The system updates the TimeCapsule record in Room (`updatedAt` timestamp refreshed). 8. The system enqueues a Firestore sync task. 9. The system displays a success message and returns to the capsule detail view. |
| **Alternative Flows** | **AF-1:** The capsule is already unlocked — the "Edit" button is hidden and the system prevents edits. **AF-2:** Validation fails — inline errors are displayed. |
| **Postconditions** | The TimeCapsule record is updated in Room. A sync task is enqueued. |
| **Acceptance Criteria** | **FR-009-AC1:** Only locked capsules display the "Edit" option. **FR-009-AC2:** Only the capsule owner can edit. **FR-009-AC3:** The `updatedAt` timestamp is refreshed on save. **FR-009-AC4:** Media can be replaced (e.g., swap a photo). |

---

**FR-010: Delete Capsule**

| Field | Detail |
|---|---|
| **ID** | FR-010 |
| **Title** | Delete Capsule |
| **Priority** | P0 — Critical |
| **Description** | The system shall allow an authenticated user to permanently delete a capsule they own. |
| **Actors** | Authenticated User (capsule owner) |
| **Preconditions** | The user is authenticated. The capsule exists in Room. The user is the capsule owner. |
| **Main Flow** | 1. User navigates to the capsule detail screen. 2. User taps "Delete." 3. The system displays a destructive confirmation dialog: "This action cannot be undone. Delete this capsule?" 4. User confirms. 5. The system deletes the TimeCapsule record from Room (cascade deletes associated Comments and Favourites). 6. The system deletes associated media files from local storage. 7. The system enqueues a WorkManager task to delete the Firestore document and Firebase Storage files. 8. The system navigates back to the previous screen. |
| **Alternative Flows** | **AF-1:** User cancels the confirmation — no action taken. **AF-2:** Firestore deletion fails (offline) — the deletion is queued and retried when connectivity is restored. |
| **Postconditions** | The capsule, associated comments, favourites, and media files are removed locally. A cloud deletion task is enqueued. |
| **Acceptance Criteria** | **FR-010-AC1:** Deletion requires explicit user confirmation. **FR-010-AC2:** Local data is deleted immediately. **FR-010-AC3:** Cloud deletion occurs within 60 seconds of connectivity restoration. **FR-010-AC4:** Both locked and unlocked capsules can be deleted by the owner. |

---

**FR-011: View My Capsules (Personal Archive)**

| Field | Detail |
|---|---|
| **ID** | FR-011 |
| **Title** | View My Capsules (Personal Archive) |
| **Priority** | P2 — High |
| **Description** | The system shall provide a dedicated screen displaying all capsules owned by the authenticated user, with filtering and sorting capabilities. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. |
| **Main Flow** | 1. User navigates to "My Capsules" via the bottom navigation bar. 2. The system queries Room for all TimeCapsule records where `userId` matches the current user. 3. The system displays capsules in a paginated list (Paging 3) with thumbnail, title, media type icon, lock/unlock status, and creation date. 4. User may apply filters: locked/unlocked, media type (text/image/audio), date range. 5. User may sort by: newest first, oldest first, alphabetical. 6. User taps a capsule to navigate to the capsule detail view (FR-012). |
| **Alternative Flows** | **AF-1:** No capsules exist — the system displays an empty state with an illustration and a "Create Your First Capsule" call-to-action button. |
| **Postconditions** | The capsule list is displayed with the applied filters and sort order. |
| **Acceptance Criteria** | **FR-011-AC1:** The list uses Paging 3 with a page size of 20. **FR-011-AC2:** Filter and sort selections persist across navigation within the session. **FR-011-AC3:** Locked capsules display a lock icon overlay; unlocked capsules display an open lock icon. **FR-011-AC4:** The list loads within 500 ms for up to 100 capsules. |

---

**FR-012: View Capsule Detail**

| Field | Detail |
|---|---|
| **ID** | FR-012 |
| **Title** | View Capsule Detail |
| **Priority** | P0 — Critical |
| **Description** | The system shall display the full details of a capsule, including its content (if unlocked), metadata, unlock conditions, and community interactions. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. The capsule exists. |
| **Main Flow** | 1. User taps on a capsule from any list (personal archive, community feed, or map marker). 2. **If the capsule is locked and owned by the user:** The system displays the title, creation date, unlock conditions (with progress indicators, e.g., "Unlocks on 15 June 2026" or "Unlocks near Main Entrance"), and edit/delete options. Content is hidden behind a "sealed" overlay animation. 3. **If the capsule is locked and not owned by the user:** The capsule is not accessible (the system should not display it in community feeds). 4. **If the capsule is unlocked:** The system displays the full content (text, image with zoom capability, or audio with playback controls), creator name and profile image, creation date, unlock date, location (if location-based), category tag, favourite count, and comments section. 5. For unlocked capsules, the user can favourite (FR-022) or comment (FR-023). |
| **Alternative Flows** | **AF-1:** Media file not yet downloaded (image/audio) — the system shows a loading indicator and fetches from Firebase Storage. **AF-2:** Media file corrupted — the system displays an error placeholder. |
| **Postconditions** | The capsule detail view is displayed with all available information. |
| **Acceptance Criteria** | **FR-012-AC1:** Locked capsule content is never displayed to any user, including the owner. **FR-012-AC2:** Images support pinch-to-zoom. **FR-012-AC3:** Audio capsules display a play/pause button, seek bar, and duration. **FR-012-AC4:** Comments are displayed in reverse chronological order. |

---

#### 3.1.3 Unlock Conditions

---

**FR-013: Set Date-Based Unlock Condition**

| Field | Detail |
|---|---|
| **ID** | FR-013 |
| **Title** | Set Date-Based Unlock Condition |
| **Priority** | P2 — High |
| **Description** | The system shall allow a user to specify a future date and time at which the capsule will automatically unlock. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is creating or editing a capsule. |
| **Main Flow** | 1. During capsule creation/editing, user selects "Date" as an unlock type. 2. The system presents a Material Design 3 date picker and time picker. 3. User selects a date and time in the future. 4. The system validates the selected date/time is at least 1 hour in the future. 5. The `unlockDate` field is stored on the TimeCapsule entity. 6. The `unlockType` is set to `DATE` (or `DATE` is added to a composite condition). |
| **Alternative Flows** | **AF-1:** User selects a past date — the system displays "Please select a future date and time." |
| **Postconditions** | The capsule's `unlockDate` is set. The UnlockCheckWorker will evaluate this condition periodically. |
| **Acceptance Criteria** | **FR-013-AC1:** The selected date must be at least 1 hour in the future. **FR-013-AC2:** The date picker defaults to tomorrow's date. **FR-013-AC3:** The date is stored in UTC and displayed in the user's local timezone. |

---

**FR-014: Set Location-Based Unlock Condition (Geofence)**

| Field | Detail |
|---|---|
| **ID** | FR-014 |
| **Title** | Set Location-Based Unlock Condition |
| **Priority** | P3 — Medium |
| **Description** | The system shall allow a user to specify a campus location and radius; the capsule unlocks when a viewer is within the defined geofence. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is creating or editing a capsule. Location permission has been granted. |
| **Main Flow** | 1. During capsule creation/editing, user selects "Location" as an unlock type. 2. The system displays an embedded Google Map centred on St Mary's University campus (latitude: 51.4328, longitude: −0.3261). 3. User taps on the map to place a marker or selects from a predefined list of campus landmarks (e.g., Main Entrance, Library, Chapel, Student Union, Sports Centre). 4. The system displays a radius slider (minimum 25 metres, maximum 500 metres, default 100 metres). 5. User adjusts the radius. A translucent circle overlay on the map shows the geofence area. 6. The system stores `unlockLatitude`, `unlockLongitude`, and `unlockRadiusMeters` on the TimeCapsule entity. 7. `unlockType` is set to `LOCATION`. |
| **Alternative Flows** | **AF-1:** Location permission not granted — the system displays a rationale dialog and directs the user to device settings. The location unlock option is disabled until permission is granted. **AF-2:** GPS unavailable — the system displays a warning and suggests using a date-based condition instead. |
| **Postconditions** | The capsule's geofence parameters are stored. The location-based unlock evaluation will check the user's proximity. |
| **Acceptance Criteria** | **FR-014-AC1:** The map is centred on St Mary's campus by default. **FR-014-AC2:** Predefined landmarks are available as quick-select options. **FR-014-AC3:** The radius slider range is 25–500 metres. **FR-014-AC4:** The geofence circle updates in real time as the slider is adjusted. **FR-014-AC5:** Coordinates are stored with a precision of at least 6 decimal places. |

---

**FR-015: Set Event-Based Unlock Condition**

| Field | Detail |
|---|---|
| **ID** | FR-015 |
| **Title** | Set Event-Based Unlock Condition |
| **Priority** | P4 — Medium-Low |
| **Description** | The system shall allow a user to tie a capsule's unlock to an admin-defined university event. When the event is activated by an admin, all capsules linked to that event unlock simultaneously. |
| **Actors** | Authenticated User (capsule creator), Admin (event definer) |
| **Preconditions** | At least one active UnlockEvent exists in the system. |
| **Main Flow** | 1. During capsule creation/editing, user selects "Event" as an unlock type. 2. The system fetches the list of active UnlockEvent records from Room (synced from Firestore). 3. The system displays the events in a selectable list with event name and date. 4. User selects an event. 5. The `unlockEventId` foreign key is stored on the TimeCapsule entity. 6. `unlockType` is set to `EVENT`. |
| **Alternative Flows** | **AF-1:** No events are currently defined — the system displays "No upcoming events available" and suggests date or location unlock types. |
| **Postconditions** | The capsule is linked to the selected event. When the admin activates the event, all linked capsules are unlocked. |
| **Acceptance Criteria** | **FR-015-AC1:** Only events with `isActive = true` and `eventDate` in the future are displayed. **FR-015-AC2:** The selected event name is displayed on the capsule detail screen. **FR-015-AC3:** When an event is activated, all linked capsules are unlocked within 5 minutes. |

---

**FR-016: Evaluate Unlock Conditions (Background Worker)**

| Field | Detail |
|---|---|
| **ID** | FR-016 |
| **Title** | Evaluate Unlock Conditions |
| **Priority** | P2 — High |
| **Description** | The system shall periodically evaluate all locked capsules' unlock conditions in the background using WorkManager. |
| **Actors** | System (UnlockCheckWorker) |
| **Preconditions** | Locked capsules exist in the Room database. |
| **Main Flow** | 1. WorkManager schedules the `UnlockCheckWorker` as a periodic task (every 15 minutes, the minimum interval). 2. The worker queries Room for all capsules where `isUnlocked = false`. 3. For each locked capsule: a. **Date-based:** Compare `unlockDate` with the current UTC time. If `currentTime >= unlockDate`, mark as unlocked. b. **Location-based:** Retrieve the user's last known location. If the distance between the user's location and the capsule's (`unlockLatitude`, `unlockLongitude`) is less than or equal to `unlockRadiusMeters`, mark as unlocked. c. **Event-based:** Check if the linked UnlockEvent's `isActive` flag has been set to true by an admin. If so, mark as unlocked. 4. For each newly unlocked capsule, update `isUnlocked = true` in Room. 5. Enqueue a Firestore sync for updated capsules. 6. Trigger a push notification (FR-025/FR-026) for each newly unlocked capsule. |
| **Alternative Flows** | **AF-1:** No locked capsules exist — the worker completes immediately. **AF-2:** Location permission revoked — location-based conditions are skipped (not evaluated as met). |
| **Postconditions** | Capsules whose conditions are satisfied are marked as unlocked. Notifications are dispatched. Cloud sync is enqueued. |
| **Acceptance Criteria** | **FR-016-AC1:** The worker runs at least every 15 minutes when the app is in the background. **FR-016-AC2:** Date-based evaluations are accurate to within 1 minute. **FR-016-AC3:** Location-based evaluations use the Fused Location Provider's last known location. **FR-016-AC4:** The worker does not drain battery excessively (uses `PeriodicWorkRequest` with `ExistingPeriodicWorkPolicy.KEEP`). |

---

**FR-017: Manual Unlock Check**

| Field | Detail |
|---|---|
| **ID** | FR-017 |
| **Title** | Manual Unlock Check |
| **Priority** | P2 — High |
| **Description** | The system shall allow the user to manually trigger an unlock condition evaluation via a pull-to-refresh gesture or a dedicated refresh button. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is on the personal archive or community feed screen. |
| **Main Flow** | 1. User performs a pull-to-refresh gesture (SwipeRefreshLayout) or taps a refresh icon. 2. The system immediately runs the unlock evaluation logic (same as FR-016, but one-shot via `OneTimeWorkRequest`). 3. The system refreshes the capsule list to reflect any newly unlocked capsules. 4. A brief toast or snackbar indicates "Capsules refreshed." |
| **Alternative Flows** | **AF-1:** No capsules were unlocked — the list is refreshed with no visible changes. |
| **Postconditions** | The capsule list reflects the current unlock state. |
| **Acceptance Criteria** | **FR-017-AC1:** The manual check completes within 3 seconds. **FR-017-AC2:** A loading indicator is displayed during evaluation. |

---

#### 3.1.4 Discovery and Community

---

**FR-018: Browse Unlocked Capsules Feed**

| Field | Detail |
|---|---|
| **ID** | FR-018 |
| **Title** | Browse Unlocked Capsules Feed |
| **Priority** | P2 — High |
| **Description** | The system shall provide a community feed displaying all publicly unlocked capsules from all users. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. Unlocked, public capsules exist. |
| **Main Flow** | 1. User navigates to the "Discover" tab via bottom navigation. 2. The system queries Room (with Firestore sync) for all capsules where `isUnlocked = true` and `isPublic = true`. 3. Capsules are displayed in a paginated list (Paging 3), each showing: thumbnail (image preview or media type icon), title, creator display name, creation date, favourite count, and comment count. 4. The list defaults to "Newest First" sorting. 5. User taps a capsule to view its detail (FR-012). |
| **Alternative Flows** | **AF-1:** No unlocked capsules exist — the system displays an empty state: "No capsules have been unlocked yet. Check back soon!" |
| **Postconditions** | The community feed is displayed with paginated, sorted capsules. |
| **Acceptance Criteria** | **FR-018-AC1:** Only capsules with `isUnlocked = true` and `isPublic = true` are shown. **FR-018-AC2:** The feed loads the first page within 1 second. **FR-018-AC3:** Pagination loads the next page seamlessly on scroll. **FR-018-AC4:** Each list item displays the creator's display name and profile image. |

---

**FR-019: Map View of Campus with Unlocked Capsule Markers**

| Field | Detail |
|---|---|
| **ID** | FR-019 |
| **Title** | Map View of Campus with Unlocked Capsule Markers |
| **Priority** | P4 — Medium-Low |
| **Description** | The system shall display a Google Map centred on the St Mary's University campus, with custom markers indicating the locations of unlocked capsules. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. Location-based unlocked capsules exist. |
| **Main Flow** | 1. User navigates to the "Map" tab via bottom navigation. 2. The system initialises a Google Map centred on St Mary's campus (51.4328, −0.3261) with an appropriate zoom level (approximately 16). 3. The system queries Room for all capsules where `isUnlocked = true`, `isPublic = true`, and `unlockType = LOCATION` (or any capsule with valid coordinates). 4. For each qualifying capsule, a custom marker is placed at (`unlockLatitude`, `unlockLongitude`). Marker icons differ by media type (text: speech bubble, image: camera, audio: microphone). 5. User taps a marker to view a brief info window (title, creator, date). 6. User taps the info window to navigate to the capsule detail view (FR-012). 7. The user's current location is displayed as a blue dot (if location permission is granted). |
| **Alternative Flows** | **AF-1:** No capsules with location data exist — the map displays with no markers and a message overlay: "No capsules on the map yet." **AF-2:** Google Maps SDK fails to load — the system displays a fallback error screen. |
| **Postconditions** | The map view is displayed with capsule markers. |
| **Acceptance Criteria** | **FR-019-AC1:** The map is centred on St Mary's campus on initial load. **FR-019-AC2:** Custom marker icons distinguish media types. **FR-019-AC3:** Info windows display capsule title, creator name, and date. **FR-019-AC4:** Marker clustering is applied when markers are dense (using the Maps SDK utility library). |

---

**FR-020: Search Capsules**

| Field | Detail |
|---|---|
| **ID** | FR-020 |
| **Title** | Search Capsules |
| **Priority** | P3 — Medium |
| **Description** | The system shall allow the user to search for unlocked capsules by keyword, matching against capsule title, description, and creator display name. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated and on the Discover or My Capsules screen. |
| **Main Flow** | 1. User taps the search icon in the top app bar. 2. The system displays a search text field with a debounced input listener (300 ms delay). 3. User types a query. 4. The system queries Room using a `LIKE` clause against the `title`, `description`, and joined `User.displayName` fields for unlocked, public capsules. 5. Results are displayed in the same list format as the community feed. 6. The search query is highlighted in the results. |
| **Alternative Flows** | **AF-1:** No results found — the system displays "No capsules match your search." **AF-2:** Query is empty — the full default list is restored. |
| **Postconditions** | Search results are displayed. |
| **Acceptance Criteria** | **FR-020-AC1:** Search is case-insensitive. **FR-020-AC2:** Results update within 500 ms of the user stopping typing (debounced). **FR-020-AC3:** Minimum query length is 2 characters. |

---

**FR-021: Filter/Sort Capsules**

| Field | Detail |
|---|---|
| **ID** | FR-021 |
| **Title** | Filter/Sort Capsules |
| **Priority** | P2 — High |
| **Description** | The system shall allow the user to filter and sort capsules by multiple criteria on both the personal archive and community feed screens. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is on the My Capsules or Discover screen. |
| **Main Flow** | 1. User taps a "Filter" chip or icon. 2. The system presents a bottom sheet with filter options: a. **Status:** All / Locked / Unlocked (personal archive only). b. **Media Type:** All / Text / Image / Audio. c. **Category:** All / Memory / Reflection / Milestone / Event. d. **Date Range:** Start date – End date (creation date). 3. User selects filters and taps "Apply." 4. The system applies the Room query filters and refreshes the list. 5. Active filters are displayed as dismissible chips above the list. 6. User may also select a sort order from a dropdown: Newest First / Oldest First / Most Favourited / Alphabetical (A–Z). |
| **Alternative Flows** | **AF-1:** No capsules match the filters — the system displays an empty state with a "Clear Filters" button. |
| **Postconditions** | The list reflects the selected filters and sort order. |
| **Acceptance Criteria** | **FR-021-AC1:** Multiple filters can be applied simultaneously (AND logic). **FR-021-AC2:** Active filters are displayed as dismissible chips. **FR-021-AC3:** The "Clear All" option removes all active filters. **FR-021-AC4:** Sort order defaults to "Newest First." |

---

**FR-022: Favourite a Capsule**

| Field | Detail |
|---|---|
| **ID** | FR-022 |
| **Title** | Favourite a Capsule |
| **Priority** | P4 — Medium-Low |
| **Description** | The system shall allow an authenticated user to favourite (or un-favourite) an unlocked capsule, and the total favourite count shall be displayed on the capsule. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. The capsule is unlocked and public. |
| **Main Flow** | 1. User views an unlocked capsule (detail view or feed item). 2. User taps the heart/favourite icon. 3. If the capsule is not currently favourited by this user: a. The system creates a Favourite record in Room (capsuleId, userId, createdAt). b. The heart icon fills (animated transition). c. The favourite count increments. 4. If the capsule is already favourited by this user: a. The system deletes the Favourite record from Room. b. The heart icon unfills. c. The favourite count decrements. 5. A Firestore sync task is enqueued. |
| **Alternative Flows** | None. |
| **Postconditions** | The Favourite record is created or deleted in Room. The UI reflects the current state. A sync task is enqueued. |
| **Acceptance Criteria** | **FR-022-AC1:** The favourite toggle is reflected instantly in the UI (optimistic update). **FR-022-AC2:** A user can favourite the same capsule only once. **FR-022-AC3:** The favourite count is accurate (derived from the count of Favourite records for that capsule). **FR-022-AC4:** The heart icon animates on tap. |

---

**FR-023: Comment on Unlocked Capsule**

| Field | Detail |
|---|---|
| **ID** | FR-023 |
| **Title** | Comment on Unlocked Capsule |
| **Priority** | P4 — Medium-Low |
| **Description** | The system shall allow an authenticated user to post a text comment on an unlocked capsule and view comments from other users. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. The capsule is unlocked and public. |
| **Main Flow** | 1. User scrolls to the comments section on the capsule detail screen. 2. Existing comments are displayed in reverse chronological order, each showing: commenter display name, profile image, comment text, and timestamp. 3. User types a comment in the input field (1–500 characters). 4. User taps "Post." 5. The system validates the input (non-empty, within character limit, sanitised for prohibited content). 6. The system creates a Comment record in Room. 7. The comment appears immediately at the top of the list. 8. A Firestore sync task is enqueued. |
| **Alternative Flows** | **AF-1:** Comment is empty or exceeds 500 characters — the "Post" button is disabled. **AF-2:** Comment contains prohibited content (basic keyword filter) — the system displays a warning. |
| **Postconditions** | A Comment record exists in Room. The comment is visible on the capsule. A sync task is enqueued. |
| **Acceptance Criteria** | **FR-023-AC1:** Comments appear instantly after posting (optimistic update). **FR-023-AC2:** Each comment displays the commenter's display name, profile image, content, and relative timestamp (e.g., "2 hours ago"). **FR-023-AC3:** A user may post multiple comments on the same capsule. **FR-023-AC4:** Comment text is sanitised (HTML tags stripped). |

---

**FR-024: Featured Capsules for University Events**

| Field | Detail |
|---|---|
| **ID** | FR-024 |
| **Title** | Featured Capsules for University Events |
| **Priority** | P4 — Medium-Low |
| **Description** | The system shall display a horizontally scrollable "Featured" section at the top of the Discover feed, showcasing capsules linked to active university events. |
| **Actors** | Authenticated User, Admin (curation) |
| **Preconditions** | At least one active UnlockEvent exists with linked, unlocked capsules. |
| **Main Flow** | 1. When the Discover feed loads, the system queries for UnlockEvents where `isActive = true`. 2. For each active event, the system retrieves associated unlocked capsules. 3. A "Featured: [Event Name]" section is displayed at the top of the Discover screen as a horizontal carousel. 4. Each featured card shows: capsule title, a preview image or media type icon, and the event name badge. 5. User taps a featured card to navigate to the capsule detail view. |
| **Alternative Flows** | **AF-1:** No active events or no featured capsules — the featured section is hidden entirely. |
| **Postconditions** | The featured section is displayed (or hidden if inapplicable). |
| **Acceptance Criteria** | **FR-024-AC1:** The featured section is displayed only when applicable. **FR-024-AC2:** The carousel supports horizontal scrolling with snap behaviour. **FR-024-AC3:** A maximum of 10 featured capsules are shown per event. |

---

#### 3.1.5 Notifications

---

**FR-025: Push Notification on Capsule Date-Unlock**

| Field | Detail |
|---|---|
| **ID** | FR-025 |
| **Title** | Push Notification on Capsule Date-Unlock |
| **Priority** | P3 — Medium |
| **Description** | The system shall send a push notification to the capsule owner and any subscriber when a capsule is unlocked due to its date condition being met. |
| **Actors** | System (NotificationWorker), Firebase Cloud Messaging |
| **Preconditions** | A capsule's `unlockDate` has passed. The user has granted notification permission (Android 13+ POST_NOTIFICATIONS). Notification preferences are enabled. |
| **Main Flow** | 1. The UnlockCheckWorker (FR-016) identifies a capsule whose `unlockDate <= currentTime`. 2. The worker marks the capsule as unlocked. 3. The worker creates a local notification using Android's NotificationManager on the "Capsule Unlocked" channel. 4. The notification title is "Capsule Unlocked! 🔓" and the body is "Your capsule '[Title]' is now open. Tap to view." 5. Tapping the notification navigates to the capsule detail screen (deep link via Navigation Component). 6. For community visibility: a Firestore Cloud Function (or the sync process) can optionally trigger an FCM message to followers (future enhancement). |
| **Alternative Flows** | **AF-1:** Notification permission not granted — the capsule is still unlocked but no notification is shown. The unlocked state is visible when the user opens the app. **AF-2:** Device is in Do Not Disturb mode — the notification is queued by the OS and delivered when DND ends. |
| **Postconditions** | A notification is displayed. The capsule is marked as unlocked. |
| **Acceptance Criteria** | **FR-025-AC1:** The notification is delivered within 1 minute of the unlock evaluation. **FR-025-AC2:** Tapping the notification opens the correct capsule detail screen. **FR-025-AC3:** The notification uses Android notification channels (required for API 26+). **FR-025-AC4:** The notification includes a meaningful title and body text. |

---

**FR-026: Push Notification on Capsule Location-Unlock**

| Field | Detail |
|---|---|
| **ID** | FR-026 |
| **Title** | Push Notification on Capsule Location-Unlock |
| **Priority** | P3 — Medium |
| **Description** | The system shall send a push notification when a user enters the geofence of a locked location-based capsule, triggering its unlock. |
| **Actors** | System (UnlockCheckWorker), Android Location Services |
| **Preconditions** | A locked capsule has a location-based unlock condition. The user has granted ACCESS_FINE_LOCATION permission. The user is within the geofence radius. |
| **Main Flow** | 1. The UnlockCheckWorker evaluates location-based capsules (FR-016). 2. The worker determines that the user's current location is within the capsule's geofence. 3. The capsule is marked as unlocked. 4. A local notification is created: "You've unlocked a capsule near [Location Name]! Tap to open." 5. Tapping the notification navigates to the capsule detail screen. |
| **Alternative Flows** | **AF-1:** Location services disabled — location-based capsules are not evaluated and no notification is sent. |
| **Postconditions** | The capsule is unlocked. A notification is displayed. |
| **Acceptance Criteria** | **FR-026-AC1:** The notification includes the approximate location name (if matched to a predefined campus landmark). **FR-026-AC2:** The location check uses the Fused Location Provider's last known location to minimise battery impact. **FR-026-AC3:** The notification deep-links to the unlocked capsule. |

---

**FR-027: In-App Notification Centre**

| Field | Detail |
|---|---|
| **ID** | FR-027 |
| **Title** | In-App Notification Centre |
| **Priority** | P3 — Medium |
| **Description** | The system shall provide an in-app notification centre listing recent events such as capsule unlocks, new comments on the user's capsules, and new favourites received. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. |
| **Main Flow** | 1. User taps the bell icon in the top app bar. 2. The system displays a list of recent notifications, sorted by timestamp (newest first). 3. Each notification shows: icon (unlock, comment, favourite), message text, relative timestamp. 4. Unread notifications are visually distinguished (bold text or accent colour). 5. User taps a notification to navigate to the relevant screen (e.g., capsule detail). 6. On tap, the notification is marked as read. |
| **Alternative Flows** | **AF-1:** No notifications exist — the system displays "No notifications yet." |
| **Postconditions** | Notifications are displayed. Tapped notifications are marked as read. |
| **Acceptance Criteria** | **FR-027-AC1:** The bell icon displays a badge count for unread notifications. **FR-027-AC2:** Notifications are persisted locally (not lost on app restart). **FR-027-AC3:** A "Mark All as Read" option is available. |

---

#### 3.1.6 Settings and Privacy

---

**FR-028: Location Services Consent Flow (GDPR-Aligned)**

| Field | Detail |
|---|---|
| **ID** | FR-028 |
| **Title** | Location Services Consent Flow |
| **Priority** | P3 — Medium |
| **Description** | The system shall present an explicit, GDPR-compliant consent flow for location data collection before any location services are activated. |
| **Actors** | Authenticated User |
| **Preconditions** | The user has just registered (first-time) or has not yet consented to location services. |
| **Main Flow** | 1. After registration (or on first launch post-update), the system displays a consent screen. 2. The screen explains: what location data is collected, why it is needed (capsule unlock, map features), how it is stored, and the user's right to withdraw consent. 3. Two prominent buttons are presented: "Allow Location Services" and "Not Now." 4. If the user consents, the system stores a `locationConsentGranted = true` flag in DataStore with a timestamp. 5. The system then requests the Android runtime location permission. 6. If the user declines, the system stores `locationConsentGranted = false`. Location-based features are disabled but the app remains fully functional for date-based and event-based capsules. |
| **Alternative Flows** | **AF-1:** User consents but denies the Android runtime permission — the system records the discrepancy and displays a guidance message. **AF-2:** User later changes their mind — consent can be managed in Settings (FR-029). |
| **Postconditions** | Consent state is recorded in DataStore. Location features are enabled or disabled accordingly. |
| **Acceptance Criteria** | **FR-028-AC1:** The consent screen is shown exactly once per user (or until the user makes a choice). **FR-028-AC2:** The consent explanation is written in plain, non-technical language. **FR-028-AC3:** No location data is collected before consent is granted. **FR-028-AC4:** Consent timestamp is recorded for audit purposes. |

---

**FR-029: Notification Preferences**

| Field | Detail |
|---|---|
| **ID** | FR-029 |
| **Title** | Notification Preferences |
| **Priority** | P3 — Medium |
| **Description** | The system shall allow the user to control which types of notifications they receive. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated. |
| **Main Flow** | 1. User navigates to Settings > Notifications. 2. The system displays toggle switches for: a. Capsule unlock notifications (date and location). b. Comment notifications (when someone comments on the user's capsule). c. Favourite notifications (when someone favourites the user's capsule). d. Featured capsule notifications (new featured content). 3. User toggles preferences. 4. Changes are stored in DataStore immediately. |
| **Alternative Flows** | **AF-1:** User revokes location consent — location-based unlock notifications are automatically disabled and greyed out. |
| **Postconditions** | Notification preferences are stored in DataStore. |
| **Acceptance Criteria** | **FR-029-AC1:** Preferences persist across app restarts. **FR-029-AC2:** Changes take effect immediately. **FR-029-AC3:** Default state for all toggles is "enabled." |

---

**FR-030: Data Export / Account Deletion (GDPR Right to Erasure)**

| Field | Detail |
|---|---|
| **ID** | FR-030 |
| **Title** | Data Export / Account Deletion |
| **Priority** | P4 — Medium-Low |
| **Description** | The system shall allow the user to export their personal data and/or permanently delete their account and all associated data. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated and has internet connectivity. |
| **Main Flow** | 1. User navigates to Settings > Privacy > "My Data." 2. **Export:** User taps "Export My Data." The system compiles all user data (profile, capsules, comments, favourites) into a JSON file and saves it to the device's Downloads directory. A share intent is offered. 3. **Delete Account:** User taps "Delete My Account." The system displays a destructive confirmation: "This will permanently delete your account, all your capsules, comments, and favourites. This cannot be undone." User confirms. The system: a. Deletes all local Room data for the user. b. Enqueues a WorkManager task to delete all Firestore documents, Firebase Storage files, and the Firebase Auth account. c. Signs the user out and navigates to the Welcome screen. |
| **Alternative Flows** | **AF-1:** Firestore deletion fails (offline) — the deletion task is queued with exponential backoff. **AF-2:** User cancels — no action taken. |
| **Postconditions** | **Export:** A JSON file is saved to the device. **Delete:** All user data is removed locally and queued for cloud deletion. |
| **Acceptance Criteria** | **FR-030-AC1:** The export file contains all personal data in a human-readable JSON format. **FR-030-AC2:** Account deletion removes all user data from Room immediately. **FR-030-AC3:** Cloud deletion completes within 24 hours (GDPR compliance). **FR-030-AC4:** The Firebase Auth account is deleted as part of the process. |

---

#### 3.1.7 Analytics (Innovation)

---

**FR-031: Insights Screen**

| Field | Detail |
|---|---|
| **ID** | FR-031 |
| **Title** | Insights Screen — Capsules Created Over Time, Top Locations, Media Type Breakdown |
| **Priority** | P4 — Medium-Low |
| **Description** | The system shall provide a personal analytics screen showing the user's capsule creation trends, popular locations, and media type distribution. |
| **Actors** | Authenticated User |
| **Preconditions** | The user is authenticated and has created at least one capsule. |
| **Main Flow** | 1. User navigates to "Insights" via the profile screen or bottom navigation overflow menu. 2. The system queries Room to compute analytics for the current user: a. **Capsules created per month:** A bar chart showing the number of capsules created in each of the last 12 months. b. **Media type breakdown:** A pie/donut chart showing the proportion of text, image, and audio capsules. c. **Top locations:** A list of the top 5 campus locations (by predefined landmark name) with capsule counts. d. **Total capsules created, total unlocked, total favourites received.** 3. Charts are rendered using a lightweight charting library (e.g., MPAndroidChart or a custom Canvas-based solution). 4. Data is computed from Room and optionally cached in the CapsuleInsight entity. |
| **Alternative Flows** | **AF-1:** User has no capsules — the screen displays an empty state: "Start creating capsules to see your insights." |
| **Postconditions** | The insights screen is displayed with current analytics data. |
| **Acceptance Criteria** | **FR-031-AC1:** The bar chart accurately represents capsules per month for the last 12 months. **FR-031-AC2:** The pie chart percentages sum to 100%. **FR-031-AC3:** The insights screen loads within 2 seconds. **FR-031-AC4:** Charts are accessible (include content descriptions for TalkBack). |

---

### 3.2 Non-Functional Requirements

#### 3.2.1 Performance

| ID | Requirement | Measurement |
|---|---|---|
| **NFR-001** | The application shall achieve a cold start time of under 2 seconds on a device with 3 GB RAM or more. | Measured using Android Studio Profiler from process creation to first frame rendered. |
| **NFR-002** | Room database queries shall complete within 100 milliseconds for lists of up to 500 records. | Measured using Room's query logging in debug builds. |
| **NFR-003** | Image loading with caching (Glide/Coil) shall display a cached image within 50 milliseconds and a network-loaded image within 2 seconds on a 4G connection. | Measured using Coil's event listeners. |
| **NFR-004** | The paginated capsule list shall load the next page within 300 milliseconds from Room. | Measured using Paging 3 load state listeners. |
| **NFR-005** | The application shall not drop below 30 frames per second during scrolling of capsule lists with image thumbnails. | Measured using Android GPU Profiler. |

#### 3.2.2 Scalability

| ID | Requirement |
|---|---|
| **NFR-006** | The Firestore document structure shall support growth to 10,000 capsules and 1,000 users without restructuring. Documents shall be organised in flat collections (not deeply nested subcollections) to allow efficient querying. |
| **NFR-007** | The Room database schema shall support index-based query optimisation on frequently filtered fields (`userId`, `isUnlocked`, `unlockType`, `createdAt`). |

#### 3.2.3 Reliability

| ID | Requirement |
|---|---|
| **NFR-008** | The application shall be fully functional offline for core operations: viewing cached capsules, creating new capsules, editing and deleting owned capsules. Data synchronisation shall occur automatically when internet connectivity is restored. |
| **NFR-009** | The WorkManager sync mechanism shall implement exponential backoff (initial delay 30 seconds, maximum 5 minutes) for failed sync operations. |
| **NFR-010** | The application shall not crash under normal usage conditions. All exceptions in coroutines shall be caught by a `CoroutineExceptionHandler` and logged, with a user-friendly error message displayed. |
| **NFR-011** | Background workers (UnlockCheckWorker, SyncWorker) shall be resilient to process death and shall resume on next WorkManager schedule. |

#### 3.2.4 Security

| ID | Requirement |
|---|---|
| **NFR-012** | User authentication shall be handled exclusively by Firebase Authentication. No passwords shall be stored locally on the device. |
| **NFR-013** | All network communication shall use HTTPS. No plaintext HTTP endpoints shall be used. |
| **NFR-014** | Firebase API keys and configuration shall be stored in `google-services.json` (excluded from version control via `.gitignore`). No API keys shall be hardcoded in source code. |
| **NFR-015** | The release build shall use R8/ProGuard for code obfuscation and shrinking. |
| **NFR-016** | Firestore security rules shall enforce that users can only read/write their own documents, and can only read unlocked public capsules. |

#### 3.2.5 Usability and Accessibility

| ID | Requirement |
|---|---|
| **NFR-017** | The application shall follow Material Design 3 guidelines for all UI components, including colour theming, typography scale, and component behaviour. |
| **NFR-018** | All interactive elements shall meet WCAG 2.1 AA contrast ratio requirements (minimum 4.5:1 for normal text, 3:1 for large text). |
| **NFR-019** | All images and icons shall have meaningful `contentDescription` attributes for TalkBack screen reader compatibility. |
| **NFR-020** | Text shall be scalable using the system font size setting (sp units for all text). The layout shall not break at 200% text scale. |
| **NFR-021** | Touch targets shall be a minimum of 48dp × 48dp as per Material Design accessibility guidelines. |
| **NFR-022** | The application shall support both light and dark themes, following the device system setting. |

#### 3.2.6 Maintainability

| ID | Requirement |
|---|---|
| **NFR-023** | The codebase shall follow the MVVM + Clean Architecture pattern with clearly separated Presentation, Domain, and Data layers. |
| **NFR-024** | Unit test coverage for business logic (Use Cases, Repositories, ViewModels) shall exceed 60%. |
| **NFR-025** | All public classes and functions shall include KDoc documentation comments. |
| **NFR-026** | The project shall use a consistent code style enforced by ktlint. |
| **NFR-027** | Git history shall demonstrate feature branching with meaningful commit messages following the Conventional Commits specification (e.g., `feat:`, `fix:`, `docs:`, `refactor:`). |

#### 3.2.7 Portability

| ID | Requirement |
|---|---|
| **NFR-028** | The application shall run on any Android device with API 26+ and 2 GB RAM minimum. |
| **NFR-029** | The application shall handle screen sizes from 4.7-inch phones to 10.5-inch tablets using responsive ConstraintLayout configurations. |
| **NFR-030** | The application shall function correctly in both portrait and landscape orientations, preserving state across configuration changes via ViewModel. |

#### 3.2.8 Privacy and Legal

| ID | Requirement |
|---|---|
| **NFR-031** | Location data shall only be collected after explicit user consent (GDPR Article 7), as implemented in FR-028. |
| **NFR-032** | The application shall include an in-app privacy policy accessible from the Settings screen. |
| **NFR-033** | Data minimisation: the application shall collect only data necessary for its stated functionality (display name, email, capsule content, location when consented). |
| **NFR-034** | Users shall have the right to export and delete their data (GDPR Articles 17 and 20), as implemented in FR-030. |
| **NFR-035** | The privacy policy shall clearly state what data is collected, how it is used, where it is stored (Firebase, EU/US regions), and how users can exercise their rights. |

---

### 3.3 External Interface Requirements

#### 3.3.1 User Interfaces

The application comprises the following screens, each accessible via the navigation graph:

| Screen | Description |
|---|---|
| **Welcome Screen** | Landing page for unauthenticated users with options to Sign In or Create Account. Displays the Echoes logo and tagline. |
| **Registration Screen** | Form with display name, email, password, and confirm password fields. "Create Account" button and link to Sign In. |
| **Login Screen** | Email and password fields. "Sign In" button, "Forgot Password?" link, and link to Registration. |
| **Password Reset Screen** | Email field and "Send Reset Link" button. |
| **Consent Screen** | GDPR-compliant location consent with explanation text and "Allow"/"Not Now" buttons. |
| **Home / Discover Feed** | Bottom navigation tab. Displays featured capsules carousel (if applicable) and the community feed of unlocked capsules. Pull-to-refresh support. |
| **My Capsules** | Bottom navigation tab. Personal archive of the user's own capsules with filter chips and sort dropdown. |
| **Map View** | Bottom navigation tab. Google Map with custom capsule markers and user location indicator. |
| **Create/Edit Capsule** | Full-screen form: media type selector, title, description, media capture/picker, unlock condition configurator, visibility toggle, "Seal Capsule" / "Save Changes" button. |
| **Capsule Detail** | Full content display for unlocked capsules (text, image with zoom, audio player). Metadata, favourite button, comments section. For locked capsules owned by the user: sealed overlay with unlock condition progress. |
| **Profile Screen** | User profile information, edit button, capsule statistics. |
| **Insights Screen** | Charts and statistics for the user's capsule activity. |
| **Notification Centre** | List of recent in-app notifications with read/unread state. |
| **Settings Screen** | Links to Notification Preferences, Privacy (consent management, data export, account deletion), About, and Sign Out. |
| **Admin: Event Management** | (Admin role only) Create/edit/activate UnlockEvents. List of defined events with status toggles. |

#### 3.3.2 Hardware Interfaces

| Hardware | Usage | Required | Fallback |
|---|---|---|---|
| **GPS Sensor** | Determine user location for geofence evaluation and map display. Accessed via Fused Location Provider. | Conditional (location features require it) | Date-based and event-based capsules function without GPS. |
| **Camera** | Capture photos for photo capsules via CameraX API. | Conditional (photo capture requires it) | Gallery picker is available as an alternative. |
| **Microphone** | Record audio for audio capsules via MediaRecorder API. | Conditional (audio capsules require it) | Text and photo capsule types remain available. |
| **Network Adapter (Wi-Fi / Mobile Data)** | Internet connectivity for Firebase services, cloud sync, and push notifications. | For initial registration and cloud features | Offline-first architecture enables core functionality without connectivity. |

#### 3.3.3 Software Interfaces

| Software Interface | Description | Protocol/Format |
|---|---|---|
| **Firebase Authentication** | User identity management (email/password sign-in, sign-up, password reset). | Firebase SDK (gRPC under the hood) |
| **Cloud Firestore** | Cloud document database for capsule, user, comment, favourite, and event data. | Firebase SDK (gRPC), JSON documents |
| **Firebase Cloud Storage** | Binary media file storage (images, audio files). | Firebase SDK, HTTPS upload/download |
| **Firebase Cloud Messaging (FCM)** | Push notification delivery to user devices. | FCM SDK, registration tokens |
| **Google Maps SDK for Android** | Campus map rendering, custom markers, info windows, user location overlay. | Maps SDK API, Google Maps API key |
| **Android Fused Location Provider** | High-accuracy location retrieval with battery optimisation. | Google Play Services Location API |
| **Android Room (SQLite)** | Local relational database for offline-first data persistence. | SQL via DAO interfaces |
| **Android WorkManager** | Scheduling and executing background tasks (sync, unlock evaluation). | WorkManager API |
| **Android DataStore (Preferences)** | Lightweight key-value storage for user preferences and consent flags. | DataStore API (Protocol Buffers / Preferences) |

#### 3.3.4 Communication Interfaces

| Interface | Protocol | Description |
|---|---|---|
| **Client ↔ Firebase Auth** | HTTPS (TLS 1.2+) / gRPC | Authentication requests and session management. |
| **Client ↔ Cloud Firestore** | HTTPS (TLS 1.2+) / gRPC | Document read/write operations with real-time listeners (optional) and batch writes. |
| **Client ↔ Firebase Storage** | HTTPS (TLS 1.2+) | File upload (multipart) and download with resumable upload support. |
| **FCM Server ↔ Client** | HTTPS / XMPP | Push notification delivery. The client registers a device token with FCM; notifications are routed via Firebase infrastructure. |
| **Client ↔ Google Maps API** | HTTPS | Map tile loading and geocoding (via the Maps SDK, transparent to the developer). |

---

### 3.4 System Features

| Feature Group | Priority | Description |
|---|---|---|
| **User Authentication** | Critical | Registration, login, logout, password reset — foundational for all other features. |
| **Capsule CRUD** | Critical | Create, read, update, and delete text, image, and audio capsules — the core product. |
| **Date-Based Unlocking** | High | Time capsules that open after a specified date — primary unlock mechanism. |
| **Local Persistence (Room)** | Critical | Offline-first data storage — ensures the app is usable without internet. |
| **Cloud Synchronisation (Firestore)** | High | Backup and multi-device access via Cloud Firestore. |
| **MVVM Architecture** | High | Clean separation of concerns for testability and maintainability. |
| **Filtering and Sorting** | High | User-friendly navigation through capsule collections. |
| **Location-Based Unlocking** | Medium | GPS geofencing — campus-specific feature differentiator. |
| **Push Notifications** | Medium | Timely alerts for capsule unlocks and community interactions. |
| **Material Design 3 / Accessibility** | Medium | Professional UI meeting accessibility standards. |
| **Community Features (Favourites/Comments)** | Medium-Low | Social engagement layer on unlocked capsules. |
| **Map View** | Medium-Low | Visual discovery of campus capsules. |
| **Featured Capsules** | Medium-Low | Event-driven content curation. |
| **Analytics/Insights** | Medium-Low | Personal data visualisation. |
| **GDPR Consent & Data Management** | Medium | Regulatory compliance and user trust. |
| **Campus Event Integration** | Low | Admin-defined event-based unlocking — adds institutional value. |
| **Offline-First Sync (WorkManager)** | High | Reliable background synchronisation with conflict resolution. |

---

## 4. Data Model

### 4.1 Conceptual Entity-Relationship Description

The Echoes data model comprises six core entities with the following relationships:

1. **User** — Represents a registered application user. Each User has a unique `userId` (matching their Firebase Auth UID). A User can own zero or more TimeCapsules, post zero or more Comments, and create zero or more Favourites.

2. **TimeCapsule** — The central entity. Each TimeCapsule is owned by exactly one User (many-to-one via `userId` foreign key). A TimeCapsule may be linked to zero or one UnlockEvent (optional many-to-one via `unlockEventId` foreign key). A TimeCapsule may receive zero or more Comments and zero or more Favourites.

3. **UnlockEvent** — Represents an admin-defined university event. One UnlockEvent can be linked to zero or more TimeCapsules. UnlockEvents have an `isActive` flag that, when set to true, triggers unlocking of all linked capsules.

4. **Comment** — A text comment posted by a User on a TimeCapsule. Each Comment belongs to exactly one TimeCapsule (many-to-one via `capsuleId`) and is authored by exactly one User (many-to-one via `userId`).

5. **Favourite** — A many-to-many relationship between User and TimeCapsule, modelled as an associative entity. Each Favourite links one User to one TimeCapsule, with a unique constraint on (`capsuleId`, `userId`).

6. **CapsuleInsight** — An aggregated analytics entity computed from TimeCapsule data. Each CapsuleInsight record belongs to one User and represents a monthly summary. This entity serves as a read-optimised cache for the Insights screen.

**Relationship Summary:**

- User (1) → (0..*) TimeCapsule
- User (1) → (0..*) Comment
- User (1) → (0..*) Favourite
- TimeCapsule (1) → (0..*) Comment
- TimeCapsule (1) → (0..*) Favourite
- UnlockEvent (1) → (0..*) TimeCapsule
- User (1) → (0..*) CapsuleInsight

*(A formal Entity-Relationship Diagram will be included in the submitted report.)*

### 4.2 Room Database Entities

#### 4.2.1 User

| Column | Type | Constraints | Description |
|---|---|---|---|
| `userId` | `String` | `@PrimaryKey` | Firebase Auth UID. |
| `email` | `String` | `NOT NULL` | User's email address. |
| `displayName` | `String` | `NOT NULL` | User's chosen display name (2–50 chars). |
| `profileImageUrl` | `String?` | Nullable | Firebase Storage URL for the profile image. |
| `createdAt` | `Long` | `NOT NULL` | Account creation timestamp (epoch millis, UTC). |
| `role` | `String` | `NOT NULL`, default `"USER"` | User role: `"USER"` or `"ADMIN"`. |
| `isSynced` | `Boolean` | `NOT NULL`, default `false` | Flag indicating whether the local record has been synced to Firestore. |

**Indices:** `userId` (primary key).

---

#### 4.2.2 TimeCapsule

| Column | Type | Constraints | Description |
|---|---|---|---|
| `capsuleId` | `String` | `@PrimaryKey` | UUID generated locally. |
| `userId` | `String` | `NOT NULL`, `@ForeignKey(User)` | Owner's UID. |
| `title` | `String` | `NOT NULL` | Capsule title (3–100 chars). |
| `description` | `String` | `NOT NULL` | Capsule body/description (10–5000 chars for text; optional for media). |
| `category` | `String?` | Nullable | Optional category tag: `"MEMORY"`, `"REFLECTION"`, `"MILESTONE"`, `"EVENT"`. |
| `mediaType` | `String` | `NOT NULL` | Enum stored as string: `"TEXT"`, `"IMAGE"`, `"AUDIO"`. |
| `mediaUri` | `String?` | Nullable | Local file URI for image or audio. Null for text-only capsules. |
| `mediaCloudUrl` | `String?` | Nullable | Firebase Storage download URL (populated after upload). |
| `unlockType` | `String` | `NOT NULL` | Enum stored as string: `"DATE"`, `"LOCATION"`, `"EVENT"`. |
| `unlockDate` | `Long?` | Nullable | Target unlock timestamp (epoch millis, UTC). Used when `unlockType = "DATE"`. |
| `unlockLatitude` | `Double?` | Nullable | Geofence centre latitude. Used when `unlockType = "LOCATION"`. |
| `unlockLongitude` | `Double?` | Nullable | Geofence centre longitude. Used when `unlockType = "LOCATION"`. |
| `unlockRadiusMeters` | `Float?` | Nullable | Geofence radius in metres (25–500). Used when `unlockType = "LOCATION"`. |
| `unlockLocationName` | `String?` | Nullable | Human-readable name of the campus landmark (e.g., "Library"). |
| `unlockEventId` | `String?` | Nullable, `@ForeignKey(UnlockEvent)` | FK to UnlockEvent. Used when `unlockType = "EVENT"`. |
| `isUnlocked` | `Boolean` | `NOT NULL`, default `false` | Whether the capsule has been unlocked. |
| `isPublic` | `Boolean` | `NOT NULL`, default `true` | Whether unlocked capsule is visible to the community. |
| `createdAt` | `Long` | `NOT NULL` | Creation timestamp (epoch millis, UTC). |
| `updatedAt` | `Long` | `NOT NULL` | Last modification timestamp (epoch millis, UTC). |
| `isSynced` | `Boolean` | `NOT NULL`, default `false` | Firestore sync flag. |

**Indices:** `userId`, `isUnlocked`, `unlockType`, `createdAt`, `unlockEventId`.

---

#### 4.2.3 UnlockEvent

| Column | Type | Constraints | Description |
|---|---|---|---|
| `eventId` | `String` | `@PrimaryKey` | UUID generated locally or from Firestore. |
| `eventName` | `String` | `NOT NULL` | Event name (e.g., "Graduation Week 2026"). |
| `eventDate` | `Long` | `NOT NULL` | Event date (epoch millis, UTC). |
| `description` | `String` | `NOT NULL` | Event description. |
| `isActive` | `Boolean` | `NOT NULL`, default `false` | Whether the event has been activated (triggering capsule unlocks). |
| `isSynced` | `Boolean` | `NOT NULL`, default `false` | Firestore sync flag. |

**Indices:** `isActive`, `eventDate`.

---

#### 4.2.4 Comment

| Column | Type | Constraints | Description |
|---|---|---|---|
| `commentId` | `String` | `@PrimaryKey` | UUID generated locally. |
| `capsuleId` | `String` | `NOT NULL`, `@ForeignKey(TimeCapsule, onDelete = CASCADE)` | FK to the parent capsule. |
| `userId` | `String` | `NOT NULL`, `@ForeignKey(User)` | FK to the comment author. |
| `content` | `String` | `NOT NULL` | Comment text (1–500 chars). |
| `createdAt` | `Long` | `NOT NULL` | Comment creation timestamp (epoch millis, UTC). |
| `isSynced` | `Boolean` | `NOT NULL`, default `false` | Firestore sync flag. |

**Indices:** `capsuleId`, `userId`, `createdAt`.

---

#### 4.2.5 Favourite

| Column | Type | Constraints | Description |
|---|---|---|---|
| `favouriteId` | `String` | `@PrimaryKey` | UUID generated locally. |
| `capsuleId` | `String` | `NOT NULL`, `@ForeignKey(TimeCapsule, onDelete = CASCADE)` | FK to the favourited capsule. |
| `userId` | `String` | `NOT NULL`, `@ForeignKey(User)` | FK to the user who favourited. |
| `createdAt` | `Long` | `NOT NULL` | Favourite creation timestamp (epoch millis, UTC). |
| `isSynced` | `Boolean` | `NOT NULL`, default `false` | Firestore sync flag. |

**Unique Constraint:** `(capsuleId, userId)` — a user can favourite a capsule only once.

**Indices:** `capsuleId`, `userId`.

---

#### 4.2.6 CapsuleInsight

| Column | Type | Constraints | Description |
|---|---|---|---|
| `insightId` | `String` | `@PrimaryKey` | UUID generated locally. |
| `userId` | `String` | `NOT NULL`, `@ForeignKey(User)` | FK to the user. |
| `month` | `Int` | `NOT NULL` | Month (1–12). |
| `year` | `Int` | `NOT NULL` | Year (e.g., 2026). |
| `capsulesCreated` | `Int` | `NOT NULL`, default `0` | Count of capsules created in this month. |
| `textCount` | `Int` | `NOT NULL`, default `0` | Count of text capsules. |
| `imageCount` | `Int` | `NOT NULL`, default `0` | Count of image capsules. |
| `audioCount` | `Int` | `NOT NULL`, default `0` | Count of audio capsules. |

**Unique Constraint:** `(userId, month, year)` — one insight record per user per month.

**Indices:** `userId`, `(month, year)`.

---

### 4.3 Firestore Collection Structure

The Firestore database mirrors the Room schema with the following collection hierarchy:

```
/users/{userId}
    ├── email: string
    ├── displayName: string
    ├── profileImageUrl: string (nullable)
    ├── createdAt: timestamp
    └── role: string

/capsules/{capsuleId}
    ├── userId: string
    ├── title: string
    ├── description: string
    ├── category: string (nullable)
    ├── mediaType: string ("TEXT" | "IMAGE" | "AUDIO")
    ├── mediaCloudUrl: string (nullable)
    ├── unlockType: string ("DATE" | "LOCATION" | "EVENT")
    ├── unlockDate: timestamp (nullable)
    ├── unlockLatitude: number (nullable)
    ├── unlockLongitude: number (nullable)
    ├── unlockRadiusMeters: number (nullable)
    ├── unlockLocationName: string (nullable)
    ├── unlockEventId: string (nullable)
    ├── isUnlocked: boolean
    ├── isPublic: boolean
    ├── createdAt: timestamp
    ├── updatedAt: timestamp
    └── favouriteCount: number (denormalised for query efficiency)

/comments/{commentId}
    ├── capsuleId: string
    ├── userId: string
    ├── content: string
    └── createdAt: timestamp

/favourites/{favouriteId}
    ├── capsuleId: string
    ├── userId: string
    └── createdAt: timestamp

/events/{eventId}
    ├── eventName: string
    ├── eventDate: timestamp
    ├── description: string
    └── isActive: boolean
```

**Design Rationale:**

- **Flat collection structure** — All entities are stored as top-level collections rather than subcollections. This enables straightforward querying across all capsules (e.g., "all unlocked public capsules") without collection group queries, which have indexing overhead.
- **Denormalised `favouriteCount`** — Stored on the capsule document to avoid an aggregation query when displaying capsule cards in the feed. Updated atomically using `FieldValue.increment()`.
- **Media files** — Stored in Firebase Storage under the path `/media/{userId}/{capsuleId}/{filename}`. The `mediaCloudUrl` field on the capsule document references the Storage download URL.

### 4.4 DataStore Keys

The application uses Jetpack DataStore (Preferences) for lightweight settings:

| Key | Type | Default | Description |
|---|---|---|---|
| `location_consent_granted` | `Boolean` | `false` | Whether the user has explicitly consented to location data collection. |
| `location_consent_timestamp` | `Long` | `0L` | Epoch millis when consent was granted (for audit). |
| `notification_capsule_unlock` | `Boolean` | `true` | Enable capsule unlock notifications. |
| `notification_comments` | `Boolean` | `true` | Enable comment notifications. |
| `notification_favourites` | `Boolean` | `true` | Enable favourite notifications. |
| `notification_featured` | `Boolean` | `true` | Enable featured capsule notifications. |
| `theme_mode` | `String` | `"SYSTEM"` | Theme preference: `"LIGHT"`, `"DARK"`, or `"SYSTEM"`. |
| `onboarding_completed` | `Boolean` | `false` | Whether the user has completed the initial onboarding/consent flow. |
| `last_sync_timestamp` | `Long` | `0L` | Epoch millis of the last successful Firestore sync. |

---

## 5. Architecture Design

### 5.1 Architecture Pattern: MVVM + Clean Architecture

Echoes adopts the **Model-View-ViewModel (MVVM)** pattern, extended with **Clean Architecture** principles as recommended by the Android Architecture Guide. This separation ensures:

- **Testability** — Business logic in Use Cases and ViewModels can be unit tested independently of the Android framework.
- **Maintainability** — Changes to the UI do not affect data handling logic, and vice versa.
- **Scalability** — New features can be added as self-contained modules without impacting existing functionality.

The architecture is organised into three distinct layers:

1. **Presentation Layer** — Fragments, ViewModels, UI state, and navigation.
2. **Domain Layer** — Use Cases (interactors) encapsulating business rules.
3. **Data Layer** — Repositories, Room DAOs, Firestore data sources, Firebase Storage, and DataStore.

### 5.2 Layer Descriptions

#### Presentation Layer

- **Fragments** — Each screen is implemented as a Fragment within a single-Activity architecture (using Navigation Component). Fragments observe `StateFlow` emissions from their associated ViewModel and render the UI accordingly.
- **ViewModels** — Extend `androidx.lifecycle.ViewModel` and are scoped to the navigation graph. ViewModels expose UI state via `StateFlow<UiState>` and accept user actions via public functions. ViewModels delegate business logic to Use Cases.
- **UI State** — Modelled as sealed classes or data classes (e.g., `CapsuleListUiState.Loading`, `CapsuleListUiState.Success(capsules)`, `CapsuleListUiState.Error(message)`). This ensures exhaustive handling of states in the UI.
- **Navigation** — Managed by the Navigation Component with a single `NavHostFragment`. Deep links are supported for notification tap-through to specific capsule detail screens.

#### Domain Layer

- **Use Cases** — Single-responsibility classes that encapsulate one business operation (e.g., `CreateCapsuleUseCase`, `EvaluateUnlockConditionsUseCase`, `ToggleFavouriteUseCase`). Use Cases depend on Repository interfaces (not implementations), enabling dependency inversion.
- **Domain Models** — Pure Kotlin data classes representing business entities, decoupled from Room entities and Firestore documents. Mappers convert between data layer entities and domain models.

#### Data Layer

- **Repositories** — Implement the Repository pattern. Each Repository (e.g., `CapsuleRepository`, `UserRepository`, `EventRepository`) mediates between the Domain layer and multiple data sources (Room, Firestore, Firebase Storage).
- **Local Data Source** — Room DAOs provide typed, compile-time-verified SQL queries against the SQLite database.
- **Remote Data Source** — Firestore and Firebase Storage wrappers handle cloud operations.
- **Sync Engine** — The `SyncWorker` (WorkManager) reconciles local and remote state using a last-write-wins strategy based on `updatedAt` timestamps.

### 5.3 Module Structure

The project is organised into feature-based packages within a single Gradle module (appropriate for the project's scope):

```
com.echoes.app/
├── di/                          # Hilt modules
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── FirebaseModule.kt
│   └── RepositoryModule.kt
├── data/
│   ├── local/
│   │   ├── db/
│   │   │   ├── EchoesDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── UserDao.kt
│   │   │   │   ├── CapsuleDao.kt
│   │   │   │   ├── CommentDao.kt
│   │   │   │   ├── FavouriteDao.kt
│   │   │   │   ├── EventDao.kt
│   │   │   │   └── InsightDao.kt
│   │   │   └── entity/
│   │   │       ├── UserEntity.kt
│   │   │       ├── TimeCapsuleEntity.kt
│   │   │       ├── CommentEntity.kt
│   │   │       ├── FavouriteEntity.kt
│   │   │       ├── UnlockEventEntity.kt
│   │   │       └── CapsuleInsightEntity.kt
│   │   └── datastore/
│   │       └── PreferencesManager.kt
│   ├── remote/
│   │   ├── firestore/
│   │   │   ├── CapsuleFirestoreSource.kt
│   │   │   ├── UserFirestoreSource.kt
│   │   │   └── EventFirestoreSource.kt
│   │   └── storage/
│   │       └── MediaStorageSource.kt
│   ├── repository/
│   │   ├── CapsuleRepositoryImpl.kt
│   │   ├── UserRepositoryImpl.kt
│   │   ├── EventRepositoryImpl.kt
│   │   ├── CommentRepositoryImpl.kt
│   │   └── FavouriteRepositoryImpl.kt
│   └── mapper/
│       ├── CapsuleMapper.kt
│       ├── UserMapper.kt
│       └── EventMapper.kt
├── domain/
│   ├── model/
│   │   ├── User.kt
│   │   ├── TimeCapsule.kt
│   │   ├── Comment.kt
│   │   ├── Favourite.kt
│   │   ├── UnlockEvent.kt
│   │   └── CapsuleInsight.kt
│   ├── repository/
│   │   ├── CapsuleRepository.kt
│   │   ├── UserRepository.kt
│   │   ├── EventRepository.kt
│   │   ├── CommentRepository.kt
│   │   └── FavouriteRepository.kt
│   └── usecase/
│       ├── auth/
│       │   ├── RegisterUseCase.kt
│       │   ├── LoginUseCase.kt
│       │   ├── LogoutUseCase.kt
│       │   └── ResetPasswordUseCase.kt
│       ├── capsule/
│       │   ├── CreateCapsuleUseCase.kt
│       │   ├── UpdateCapsuleUseCase.kt
│       │   ├── DeleteCapsuleUseCase.kt
│       │   ├── GetUserCapsulesUseCase.kt
│       │   └── GetCapsuleDetailUseCase.kt
│       ├── unlock/
│       │   ├── EvaluateUnlockConditionsUseCase.kt
│       │   └── ManualUnlockCheckUseCase.kt
│       ├── discovery/
│       │   ├── GetUnlockedCapsulesUseCase.kt
│       │   ├── SearchCapsulesUseCase.kt
│       │   └── GetFeaturedCapsulesUseCase.kt
│       ├── community/
│       │   ├── ToggleFavouriteUseCase.kt
│       │   ├── PostCommentUseCase.kt
│       │   └── GetCommentsUseCase.kt
│       └── analytics/
│           └── GetInsightsUseCase.kt
├── presentation/
│   ├── auth/
│   │   ├── LoginFragment.kt
│   │   ├── LoginViewModel.kt
│   │   ├── RegisterFragment.kt
│   │   └── RegisterViewModel.kt
│   ├── capsule/
│   │   ├── create/
│   │   │   ├── CreateCapsuleFragment.kt
│   │   │   └── CreateCapsuleViewModel.kt
│   │   ├── detail/
│   │   │   ├── CapsuleDetailFragment.kt
│   │   │   └── CapsuleDetailViewModel.kt
│   │   └── list/
│   │       ├── MyCapsulesFragment.kt
│   │       └── MyCapsulesViewModel.kt
│   ├── discovery/
│   │   ├── DiscoverFragment.kt
│   │   └── DiscoverViewModel.kt
│   ├── map/
│   │   ├── MapFragment.kt
│   │   └── MapViewModel.kt
│   ├── profile/
│   │   ├── ProfileFragment.kt
│   │   └── ProfileViewModel.kt
│   ├── insights/
│   │   ├── InsightsFragment.kt
│   │   └── InsightsViewModel.kt
│   ├── notifications/
│   │   ├── NotificationCentreFragment.kt
│   │   └── NotificationViewModel.kt
│   ├── settings/
│   │   ├── SettingsFragment.kt
│   │   └── SettingsViewModel.kt
│   ├── consent/
│   │   └── ConsentFragment.kt
│   ├── admin/
│   │   ├── EventManagementFragment.kt
│   │   └── EventManagementViewModel.kt
│   └── common/
│       ├── adapter/
│       │   ├── CapsuleListAdapter.kt
│       │   └── CommentListAdapter.kt
│       └── view/
│           └── AudioPlayerView.kt
├── worker/
│   ├── UnlockCheckWorker.kt
│   ├── SyncWorker.kt
│   └── NotificationWorker.kt
├── util/
│   ├── Constants.kt
│   ├── Extensions.kt
│   ├── DateUtils.kt
│   ├── LocationUtils.kt
│   └── InputValidator.kt
└── EchoesApplication.kt          # @HiltAndroidApp
```

### 5.4 Dependency Injection Strategy

Hilt (built on top of Dagger 2) provides compile-time dependency injection. The following Hilt modules are defined:

| Module | Scope | Provides |
|---|---|---|
| **AppModule** | `@Singleton` | Application context, DataStore instance, dispatchers (IO, Default, Main). |
| **DatabaseModule** | `@Singleton` | Room `EchoesDatabase` instance, all DAO instances. |
| **FirebaseModule** | `@Singleton` | `FirebaseAuth`, `FirebaseFirestore`, `FirebaseStorage`, `FirebaseMessaging` instances. |
| **RepositoryModule** | `@Singleton` | Binds repository interfaces to their implementations (e.g., `CapsuleRepository` → `CapsuleRepositoryImpl`). |

ViewModels are injected using `@HiltViewModel` with constructor injection. Use Cases are injected into ViewModels via constructor parameters.

### 5.5 Data Flow Diagram

The following describes the data flow for a typical user action (e.g., creating a capsule):

```
User Action (tap "Seal Capsule")
       │
       ▼
Fragment → calls ViewModel.createCapsule(data)
       │
       ▼
ViewModel → calls CreateCapsuleUseCase.invoke(capsule)
       │
       ▼
CreateCapsuleUseCase → validates business rules
       │                → calls CapsuleRepository.create(capsule)
       ▼
CapsuleRepositoryImpl
       │
       ├──► Room DAO: insert TimeCapsuleEntity (isSynced = false)
       │         → Returns success
       │
       ├──► WorkManager: enqueue SyncWorker (one-time)
       │
       └──► (If media) WorkManager: enqueue MediaUploadWorker
       │
       ▼
ViewModel ← receives Result<Success>
       │
       ▼
ViewModel → emits new UiState.Success via StateFlow
       │
       ▼
Fragment ← collects StateFlow
       │    → Updates UI (confirmation, navigates to detail)
       ▼
[Background: SyncWorker runs]
       │
       ├──► Queries Room for unsynced records
       ├──► Writes to Firestore
       ├──► Updates Room: isSynced = true
       └──► Uploads media to Firebase Storage
```

### 5.6 Offline-First Strategy

The offline-first architecture ensures that Echoes is fully usable without internet connectivity:

1. **Room as Single Source of Truth** — All reads are served from Room. The UI never reads directly from Firestore.
2. **Optimistic Local Writes** — Create, update, and delete operations are applied to Room immediately. A `isSynced = false` flag marks records pending cloud synchronisation.
3. **WorkManager Sync Queue** — After each local write, a `SyncWorker` is enqueued as a `OneTimeWorkRequest` with a network connectivity constraint (`Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)`). The worker processes all unsynced records in batch.
4. **Periodic Full Sync** — A `PeriodicWorkRequest` (every 6 hours) performs a full bidirectional sync: pulling new remote data and pushing local changes. Conflict resolution uses a last-write-wins strategy based on the `updatedAt` timestamp.
5. **Connectivity Listener** — A `NetworkCallback` monitors connectivity changes. When connectivity is restored, the sync worker is expedited.
6. **Graceful Degradation** — Features requiring real-time cloud data (e.g., community feed updates) display a "Last synced [timestamp]" indicator when offline. Push notifications are delivered only when online.

### 5.7 Background Processing

Three WorkManager workers handle background tasks:

| Worker | Type | Schedule | Constraints | Responsibility |
|---|---|---|---|---|
| **UnlockCheckWorker** | `PeriodicWorkRequest` | Every 15 minutes (minimum) | None (runs even offline for date checks) | Evaluates date-based, location-based, and event-based unlock conditions. Marks capsules as unlocked. Triggers local notifications. |
| **SyncWorker** | `OneTimeWorkRequest` (on data change) + `PeriodicWorkRequest` (every 6 hours) | On demand + periodic | `NetworkType.CONNECTED` | Syncs unsynced Room records to Firestore. Pulls remote changes. Uploads/downloads media files. |
| **NotificationWorker** | `OneTimeWorkRequest` | Triggered by UnlockCheckWorker | None | Constructs and displays local notifications for newly unlocked capsules using Android's NotificationManager and notification channels. |

All workers are registered with `ExistingPeriodicWorkPolicy.KEEP` (for periodic) or `ExistingWorkPolicy.APPEND_OR_REPLACE` (for one-time) to prevent duplicate work and ensure idempotent execution.

---

## 6. Security Requirements

### 6.1 Authentication

- Firebase Authentication handles all identity management. The application uses the **email/password** sign-in provider.
- No plaintext passwords are stored on the device at any time. Firebase Auth manages secure token-based sessions.
- Session tokens are managed by the Firebase SDK, which handles token refresh automatically.
- Re-authentication is required before sensitive operations (account deletion).
- The `FirebaseAuth.currentUser` property is checked on app startup to determine authentication state. If the session has expired, the user is redirected to the Login screen.

### 6.2 Data Encryption

- **In transit** — All communication with Firebase services (Auth, Firestore, Storage, FCM) uses TLS 1.2 or higher.
- **At rest (device)** — Room databases are stored in the application's private internal storage, protected by the Android application sandbox. On devices with full-disk encryption (standard from Android 10+), this provides encryption at rest.
- **Optional enhancement** — SQLCipher integration for Room database encryption could be added for sensitive deployments. This is documented as a future enhancement but is not required for the assessment scope.
- **Android Keystore** — If local credential caching is needed (e.g., biometric re-authentication), the Android Keystore system shall be used. For the current scope, Firebase handles credential management.

### 6.3 Input Validation and Sanitisation

All user inputs are validated on the client side before persistence:

| Input | Validation Rules |
|---|---|
| Email | Regex pattern: `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$`. Must not exceed 254 characters. |
| Password | Minimum 8 characters, at least one uppercase, one lowercase, one digit. Maximum 128 characters. |
| Display Name | 2–50 characters. Alphanumeric, spaces, hyphens, and apostrophes only. |
| Capsule Title | 3–100 characters. HTML tags stripped. |
| Capsule Description | 10–5000 characters for text capsules; 0–2000 for media capsules. HTML tags stripped. |
| Comment Content | 1–500 characters. HTML tags stripped. Prohibited keyword filter applied. |
| Unlock Date | Must be at least 1 hour in the future. Validated against `System.currentTimeMillis()`. |
| Geofence Radius | 25–500 metres (Float). |
| Geofence Coordinates | Latitude: −90.0 to 90.0. Longitude: −180.0 to 180.0. |

Input sanitisation strips HTML tags using a simple regex replacement (`<[^>]*>` → `""`) to prevent injection of formatted content.

### 6.4 Network Security

- All network communication uses HTTPS (enforced by Firebase SDKs).
- The `google-services.json` file is added to `.gitignore` and is not committed to the Git repository. A `google-services.json.example` template is provided.
- The Google Maps API key is restricted by Android package name and SHA-1 fingerprint in the Google Cloud Console.
- Firestore security rules enforce document-level access control (see Appendix C).
- Firebase Storage security rules restrict file access to authenticated users and limit file sizes.
- The release build enables R8 code shrinking and obfuscation to protect business logic.

### 6.5 Privacy

- **Consent-first** — No location data is collected before explicit user consent (FR-028).
- **Right to withdraw** — Users can revoke location consent at any time via Settings. Existing location data on their capsules is retained but no new location data is collected.
- **Data minimisation** — Only data necessary for application functionality is collected: display name, email, capsule content, and (with consent) location coordinates.
- **Data deletion** — Users can request full account and data deletion (FR-030). The system deletes all associated records from Room, Firestore, and Firebase Storage.
- **No third-party analytics** — The application does not integrate third-party analytics SDKs (e.g., Google Analytics, Firebase Analytics) to minimise data sharing.
- **Privacy policy** — An in-app privacy policy is accessible from the Settings screen, detailing data collection, usage, storage, and user rights.

---

## 7. Ethical Considerations

### 7.1 User-Generated Content Moderation

As Echoes enables community sharing of text, image, and audio content, a moderation mechanism is necessary:

- **Flag/Report mechanism** — Unlocked capsule detail screens include a "Report" option (overflow menu). Users can flag content as inappropriate, offensive, or spam.
- **Flagged content review** — Flagged capsules are marked in Firestore with a `flagCount` field. Capsules exceeding a threshold (e.g., 3 flags) are hidden from the community feed pending review.
- **Admin oversight** — Admin users can view flagged capsules and take action (hide, delete, or dismiss flags).
- **Limitation** — Automated content moderation (e.g., image classification, profanity filters) is beyond the current scope. The assessment scope supports a manual flag-based system.

### 7.2 Location Privacy

- Location data introduces specific privacy risks. The application mitigates these through:
  - **Explicit consent** — The GDPR-aligned consent flow (FR-028) explains exactly what location data is collected and why.
  - **User control** — Users can disable location services at any time without losing access to non-location features.
  - **Precision limitation** — Geofence locations are constrained to the St Mary's campus area. Precise home or personal location tracking is neither encouraged nor supported.
  - **No continuous tracking** — Location is checked periodically (every 15 minutes via WorkManager), not continuously tracked. The Fused Location Provider's last known location is used to minimise battery impact.

### 7.3 Data Protection

- The application complies with GDPR principles:
  - **Lawfulness, fairness, and transparency** — Clear privacy policy and consent mechanisms.
  - **Purpose limitation** — Data is used only for capsule functionality.
  - **Data minimisation** — Only necessary data is collected.
  - **Accuracy** — Users can edit their profile and capsule data.
  - **Storage limitation** — Deleted accounts are purged from all systems.
  - **Integrity and confidentiality** — Data protected by Firebase security rules and HTTPS.
- St Mary's University data handling policies are respected. The application does not access or store university academic records, student IDs, or other institutional data.

### 7.4 Accessibility and Inclusion

- The application follows Material Design 3 accessibility guidelines and WCAG 2.1 AA standards.
- All images include `contentDescription` attributes for screen reader users.
- Touch targets meet the 48dp minimum size requirement.
- Text uses `sp` units to respect the user's system font size preference.
- Colour contrast ratios meet WCAG AA minimums (4.5:1 for normal text).
- The application supports both light and dark themes.
- Audio capsules include a visual amplitude indicator for users who may not be able to hear playback.
- The application does not assume any particular level of technological literacy; onboarding screens guide new users through key features.

### 7.5 Responsible Content Policy

- The application's Terms of Use (displayed during registration) prohibit:
  - Harassment, hate speech, or discriminatory content.
  - Personally identifiable information about third parties without consent.
  - Copyrighted material without proper attribution.
  - Sexually explicit or violent content.
- The flag/report system (Section 7.1) provides a mechanism for community self-regulation.
- Capsules are immutable after unlocking (no edits allowed), ensuring that community-visible content matches what was originally sealed.

---

## 8. Testing Strategy

### 8.1 Unit Testing

**Framework:** JUnit 5 + MockK

**Scope:** All ViewModels, Use Cases, and Repository implementations.

**Approach:**
- ViewModels are tested by providing mocked Use Cases and verifying StateFlow emissions using Turbine.
- Use Cases are tested by providing mocked Repository interfaces and verifying correct invocations and return values.
- Repositories are tested by providing mocked DAOs and Firestore sources and verifying correct delegation and mapping.
- Input validation utility functions are tested with boundary values and edge cases.

**Example Test Cases:**
- `CreateCapsuleUseCaseTest`: Verify that a capsule with a past unlock date is rejected. Verify that a capsule without a title is rejected. Verify that a valid capsule is passed to the repository.
- `CapsuleDetailViewModelTest`: Verify that loading a locked capsule emits a `Locked` UI state. Verify that loading an unlocked capsule emits the full content.
- `EvaluateUnlockConditionsUseCaseTest`: Verify that a capsule with `unlockDate` in the past is marked as unlocked. Verify that a capsule with `unlockDate` in the future remains locked.

### 8.2 Integration Testing

**Framework:** AndroidX Test + Room in-memory database

**Scope:** Room DAO operations, Repository-to-Room integration, WorkManager workers.

**Approach:**
- Room DAOs are tested against an in-memory database instance using `Room.inMemoryDatabaseBuilder()`.
- Tests verify CRUD operations, query correctness (filtering, sorting, pagination), cascade deletes, and unique constraints.
- WorkManager workers are tested using `TestWorkerBuilder` and `TestListenableWorkerBuilder`.

**Example Test Cases:**
- `CapsuleDaoTest`: Insert a capsule with `isUnlocked = false`, query for locked capsules, verify it appears. Update `isUnlocked = true`, query again, verify it no longer appears in the locked list.
- `SyncWorkerTest`: Verify that unsynced records are identified and that the sync flag is updated after successful processing.

### 8.3 UI Testing

**Framework:** Espresso + AndroidX Test

**Scope:** Critical user flows (registration, login, capsule creation, capsule discovery).

**Approach:**
- End-to-end tests verify the complete user journey through the application.
- Tests use `ActivityScenario` and Espresso matchers to interact with UI elements.
- Firebase is configured to use the Firebase Emulator Suite during UI tests to avoid impacting production data.

**Example Test Cases:**
- `RegistrationFlowTest`: Launch the app, navigate to registration, fill in valid data, submit, verify navigation to consent screen, accept consent, verify navigation to home screen.
- `CreateTextCapsuleFlowTest`: Log in, tap "Create Capsule," select "Text," fill in title and body, set a date unlock, tap "Seal Capsule," verify the capsule appears in "My Capsules."
- `FavouriteToggleTest`: Navigate to an unlocked capsule, tap the favourite icon, verify the icon state changes and the count increments.

### 8.4 Flow Testing

**Framework:** Turbine

**Scope:** All StateFlow emissions from ViewModels.

**Approach:**
- Turbine provides a clean API for testing Flow emissions in a sequential, assertion-based manner.
- Tests verify that ViewModels emit the correct sequence of states (e.g., `Loading → Success` or `Loading → Error`).
- Tests verify that search debouncing works correctly (only the final emission after a pause is relevant).

**Example:**
```kotlin
@Test
fun `capsule list emits loading then success`() = runTest {
    val viewModel = MyCapsulesViewModel(mockGetUserCapsulesUseCase)
    viewModel.uiState.test {
        assertEquals(MyCapsulesUiState.Loading, awaitItem())
        assertEquals(MyCapsulesUiState.Success(expectedCapsules), awaitItem())
        cancelAndConsumeRemainingEvents()
    }
}
```

### 8.5 Performance Testing

**Tools:** Android Studio Profiler, LeakCanary (debug builds), Android GPU Profiler.

**Approach:**
- **Memory profiling** — Monitor memory allocation during capsule list scrolling with image thumbnails. Verify no memory leaks using LeakCanary.
- **CPU profiling** — Verify that Room queries execute on the IO dispatcher and do not block the main thread.
- **Frame rate** — Use the GPU Profiler to verify that the capsule feed maintains 30+ FPS during scrolling.
- **Cold start time** — Measure from process creation to first frame using `adb shell am start -W`.
- **Database performance** — Log Room query execution times in debug builds using a custom `RoomDatabase.QueryCallback`.

### 8.6 Test Coverage Targets

| Layer | Target Coverage | Rationale |
|---|---|---|
| **Use Cases** | ≥ 80% | Business logic is the most critical layer; high coverage prevents regressions. |
| **ViewModels** | ≥ 70% | StateFlow emission correctness is essential for UI accuracy. |
| **Repositories** | ≥ 60% | Integration complexity makes exhaustive testing valuable. |
| **Room DAOs** | ≥ 70% | Query correctness is critical for data integrity. |
| **UI (Espresso)** | ≥ 40% of critical flows | End-to-end tests are slower; focus on the highest-value user journeys. |
| **Overall** | ≥ 60% | Module requirement for First Class band. |

---

## 9. Constraints and Limitations

### Assessment Constraints

1. **Individual work** — The project must be developed entirely by a single student. No pair programming, collaborative coding, or delegation is permitted.
2. **Kotlin only** — Java code is not permitted. All source code must be written in Kotlin.
3. **Approved libraries** — Only widely-adopted, module-approved libraries may be used. Experimental or obscure libraries require justification.
4. **Submission deadline** — 8 May 2026. No extensions are anticipated.
5. **Report word limit** — The accompanying report is limited to 2,400 words. This SRS is a supplementary planning document.

### Firebase Free Tier Limits

| Resource | Spark Plan Limit | Mitigation |
|---|---|---|
| Firestore Storage | 1 GiB | Image compression, audio bitrate limits, data archiving. |
| Firestore Reads | 50,000/day | Pagination, local caching, efficient queries. |
| Firestore Writes | 20,000/day | Batch writes, sync debouncing. |
| Firebase Storage | 5 GB | Image compression (max 2 MB per image), audio limit (5 min at 128 kbps ≈ 4.7 MB per recording). |
| FCM Messages | 50,000/day | Well within expected usage. |
| Network Egress | 10 GiB/month | Image caching with Coil reduces repeated downloads. |

### Device Permission Requirements

The application requires the following runtime permissions, all of which must be requested at the point of use (not on install):

- `ACCESS_FINE_LOCATION` — For geofence evaluation and map user location.
- `ACCESS_COARSE_LOCATION` — Fallback location provider.
- `CAMERA` — For CameraX photo capture.
- `RECORD_AUDIO` — For MediaRecorder audio capsules.
- `POST_NOTIFICATIONS` (API 33+) — For displaying notifications.

If any permission is denied, the corresponding feature is gracefully disabled with a user-facing explanation.

### API Level Constraints

- **Minimum API 26** ensures notification channels, background execution limits, and adaptive icons are available.
- **Photo Picker API** requires API 33+; a fallback Intent-based picker is used for API 26–32.
- **POST_NOTIFICATIONS permission** was introduced in API 33; on API 26–32, notification permission is granted at install time.

---

## 10. Tech Stack Summary Table

| Category | Technology | Version | Justification |
|---|---|---|---|
| **Language** | Kotlin | 2.0+ | Module requirement. Concise, null-safe, coroutine-native. |
| **Minimum API** | Android API 26 | Android 8.0 Oreo | Covers ~97% of active devices. Enables notification channels and background limits. |
| **Target API** | Android API 35 | Android 15 | Targets the latest stable platform for optimal behaviour. |
| **Build Tool** | Gradle (Kotlin DSL) | 8.7+ | Standard Android build system with type-safe build scripts. |
| **IDE** | Android Studio Ladybug | 2024.2+ | Latest stable IDE with Kotlin 2.0 support. |
| **Architecture** | MVVM + Clean Architecture | — | Recommended by Android Architecture Guide. Ensures testability and separation of concerns. |
| **DI** | Hilt | 2.51+ (Dagger) / 1.3.0 (AndroidX Hilt) | Official Android DI solution. Reduces boilerplate vs. manual injection. |
| **ViewModel** | Jetpack ViewModel | 2.8+ | Lifecycle-aware state management. Survives configuration changes. |
| **State Management** | Kotlin StateFlow | Kotlin Coroutines 1.8+ | Type-safe, lifecycle-aware observable state. Preferred over LiveData for Kotlin-first projects. |
| **Local Database** | Room | 2.7.0 | Official Android ORM. Compile-time SQL verification. Type-safe queries. |
| **Navigation** | Navigation Component | 2.8+ | Single-activity architecture. Deep link support. Type-safe arguments. |
| **Background Work** | WorkManager | 2.11.2 | Guaranteed background execution. Handles constraints, retries, and chaining. |
| **Preferences** | DataStore (Preferences) | 1.1+ | Modern replacement for SharedPreferences. Coroutine-based, type-safe. |
| **Camera** | CameraX | 1.4+ | Simplified camera API. Lifecycle-aware. Handles device fragmentation. |
| **Pagination** | Paging 3 | 3.3+ | Efficient paginated data loading for large lists. |
| **Auth** | Firebase Authentication | BOM 34.12.0 | Secure, managed authentication. Email/password provider. |
| **Cloud Database** | Cloud Firestore | BOM 34.12.0 | Scalable NoSQL document database. Offline support. Real-time listeners. |
| **Push Notifications** | Firebase Cloud Messaging | BOM 34.12.0 | Reliable cross-platform push notification delivery. |
| **Cloud Storage** | Firebase Storage | BOM 34.12.0 | Scalable binary file storage. Integrates with Firebase Auth for security. |
| **Maps** | Google Maps SDK for Android | 20.0.0 | Industry-standard map rendering. Custom markers, clustering, info windows. |
| **Image Loading** | Coil | 2.7+ | Kotlin-first image loading library. Built on coroutines. Lightweight alternative to Glide. |
| **Audio** | Android MediaRecorder / MediaPlayer | Platform API | Built-in Android APIs. No external dependency needed. |
| **Photo Picker** | AndroidX Photo Picker / Intent fallback | Platform API | Privacy-focused photo selection (API 33+). Intent fallback for older APIs. |
| **UI Framework** | Material Design 3 (Material You) | Material 3 1.4.0 | Google's latest design system. Dynamic colour, accessibility, modern components. |
| **Layout** | ConstraintLayout / MotionLayout | 2.2.0 | Flexible flat layouts. MotionLayout for animations. |
| **Lists** | RecyclerView + DiffUtil | 1.4+ | Efficient list rendering with minimal UI updates. |
| **Unit Testing** | JUnit 5 | 5.10+ | Modern Java/Kotlin testing framework. Parameterised tests, nested classes. |
| **Mocking** | MockK | 1.13+ | Kotlin-native mocking library. Supports coroutines, extension functions. |
| **UI Testing** | Espresso | 3.6+ | Official Android UI testing framework. |
| **Flow Testing** | Turbine | 1.1+ | Clean API for testing Kotlin Flow/StateFlow emissions. |
| **Memory Leak Detection** | LeakCanary | 2.14+ | Automatic memory leak detection in debug builds. |
| **Version Control** | Git + GitHub | — | Industry-standard VCS. Feature branching, pull requests, commit history. |
| **Design** | Figma | — | Industry-standard prototyping tool. Free for individual use. |

---

## 11. Project Milestones / Development Roadmap

The development period spans approximately 3.5 weeks from 15 April to 8 May 2026.

### Week 1: 15–21 April 2026 — Foundation

| Day(s) | Task | Deliverable |
|---|---|---|
| 15–16 Apr | Project setup: Android Studio project, Gradle configuration, Hilt setup, Room database, Navigation graph, GitHub repo initialisation | Compilable project skeleton with DI and navigation. |
| 16–17 Apr | Firebase integration: Firebase Auth, Firestore, Storage, FCM configuration | Firebase services connected and verified. |
| 17–18 Apr | Authentication: Registration, Login, Logout, Password Reset (FR-001 to FR-004) | Full auth flow functional with Firebase. |
| 19–20 Apr | Data model: Room entities, DAOs, Firestore data sources, mappers, repository implementations | Complete data layer with Room persistence. |
| 20–21 Apr | Create Text Capsule (FR-006), Capsule List (FR-011), Capsule Detail (FR-012), Edit (FR-009), Delete (FR-010) | Basic CRUD complete. **Milestone: MVP (P0) complete.** |

### Week 2: 22–28 April 2026 — Media, Unlocking, Sync

| Day(s) | Task | Deliverable |
|---|---|---|
| 22–23 Apr | Photo Capsule (FR-007): CameraX integration, gallery picker, image compression | Photo capsules functional. |
| 23–24 Apr | Audio Capsule (FR-008): MediaRecorder integration, playback UI | Audio capsules functional. **Milestone: P1 complete.** |
| 24–25 Apr | Date-based unlocking (FR-013, FR-016), UnlockCheckWorker | Date-based unlock working. |
| 25–26 Apr | Filtering and sorting (FR-021), search (FR-020) | List filtering/sorting operational. |
| 26–27 Apr | Firestore sync: SyncWorker, offline-first architecture, WorkManager integration | Offline-first sync functional. |
| 27–28 Apr | Community feed (FR-018), profile view/edit (FR-005) | Discover tab and profile functional. **Milestone: P2 complete.** |

### Week 3: 29 April – 5 May 2026 — Advanced Features

| Day(s) | Task | Deliverable |
|---|---|---|
| 29–30 Apr | Location-based unlocking (FR-014, FR-016 extension), geofence UI on map | Geofence unlock working. |
| 30 Apr – 1 May | Push notifications (FR-025, FR-026, FR-027), notification channels | Notifications functional. **Milestone: P3 complete.** |
| 1–2 May | Material Design 3 theming, accessibility audit (contrast, contentDescription, touch targets) | WCAG AA compliance verified. |
| 2–3 May | Map view (FR-019): Google Maps integration, custom markers, clustering | Map view functional. |
| 3–4 May | Community features: Favourites (FR-022), Comments (FR-023), Featured (FR-024) | Social features complete. |
| 4–5 May | Analytics/Insights (FR-031), Event management (FR-015), GDPR consent (FR-028), data export/delete (FR-030) | Innovation features complete. **Milestone: P4 complete.** |

### Week 3.5: 5–8 May 2026 — Polish, Testing, Submission

| Day(s) | Task | Deliverable |
|---|---|---|
| 5–6 May | Unit testing (ViewModels, Use Cases, Repositories), integration testing (DAOs), flow testing (Turbine) | Test suite with ≥60% coverage. |
| 6–7 May | UI testing (Espresso), performance profiling, LeakCanary verification, bug fixes | Performance baselines met. Known bugs resolved. |
| 7 May | Final code review, KDoc comments, README, code cleanup, Git history review | Clean codebase ready for submission. |
| 8 May | Report writing/finalisation, submission | **Final submission.** |

---

## 12. Glossary

| Term | Definition |
|---|---|
| **Capsule** | A digital time capsule created by a user, containing text, an image, or an audio recording, bound to unlock conditions. |
| **Clean Architecture** | An architectural approach by Robert C. Martin that separates software into concentric layers (Entities, Use Cases, Interface Adapters, Frameworks) with a strict dependency rule. |
| **Cold Start** | The launch of an application when its process is not already in memory. |
| **Conventional Commits** | A specification for adding human and machine-readable meaning to commit messages (e.g., `feat:`, `fix:`). |
| **DiffUtil** | A RecyclerView utility that calculates the minimal set of updates needed to transform one list into another. |
| **Fused Location Provider** | A Google Play Services API that intelligently combines GPS, Wi-Fi, and cellular data for optimal location accuracy with minimal battery impact. |
| **Geofence** | A virtual geographic boundary defined by a centre point (latitude/longitude) and a radius. |
| **Hilt** | An Android dependency injection library built on Dagger 2, providing compile-time dependency injection with reduced boilerplate. |
| **MVVM** | Model-View-ViewModel — an architectural pattern where the ViewModel exposes data and commands to the View and mediates between the View and the Model. |
| **Offline-First** | An architecture where the local database is the primary data source, with cloud synchronisation occurring in the background. |
| **Paging 3** | A Jetpack library that loads data in pages, enabling efficient display of large data sets in RecyclerView. |
| **Repository Pattern** | A design pattern that abstracts data access, providing a clean API to the domain layer regardless of the underlying data source. |
| **Room** | An Android Jetpack library providing an abstraction layer over SQLite, with compile-time verification of SQL queries. |
| **Sealed Class** | A Kotlin class that restricts which classes can inherit from it, enabling exhaustive `when` expressions. |
| **Spark Plan** | Firebase's free pricing tier with limited resource quotas. |
| **StateFlow** | A Kotlin coroutines hot flow that always holds a value and emits updates to collectors. |
| **Turbine** | A testing library for Kotlin Flow that provides a sequential, assertion-based API for verifying flow emissions. |
| **UUID** | Universally Unique Identifier — a 128-bit identifier used to uniquely identify records without centralised coordination. |
| **WorkManager** | A Jetpack library for scheduling deferrable, guaranteed background work that survives process death and device reboots. |

---

## 13. Appendices

### Appendix A: Screen Inventory

| # | Screen Name | Access Point | Key Components |
|---|---|---|---|
| 1 | Welcome | App launch (unauthenticated) | Logo, tagline, "Sign In" button, "Create Account" button |
| 2 | Registration | Welcome → "Create Account" | Display name, email, password, confirm password fields; "Create Account" button |
| 3 | Login | Welcome → "Sign In" | Email, password fields; "Sign In" button; "Forgot Password?" link |
| 4 | Password Reset | Login → "Forgot Password?" | Email field; "Send Reset Link" button |
| 5 | Consent | Post-registration | Location consent explanation; "Allow" / "Not Now" buttons |
| 6 | Home / Discover Feed | Bottom nav — "Discover" | Featured carousel (optional), unlocked capsules feed, search icon, filter chips |
| 7 | My Capsules | Bottom nav — "My Capsules" | Personal capsule list, filter/sort controls, FAB (Create Capsule) |
| 8 | Map View | Bottom nav — "Map" | Google Map, capsule markers, user location, info windows |
| 9 | Create Capsule | FAB on My Capsules / Home | Media type selector, title, description, media capture/picker, unlock configurator, visibility toggle |
| 10 | Edit Capsule | Capsule Detail → "Edit" | Same as Create Capsule, pre-populated |
| 11 | Capsule Detail (Locked) | My Capsules → tap locked capsule | Sealed overlay, title, unlock condition progress, edit/delete options |
| 12 | Capsule Detail (Unlocked) | Any list → tap unlocked capsule | Full content (text/image/audio), metadata, favourite button, comments section |
| 13 | Profile | Bottom nav — "Profile" or Settings | Display name, email, profile image, stats, "Edit Profile" button |
| 14 | Edit Profile | Profile → "Edit Profile" | Editable display name, image picker |
| 15 | Insights | Profile → "Insights" | Bar chart (monthly capsules), pie chart (media types), top locations list, summary stats |
| 16 | Notification Centre | Top app bar → bell icon | Notification list with read/unread states |
| 17 | Settings | Bottom nav overflow or Profile | Notification preferences, privacy (consent, data export, delete account), about, sign out |
| 18 | Notification Preferences | Settings → "Notifications" | Toggle switches for each notification type |
| 19 | Privacy / My Data | Settings → "Privacy" | Location consent toggle, "Export My Data" button, "Delete My Account" button |
| 20 | Admin: Event Management | Settings (Admin role only) | Event list, create/edit/activate event forms |

---

### Appendix B: Permission Manifest

```xml
<!-- Internet access for Firebase services -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Location for geofence-based capsule unlocking -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Camera for photo capsule capture -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Microphone for audio capsule recording -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />

<!-- Notifications (API 33+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Foreground service for WorkManager (location-aware sync) -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />

<!-- Wake lock for WorkManager -->
<uses-permission android:name="android.permission.WAKE_LOCK" />

<!-- Receive boot completed for rescheduling WorkManager tasks -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

**Permission Justification Table:**

| Permission | Justification | Required | Fallback |
|---|---|---|---|
| `INTERNET` | Firebase Auth, Firestore, Storage, FCM, Google Maps | Yes | Offline mode (limited) |
| `ACCESS_NETWORK_STATE` | WorkManager connectivity constraints | Yes | — |
| `ACCESS_FINE_LOCATION` | Geofence evaluation, map user location | Conditional | Date/event unlock types |
| `ACCESS_COARSE_LOCATION` | Fallback for approximate location | Conditional | Date/event unlock types |
| `CAMERA` | CameraX photo capture | Conditional | Gallery picker |
| `RECORD_AUDIO` | MediaRecorder audio recording | Conditional | Text/photo capsule types |
| `POST_NOTIFICATIONS` | Push notifications for capsule unlocks | Conditional (API 33+) | In-app notification centre |
| `FOREGROUND_SERVICE` | Long-running sync tasks | Yes | — |
| `WAKE_LOCK` | WorkManager reliability | Yes | — |
| `RECEIVE_BOOT_COMPLETED` | Reschedule periodic workers after reboot | Yes | Workers reschedule on next app launch |

---

### Appendix C: Firebase Security Rules

#### Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Helper function: Check if user is authenticated
    function isAuthenticated() {
      return request.auth != null;
    }

    // Helper function: Check if user owns the document
    function isOwner(userId) {
      return request.auth.uid == userId;
    }

    // Helper function: Check if user is an admin
    function isAdmin() {
      return isAuthenticated() &&
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == "ADMIN";
    }

    // Users collection
    match /users/{userId} {
      // Users can read their own profile; others can read display name and profile image
      allow read: if isAuthenticated();
      // Users can only write their own profile
      allow create: if isOwner(userId);
      allow update: if isOwner(userId);
      allow delete: if isOwner(userId);
    }

    // Capsules collection
    match /capsules/{capsuleId} {
      // Anyone authenticated can read unlocked, public capsules
      // Owners can read their own capsules (locked or unlocked)
      allow read: if isAuthenticated() &&
        (resource.data.isUnlocked == true && resource.data.isPublic == true) ||
        isOwner(resource.data.userId);
      // Only the owner can create capsules
      allow create: if isAuthenticated() &&
        isOwner(request.resource.data.userId);
      // Only the owner can update their capsules
      allow update: if isAuthenticated() &&
        isOwner(resource.data.userId);
      // Only the owner can delete their capsules
      allow delete: if isAuthenticated() &&
        isOwner(resource.data.userId);
    }

    // Comments collection
    match /comments/{commentId} {
      // Anyone authenticated can read comments on unlocked capsules
      allow read: if isAuthenticated();
      // Authenticated users can create comments
      allow create: if isAuthenticated() &&
        isOwner(request.resource.data.userId);
      // Only the comment author can delete their comment
      allow delete: if isAuthenticated() &&
        isOwner(resource.data.userId);
      // Comments are immutable (no updates)
      allow update: if false;
    }

    // Favourites collection
    match /favourites/{favouriteId} {
      allow read: if isAuthenticated();
      allow create: if isAuthenticated() &&
        isOwner(request.resource.data.userId);
      allow delete: if isAuthenticated() &&
        isOwner(resource.data.userId);
      allow update: if false;
    }

    // Events collection
    match /events/{eventId} {
      // All authenticated users can read events
      allow read: if isAuthenticated();
      // Only admins can create, update, or delete events
      allow create: if isAdmin();
      allow update: if isAdmin();
      allow delete: if isAdmin();
    }
  }
}
```

#### Firebase Storage Security Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Media files: /media/{userId}/{capsuleId}/{filename}
    match /media/{userId}/{capsuleId}/{filename} {
      // Authenticated users can read media for unlocked capsules
      // (Application-level check ensures capsule is unlocked)
      allow read: if request.auth != null;

      // Only the owner can upload media
      allow write: if request.auth != null &&
        request.auth.uid == userId &&
        request.resource.size < 10 * 1024 * 1024; // Max 10 MB
    }

    // Profile images: /profiles/{userId}/{filename}
    match /profiles/{userId}/{filename} {
      allow read: if request.auth != null;
      allow write: if request.auth != null &&
        request.auth.uid == userId &&
        request.resource.size < 1 * 1024 * 1024; // Max 1 MB
    }
  }
}
```

---

### Appendix D: Gradle Dependencies List

```kotlin
// build.gradle.kts (Project level)
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}

// build.gradle.kts (App level)
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.echoes.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.echoes.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Material Design 3
    implementation("com.google.android.material:material:1.12.0")

    // Jetpack Lifecycle + ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.6")

    // Room
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")
    implementation("androidx.room:room-paging:2.7.0")

    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.3.5")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.2")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.11.2")

    // CameraX
    implementation("androidx.camera:camera-core:1.4.1")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-work:1.3.0")
    ksp("androidx.hilt:hilt-compiler:1.3.0")

    // Firebase (BOM)
    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:20.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.maps.android:android-maps-utils:3.8.2")

    // Image Loading (Coil)
    implementation("io.coil-kt:coil:2.7.0")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // LeakCanary (Debug only)
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    // ---- Testing ----

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.3")

    // MockK
    testImplementation("io.mockk:mockk:1.13.13")

    // Turbine (Flow testing)
    testImplementation("app.cash.turbine:turbine:1.1.0")

    // Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    // AndroidX Test
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")

    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.6.1")

    // Room Testing
    androidTestImplementation("androidx.room:room-testing:2.7.0")

    // WorkManager Testing
    androidTestImplementation("androidx.work:work-testing:2.11.2")

    // Hilt Testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.51.1")
}
```

---

*End of Document*

**Software Requirements Specification — Echoes: Digital Time Capsule Application**
**Version 1.0 — 15 April 2026**
**CPS5012 Mobile Application Development — St Mary's University, Twickenham, London**
