# CampusConnect Data Layer Documentation

## 1. Executive Summary
The Data Layer of CampusConnect has been re-architected to follow the **Offline-First** principle using the **Repository Pattern**. It serves as the bridge between the UI, the Local Database (Room), and the Remote API (Retrofit).

**Key Value Proposition:**
- **Zero Latency UI:** The UI always displays data from the local database immediately.
- **Offline Access:** Users can browse Events, Clubs, Posts, and their Profile without an internet connection.
- **Data Consistency:** The app automatically synchronizes local data with the server using transactional updates.
- **Secure Authentication:** Token management is handled transparently, keeping the UI code clean.

---

## 2. Architecture: Single Source of Truth (SSOT)
We strictly adhere to the **Single Source of Truth** principle. 

*   **The Database (Room)** is the *only* source of data for the UI.
*   **The API (Retrofit)** is used solely to *refresh* the Database.
*   **The UI** observes the Database via `Flow`.

**Data Flow:**
1.  **UI** subscribes to `repository.getDataLocal()`.
2.  **Repository** returns a `Flow` from the DAO.
3.  **UI** triggers a refresh (e.g., `viewModel.refreshData()`).
4.  **Repository** calls API -> gets data -> **clears old data** -> **inserts new data** (in a Transaction).
5.  **Room** emits the new data to the `Flow`.
6.  **UI** updates automatically.

---

## 3. Key Features & Capabilities

### A. Robust Offline Support
*   **Entities:** `User`, `Club`, `Event`, `Post` are all cached locally.
*   **Smart Caching:** 
    *   Events and Sessions are stored in the same table but managed separately to ensure fetching one doesn't wipe the other.
    *   Data is persisted across app restarts.

### B. Reactive Data Streams
*   All local data access methods return `Flow<T>`.
*   This means the UI is **reactive**. If a background sync updates the database, or if the user modifies data (e.g., "Likes" a post), the UI updates instantly without needing a manual refresh callback.

### C. Secure Authentication
*   **TokenManager:** Encrypted storage of JWT tokens using `DataStore`.
*   **AuthInterceptor:** Automatically injects the `Authorization: Bearer <token>` header into *every* API request.
*   **Login Flow:** Login automatically fetches the User Profile and caches it in one smooth operation.

### D. Data Consistency
*   **Transactional Refreshes:** We use `@Transaction` in DAOs to perform "Clear & Insert" operations atomically. This ensures the user never sees a mix of old and new data, and deleted items on the server are correctly removed locally.

---

## 4. Repository Breakdown

### 1. `UserRepository`
*   **Features:** Login, Profile Management.
*   **API:** `login()`, `getUserProfile()`.
*   **Local:** `getUser()` returns `Flow<User?>`.
*   **Logic:** On login, the token is saved, profile is fetched, and immediately cached.

### 2. `EventRepository`
*   **Features:** Events, Sessions, Registration.
*   **API:** CRUD for Events/Sessions, Register/Unregister.
*   **Local:** `getEventsLocal()` returns `Flow<List<Event>>`.
*   **Special Handling:** Distinguishes between `EventType.EVENT` and `EventType.SESSION` to manage the cache intelligently.

### 3. `ClubRepository`
*   **Features:** Club Discovery, Membership.
*   **API:** List Clubs, Join/Leave Club.
*   **Local:** `getClubsLocal()` returns `Flow<List<Club>>`.
*   **Logic:** Joining/Leaving a club automatically updates the local cache state for that club.

### 4. `PostRepository`
*   **Features:** Social Feed, Interactions.
*   **API:** Feed, Create Post, Like/Unlike.
*   **Local:** `getPostsLocal()` returns `Flow<List<Post>>`.
*   **Logic:** "Like" actions update the server and immediately refresh the local record to reflect the new like count/status.

---

## 5. Usage for UI Developers
To use this data layer in your ViewModels:

1.  **Inject the Repository:**
    ```kotlin
    @HiltViewModel
    class HomeViewModel @Inject constructor(
        private val eventRepository: EventRepository
    ) : ViewModel() { ... }
    ```

2.  **Observe Data (The "Source of Truth"):**
    ```kotlin
    val events = eventRepository.getEventsLocal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    ```

3.  **Trigger Refresh (The "Side Effect"):**
    ```kotlin
    fun refresh() {
        viewModelScope.launch {
            eventRepository.getEvents() // This updates the DB, which updates the Flow above
                .onFailure { /* Show error snackbar */ }
        }
    }
    ```
