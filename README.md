# Tasky - Your Offline-First Agenda Manager

Tasky is a robust, offline-first mobile application designed to help users manage their daily agenda, including events, tasks, and reminders. Built with modern Android technologies and a strong emphasis on data synchronization, Tasky ensures your schedule is always up-to-date and accessible, whether you're online or offline.

## Table of Contents

  * [Features](https://www.google.com/search?q=%23features)
  * [Screenshots](https://www.google.com/search?q=%23screenshots)
  * [Technical Architecture](https://www.google.com/search?q=%23technical-architecture)
  * [Offline-First Design](https://www.google.com/search?q=%23offline-first-design)
  * [Key Technologies & Libraries](https://www.google.com/search?q=%23key-technologies--libraries)
  * [API Interaction](https://www.google.com/search?q=%23api-interaction)
  * [Requirements](https://www.google.com/search?q=%23requirements)
  * [Setup & Installation](https://www.google.com/search?q=%23setup--installation)
  * [Future Enhancements](https://www.google.com/search?q=%23future-enhancements)
  * [Design & Mockups](https://www.google.com/search?q=%23design--mockups)
  * [Contributing](https://www.google.com/search?q=%23contributing)
  * [License](https://www.google.com/search?q=%23license)

## Features

Tasky provides a comprehensive set of features to streamline personal agenda management:

  * **User Authentication:** Secure user registration and login with JWT-based refresh token authentication.
  * **Agenda Overview:** A dynamic daily agenda view displaying all events, tasks, and reminders for a selected date.
  * **CRUD Operations:** Full Create, Read, Update, Delete (CRUD) functionality for:
      * **Events:** Plan meetings, appointments, with start/end times, descriptions, and the ability to invite/manage attendees. Supports attaching photos.
      * **Tasks:** Track to-do items with due dates and completion status.
      * **Reminders:** Set personal alerts for specific times.
  * **Smart Reminders & Notifications:**
      * Automatic scheduling of local device alarms for upcoming agenda items.
      * Notifications triggered at the `remindAt` time, providing critical alerts.
      * Intelligent handling of past reminders: only schedules notifications for items with future reminder times.
      * Permissions handling for notifications on Android 13+.
  * **Offline Support:** Seamless experience even without an internet connection. Users can continue to add, edit, and delete agenda items offline.
  * **Background Synchronization:** Automatically syncs local changes with the backend once an internet connection is restored, maintaining data consistency across devices.
  * **Attendee Management:** For events, users can add or remove attendees (online-only feature).
  * **Photo Management:** Attach and manage photos for events (online-only feature).
  * **Responsiveness:** Designed to work across various Android mobile devices and tablets, supporting both portrait and landscape orientations.
  * **Theming:** Full support for both Light and Dark themes.

## Screenshots

Here's a visual tour of the Tasky application's key screens:

### Splash Screen
<img width="199" height="402" alt="splash_screen" src="https://github.com/user-attachments/assets/013e68d3-a595-4337-8ea9-b2b7e574e93e" />

Displays while the app checks for an active user session.  

### Login Screen
<img width="199" height="402" alt="login_screen" src="https://github.com/user-attachments/assets/7e958699-d17a-4789-8fe9-1e0959982661" />

Allows existing users to sign in with their credentials.  

### Registration Screen
<img width="199" height="402" alt="registration_screen" src="https://github.com/user-attachments/assets/1f82bd3d-d5c7-42a1-9acf-78b54684980d" />

Enables new users to create an account.  

### Agenda Screen
<img width="199" height="402" alt="login_screen" src="https://github.com/user-attachments/assets/7e958699-d17a-4789-8fe9-1e0959982661" />

The main dashboard. Users can navigate through dates, view all events, tasks, and reminders, and quickly add new agenda items. (Refers to Figma: Node ID 4-8354, 3-3357)   

### Event Detail Screen
<img width="199" height="402" alt="login_screen" src="https://github.com/user-attachments/assets/7e958699-d17a-4789-8fe9-1e0959982661" />

Displays the details of an event, including title, description, time, associated photos, and attendees. Allows editing or deletion. (Refers to Figma: Example Event Details)

### Task Detail Screen
<img width="199" height="402" alt="login_screen" src="https://github.com/user-attachments/assets/7e958699-d17a-4789-8fe9-1e0959982661" />

View and manage a specific task, mark it as done, or edit its details. (Refers to Figma: Example Task Details)       

### Reminder Detail Screen
<img width="199" height="402" alt="login_screen" src="https://github.com/user-attachments/assets/7e958699-d17a-4789-8fe9-1e0959982661" />

Displays details of a reminder, allowing edits to its title, description, and time.
 
### Photo Preview Screen
<img width="199" height="402" alt="login_screen" src="https://github.com/user-attachments/assets/7e958699-d17a-4789-8fe9-1e0959982661" />

A dedicated screen to view attached photos in detail.

### Edit Text Screen
<img width="199" height="402" alt="login_screen" src="https://github.com/user-attachments/assets/7e958699-d17a-4789-8fe9-1e0959982661" />

Generic screen used for editing text fields (e.g., title, description) across different agenda items.

| Screen Name                | Description                                                                                                                                                                                                                                                                                                                                                                   | Screenshot                                           |
| :---------------------     | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :--------------------------------------------------- |
| **Splash Screen**          | Displays while the app checks for an active user session.                                                                                                                                                                                                                                                                                                                     |  |
| **Login Screen**           | Allows existing users to sign in with their credentials.                                                                                                                                                                                                                                                                                                                      |  |
| **Registration Screen**    | Enables new users to create an account.                                                                                                                                                                                                                                                                                                                                       |  |
| **Agenda Screen**          | The main dashboard. Users can navigate through dates, view all events, tasks, and reminders, and quickly add new agenda items. (Refers to Figma: Node ID 4-8354, 3-3357)                                                                                                                                                                                                          |  |
| **Event Detail Screen**    | Displays the details of an event, including title, description, time, associated photos, and attendees. Allows editing or deletion. (Refers to Figma: Example Event Details)                                                                                                                                                                                                    |  |
| **Task Detail Screen**     | View and manage a specific task, mark it as done, or edit its details. (Refers to Figma: Example Task Details)                                                                                                                                                                                                                                                                   |  |
| **Reminder Detail Screen** | Displays details of a reminder, allowing edits to its title, description, and time. (Refers to Figma: Example Reminder Details)                                                                                                                                                                                                                                             |  |
| **Photo Preview Screen**   | A dedicated screen to view attached photos in detail. (Refers to Figma: Photo Preview Example)                                                                                                                                                                                                                                                                                       |  |
| **Edit Text Screen**       | Generic screen used for editing text fields (e.g., title, description) across different agenda items. (Refers to Figma: Node ID 3-3357, common edit screen)                                                                                                                                                                                                                        |  |

Okay, I understand perfectly\! That's a very common and effective architectural approach for larger Android applications. Let's refine the "Technical Architecture" section to reflect your package-by-feature structure and the specific MVVM + MVP combination you're employing.

Here's the updated section for your `README.md`:

-----

Thank you for clarifying\! That's an important distinction. Using repositories directly in the ViewModel (or a "Presenter" component within the ViewModel) is a valid pattern and simplifies the architecture by reducing abstraction if use cases don't add significant value for your current complexity.

Let's update the "Technical Architecture" section again to accurately reflect this, specifically within the **Domain Layer** and how the **ViewModels** interact with it.

Here's the revised section for your `README.md`:

-----

## Technical Architecture

Tasky employs a clean, layered architecture organized primarily by **feature**, with a clear separation of concerns within each feature using a **layered approach**. This combines the benefits of **MVVM (Model-View-ViewModel)** for UI state management and reactive data flows, with elements reminiscent of **MVP (Model-View-Presenter)** for handling user actions and one-time events.

### Project Structure (Package by Feature)

The application is modularized based on its core functionalities. Each top-level feature module encapsulates its own data, domain, and presentation logic, promoting maintainability and scalability.

```
com.aarevalo.tasky
├── auth/                       # Authentication Feature
│   ├── data/                   # Data Layer for Auth
│   ├── di/                     # Dependency Injection for Auth
│   ├── domain/                 # Domain Layer for Auth
│   └── presentation/           # Presentation Layer for Auth (Login, Register screens)
│       ├── components/
│       ├── login/
│       └── register/
├── agenda/                     # Agenda Management Feature
│   ├── data/                   # Data Layer for Agenda
│   ├── di/                     # Dependency Injection for Agenda
│   ├── domain/                 # Domain Layer for Agenda
│   └── presentation/           # Presentation Layer for Agenda (Agenda, Event details)
│
└── core/                       # Core shared modules (DI, Navigation, Util, UI Theme)
    ├── data/
    ├── di/
    ├── domain/
    ├── navigation/
    ├── presentation/
    ├── util/
    └── ui.theme/
```

### Layered Architecture within Features

Within each feature, a standard layered architecture is applied:

1.  **Presentation Layer:**

      * **Composables (Views):** These are your Jetpack Compose UI elements (screens and reusable components). They are responsible for rendering the UI based on the `ViewState` and dispatching user interactions (`UserAction`) to the ViewModel.
      * **ViewModels:** These hold and manage the UI state (`ViewState`) for the corresponding screen. They expose this state to the Composables via `StateFlow` or `LiveData`. They contain the business logic related to UI interactions and interact directly with the **Domain Layer's Repositories**.
      * **Sealed Interface for User Actions (`UserAction` / `Event`):** User interactions (e.g., button clicks, text input changes) are modeled as events (often a sealed interface, e.g., `LoginEvent`, `RegistrationAction`). The Composables collect these actions and pass them to the ViewModel for processing, giving the ViewModel a "presenter"-like role in handling input.
      * **Sealed Interface for One-Time View Events (`Effect` / `UiEvent`):** The ViewModel communicates one-time events (e.g., navigation commands, showing a SnackBar, displaying a Toast) back to the View via a separate mechanism (e.g., a `SharedFlow` or `Channel`). These events are typically consumed once by the View and do not represent persistent UI state.
      * **Data Class for View State (`ViewState` / `ScreenState`):** The ViewModel exposes the current state of the UI as a simple data class (e.g., `LoginState`, `AgendaState`). This data class contains all the necessary properties (loading indicators, error messages, data to display) that the Composable needs to render itself.

2.  **Domain Layer:**

      * This layer is platform-independent and focuses on the core business rules and entities. It contains:
          * **Repository Interfaces:** Define contracts for data operations (e.g., `AgendaRepository`, `AuthRepository`) that the Data Layer must implement. These interfaces are directly consumed by the Presentation Layer's ViewModels.
          * **Domain Models:** Pure Kotlin data classes representing the core entities of the application (e.g., `AgendaItem`, `Attendee`, `AlarmItem`).

3.  **Data Layer:**

      * Responsible for providing data to the Domain Layer. It contains:
          * **Repository Implementations:** Implement the interfaces defined in the Domain Layer. They decide whether to fetch data from local or remote sources. (e.g., `AgendaRepositoryImpl`).
          * **Data Sources:** Handle direct interaction with local (Room database) and remote (API via Retrofit/OkHttp) data (e.g., `LocalAgendaDataSource`, `RemoteAgendaDataSource`).
          * **Mappers:** Convert data between network/database entities and domain models.

<!-- end list -->

```
+-------------------------------------------------------------+
| Feature Module (e.g., `auth`, `agenda`)                     |
|                                                             |
|   +---------------------+   (UserAction)  +----------------+
|   |   Presentation Layer  |<------------->|  ViewModel     |
|   | - Composables (View) |                 | - StateFlow<ViewState> |
|   | - Observes ViewState|                 | - Handles UserAction  |
|   | - Dispatches UserAction|                 | - Emits One-Time Events|
|   +---------------------+   (One-Time Event)+----------------+
|              |                                     |
|              | (Domain Models)                     | (Calls Repository Interface methods)
|              V                                     V
|   +---------------------+   (Repository Interfaces)
|   |    Domain Layer     |<------------------------+
|   | - Domain Models      |
|   | - Repository Interfaces|
|   +---------------------+
|              |
|              | (Repository Implementations)
|              V
|   +---------------------+
|   |      Data Layer     |
|   | - Repository Impls  |
|   | - Remote Data Sources |
|   | - Local Data Sources  |
|   | - Mappers           |
|   +---------------------+
|                                                             |
+-------------------------------------------------------------+
               ^                                   ^
               | (Retrofit/OkHttp)                 | (Room DB)
               |                                   |
    +-----------------------+              +-----------------------+
    |   Backend API Service   |              |   Local Persistence   |
    | (Remote Data Source Impl)|              | (Local Data Source Impl) |
    +-----------------------+              +-----------------------+
```

## Offline-First Design

A cornerstone of Tasky's architecture is its robust offline-first strategy, ensuring a smooth user experience regardless of network connectivity.

  * **Local Data Source (`Room` Database):** All agenda items (Events, Tasks, Reminders) are persistently stored in a local Room database. This allows the app to function fully, including CRUD operations, when offline.
  * **Remote Data Source (API):** Interacts with the backend API for synchronization.
  * **`OfflineFirstAgendaRepository`:** This central component orchestrates data flow.
      * It always serves data from the local database first, ensuring immediate responsiveness.
      * It manages sending local changes to the remote and periodically fetching updates from the remote.
  * **Background Synchronization with `WorkManager`:**
      * **Pending Sync Queue:** Local changes (create, update, delete operations) are first saved to a `PendingItemSyncEntity` in the local database.
      * **Dedicated Workers:** `WorkManager` is heavily utilized to handle synchronization:
          * `CreateAgendaItemWorker`, `UpdateAgendaItemWorker`, `DeleteAgendaItemWorker`: Dedicated workers for each type of local modification, configured with `setBackoffCriteria` for robust retries. These workers process items from the pending sync queue when connectivity is available.
          * `PeriodicFetchAgendaWorker`: Periodically (e.g., every 30 minutes) fetches the latest agenda items from the backend to ensure local data is up-to-date, including items added by other devices or users adding the current user as an attendee.
      * **Conflict Resolution:** The system is designed to prioritize the most recent state where applicable, relying on the backend for the "source of truth" upon successful synchronization.
  * **Login/Logout Handling:** Upon successful login, the app fetches all user-specific agenda items from the backend. Upon logout, local user data is cleared, and associated reminders are cancelled.

## Key Technologies & Libraries

  * **Language:** [Kotlin](https://kotlinlang.org/)
  * **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
  * **Dependency Injection:** [Hilt (Dagger)](https://developer.android.com/training/dependency-injection/hilt-android)
  * **Background Tasks & Synchronization:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
  * **Local Database:** [Room Persistence Library](https://developer.android.com/topic/libraries/architecture/room)
  * **Asynchronous Programming:** [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines/basics.html) and [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html)
  * **Networking:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) (common choices for API interaction)
  * **Logging:** [Timber](https://github.com/JakeWharton/timber)
  * **Navigation:** [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
  * **Date & Time API:** [Java Time API (java.time)](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html) (via `ZonedDateTime`, `Instant`)
  * **Image Loading:** (Assuming Coil or Glide for image handling, if photos are displayed) - Add if applicable.
  * **Splash Screen:** [Android 12+ Splash Screen API](https://developer.android.com/develop/ui/views/launch/splash-screen)

## API Interaction

Tasky interacts with a custom backend API ([Tasky API Documentation](https://www.google.com/search?q=Tasky-API.pdf)) to manage agenda items and user authentication.

  * **Base URL:** `https://tasky.pl-coding.com/`
  * **Authentication:**
      * All endpoints require an `x-api-key` header.
      * Uses **Refresh Token Authentication** with JWTs (JSON Web Tokens) for managing user sessions. The app handles the entire authentication flow:
          * User registration creates a long-lived refresh token.
          * Login provides an `accessToken` (short-lived) and `refreshToken` (long-lived).
          * The `SessionStorage` is responsible for securely storing and providing session tokens.
          * Access token refresh logic is handled to ensure continuous authenticated API calls.
  * **Endpoints:**
      * **Auth:** `/register`, `/login`, `/refreshToken`
      * **Agenda:** `/agenda` (for fetching all items for a given day)
      * **Events:** `/event` (Create, Update, Delete, Get by ID), `/event?eventId={eventId}` (Delete)
          * Supports `hostId` for ownership and `attendees` management.
      * **Tasks:** `/task` (Create, Update, Delete, Get by ID), `/task?taskId={taskId}` (Delete)
          * *Note: API does not return `hostId` for Tasks, ownership is inferred by user session.*
      * **Reminders:** `/reminder` (Create, Update, Delete, Get by ID), `/reminder?reminderId={reminderId}` (Delete)
          * *Note: API does not return `hostId` for Reminders, ownership is inferred by user session.*
      * **Photos:** `/photo` (Upload, Delete) - part of Event management.

## Requirements

The application adheres to the following key requirements:

  * **Responsiveness:** Supports both mobile devices and tablets, including landscape mode.
  * **Theming:** Fully supports both light and dark themes.
  * **Splash Screen:** Displays a splash screen during app launch to check for active user sessions.
  * **Notification Reliability:** Alarms are set using `AlarmManager.setExactAndAllowWhileIdle` to ensure timely delivery, even in Doze mode.
  * **Cross-Device Sync:** Changes made on one device are synced and reflected on others where the user is logged in.
  * **Data Integrity:** Proper sync order is maintained to ensure the backend reflects the most up-to-date state.
  * **Background Check:** The app checks for new agenda items from the backend at least every 30 minutes.

## Setup & Installation

To get a copy of this project up and running on your local machine for development and testing purposes, follow these steps.

### Prerequisites

  * Android Studio Arctic Fox or newer
  * Kotlin plugin for Android Studio
  * An Android device or emulator running API level 21+ (preferably 33+ for full notification testing)

### Building the Project

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/Tasky.git
    cd Tasky
    ```
2.  **Open in Android Studio:**
    Open the cloned project in Android Studio.
3.  **Sync Gradle:**
    Allow Gradle to sync and download all necessary dependencies.
4.  **API Key:**
      * You will need to obtain an `x-api-key` from the Tasky API provider.
      * Create a `local.properties` file in your project's root directory (if it doesn't exist).
      * Add your API key to this file:
        ```properties
        TASKY_API_KEY="YOUR_API_KEY_HERE"
        ```
      * This key will be automatically picked up by your `build.gradle` (module-level) through `BuildConfig` or similar.
5.  **Run on Device/Emulator:**
    Connect an Android device or start an emulator and click the "Run" button in Android Studio.

## Future Enhancements

  * More sophisticated conflict resolution strategies for offline changes.
  * UI/UX improvements based on user feedback.
  * Deep linking for direct access to specific agenda items from notifications.
  * Integration with calendar applications.
  * Widget for quick agenda overview.

## Design & Mockups

The entire user interface and experience for Tasky were designed using Figma. You can view the full design mockups here:

👉 [Tasky Figma Design](https://www.figma.com/design/nblGPLxYBWvuZwyLfWwpzQ/Tasky?node-id=4-8354&t=S5QuvwzKRKGvclir-0)

## Contributing

Feel free to open issues or submit pull requests. Any contributions are welcome\!

**Please remember to:**

1.  **Replace `https://via.placeholder.com/...` URLs** in the "Screenshots" section with actual URLs to your app's screenshots.
2.  **Replace `https://github.com/your-username/Tasky.git`** with your actual GitHub repository URL.
3.  **Add a `LICENSE` file** to your repository if you choose the MIT License (or specify another).
4.  **Verify the `TASKY_API_KEY` handling** in your `build.gradle` to ensure it correctly picks up the key from `local.properties`.

This detailed README should provide a fantastic overview of your app for potential employers\! Good luck with your job search\!
