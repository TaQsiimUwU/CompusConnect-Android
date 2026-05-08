# API Integration Documentation
**Date:** December 24, 2025

## Overview
This document details the complete integration of the backend APIs into the CompusConnect Android application. All endpoints from the API documentation have been implemented with proper data models, repositories, and dependency injection.

---

## 🎯 Summary of Changes

### New Data Models Created
1. **Room.kt** - Room management, reservations, and resources
2. **Facility.kt** - Facility types, status, and creation
3. **Admin.kt** - Admin operations, user management, statistics, and approvals
4. **Report.kt** - Issue reporting for events, rooms, facilities, and clubs

### New Repositories Created
1. **RoomRepository** - Room operations and reservations
2. **FacilityRepository** - Facility management
3. **AdminRepository** - Admin-only operations
4. **ReportRepository** - Unified reporting system

### Updated Repositories
1. **EventRepository** - Added attendance, deletion, and requested events
2. **PostRepository** - Added comments, updates, and event-specific posts
3. **ClubRepository** - Added details, updates, follow/unfollow
4. **UserRepository** - Added current user and logout functionality

---

## 📁 File Structure

```
app/src/main/java/com/taqsiim/compusconnect/
├── data/
│   ├── api/
│   │   └── ApiService.kt (UPDATED - all endpoints)
│   ├── model/
│   │   ├── Admin.kt (NEW)
│   │   ├── Facility.kt (NEW)
│   │   ├── Report.kt (NEW)
│   │   ├── Room.kt (NEW)
│   │   ├── Event.kt (UPDATED)
│   │   ├── Post.kt (UPDATED)
│   │   ├── Club.kt (EXISTING)
│   │   ├── User.kt (EXISTING)
│   │   ├── Comment.kt (EXISTING)
│   │   └── Reservation.kt (UPDATED)
│   └── repository/
│       ├── AdminRepository.kt (NEW)
│       ├── FacilityRepository.kt (NEW)
│       ├── ReportRepository.kt (NEW)
│       ├── RoomRepository.kt (NEW)
│       ├── EventRepository.kt (UPDATED)
│       ├── PostRepository.kt (UPDATED)
│       ├── ClubRepository.kt (UPDATED)
│       └── UserRepository.kt (UPDATED)
└── di/
    └── AppModule.kt (UPDATED - new repository providers)
```

---

## 🔌 API Endpoints Implementation

### Authentication (✅ Complete)
| Method | Endpoint | Repository Method | Model |
|--------|----------|------------------|-------|
| POST | `/api/auth/login` | `UserRepository.login()` | `LoginRequest`, `LoginResponse` |
| GET | `/api/users/me` | `UserRepository.getCurrentUser()` | `User` |

### Admin Operations (✅ Complete)
| Method | Endpoint | Repository Method | Model |
|--------|----------|------------------|-------|
| POST | `/api/admin/rooms` | `AdminRepository.createRoom()` | `CreateRoomRequest` |
| POST | `/api/admin/facilities` | `AdminRepository.createFacility()` | `CreateFacilityRequest` |
| POST | `/api/admin/users` | `AdminRepository.createUser()` | `CreateUserRequest` |
| GET | `/api/admin/report` | `AdminRepository.getAllReports()` | `Report[]` |
| GET | `/api/admin/stats` | `AdminRepository.getSystemStats()` | `SystemStats` |
| GET | `/api/admin/attendance` | `AdminRepository.getAttendanceOverview()` | `AttendanceOverview[]` |
| GET | `/api/admin/approvals/events` | `AdminRepository.getPendingEvents()` | `PendingEvent[]` |
| PATCH | `/api/admin/approvals/events/:id` | `AdminRepository.approveOrRejectEvent()` | `ApproveEventRequest` |
| GET | `/api/admin/logs` | `AdminRepository.getSystemLogs()` | `SystemLog[]` |

### User Management (✅ Complete)
| Method | Endpoint | Repository Method | Model |
|--------|----------|------------------|-------|
| GET | `/api/users/me` | `UserRepository.getCurrentUser()` | `User` |
| GET | `/api/users/students` | `AdminRepository.getAllStudents()` | `StudentSearchResult[]` |
| PATCH | `/api/users/:id/ban` | `AdminRepository.banUser()` | `BanUserRequest` |
| POST | `/api/users` | `AdminRepository.searchStudents()` | `SearchStudentsRequest` |

### Events (✅ Complete)
| Method | Endpoint | Repository Method | Model |
|--------|----------|------------------|-------|
| POST | `/api/events` | `EventRepository.createEvent()` | `CreateEventRequest` |
| POST | `/api/events/:id/register` | `EventRepository.registerForEvent()` | - |
| DELETE | `/api/events/:id/register` | `EventRepository.unregisterFromEvent()` | - |
| POST | `/api/events/:id/attendance` | `EventRepository.checkInStudent()` | `CheckInRequest` |
| DELETE | `/api/events/:id` | `EventRepository.deleteEvent()` | - |
| GET | `/api/events` | `EventRepository.getEvents()` | `Event[]` |
| GET | `/api/events/requested` | `EventRepository.getRequestedEvents()` | `PendingEvent[]` |
| GET | `/api/events/:id` | `EventRepository.getEventById()` | `Event` |
| GET | `/api/events/:id/registered_students` | `EventRepository.getRegisteredStudents()` | `RegisteredStudentResponse[]` |
| GET | `/api/events/:id/attendance_list` | `EventRepository.getAttendanceList()` | `RegisteredStudentResponse[]` |
| GET | `/api/events/:id/posts` | `PostRepository.getPostsForEvent()` | `Post[]` |
| POST | `/api/events/report` | `ReportRepository.reportEvent()` | `ReportEventRequest` |

### Rooms (✅ Complete)
| Method | Endpoint | Repository Method | Model |
|--------|----------|------------------|-------|
| POST | `/api/rooms/reserve` | `RoomRepository.reserveRoom()` | `ReserveRoomRequest` |
| PATCH | `/api/rooms/:id/cancel` | `RoomRepository.cancelReservation()` | `CancelReservationRequest` |
| GET | `/api/rooms` | `RoomRepository.getRooms()` | `Room[]` |
| POST | `/api/rooms/report` | `RoomRepository.reportRoom()` | `ReportRoomRequest` |
| POST | `/api/rooms` | `RoomRepository.createRoom()` | `CreateRoomRequest` |
| POST | `/api/rooms/resources` | `RoomRepository.createResource()` | `CreateResourceRequest` |
| GET | `/api/rooms/resources` | `RoomRepository.getResources()` | `Resource[]` |

### Clubs (✅ Complete)
| Method | Endpoint | Repository Method | Model |
|--------|----------|------------------|-------|
| POST | `/api/clubs` | Admin only (not in ClubRepository) | `CreateClubRequest` |
| GET | `/api/clubs` | `ClubRepository.getClubs()` | `Club[]` |
| GET | `/api/clubs/:id` | `ClubRepository.getClubDetails()` | `Club` |
| PUT | `/api/clubs/:id` | `ClubRepository.updateClub()` | `UpdateClubRequest` |
| POST | `/api/clubs/:id/follow` | `ClubRepository.followClub()` | - |
| DELETE | `/api/clubs/:id/follow` | `ClubRepository.unfollowClub()` | - |
| POST | `/api/clubs/report` | `ReportRepository.reportClub()` | `ReportClubRequest` |

### Posts (✅ Complete)
| Method | Endpoint | Repository Method | Model |
|--------|----------|------------------|-------|
| POST | `/api/posts` | `PostRepository.createPost()` | `CreatePostRequest` |
| PUT | `/api/posts/:id` | `PostRepository.updatePost()` | `UpdatePostRequest` |
| GET | `/api/posts` | `PostRepository.getPosts()` | `Post[]` |
| POST | `/api/posts/:id/like` | `PostRepository.likePost()` | - |
| DELETE | `/api/posts/:id/like` | `PostRepository.unlikePost()` | - |
| POST | `/api/posts/:id/comments` | `PostRepository.addComment()` | `CommentRequest` |
| GET | `/api/posts/:id/comments` | `PostRepository.getComments()` | `Comment[]` |

### Facilities (✅ Complete)
| Method | Endpoint | Repository Method | Model |
|--------|----------|------------------|-------|
| POST | `/api/facilities/report` | `FacilityRepository.reportFacility()` | `ReportFacilityRequest` |
| GET | `/api/facilities` | `FacilityRepository.getFacilities()` | `Facility[]` |
| POST | `/api/facilities` | `FacilityRepository.createFacility()` | `CreateFacilityRequest` |

---

## 📦 Data Models

### New Enums
- `RoomType`: CLASSROOM, LAB, STUDY_ROOM, MEETING_ROOM, AUDITORIUM
- `FacilityType`: GYM, COURT, POOL, LIBRARY, CAFETERIA, LAB, OTHER
- `FacilityStatus`: AVAILABLE, OCCUPIED, MAINTENANCE, CLOSED
- `ReportType`: EVENT, ROOM, FACILITY, CLUB
- `ReportStatus`: PENDING, IN_PROGRESS, RESOLVED, REJECTED

### Request Models
```kotlin
// Room Management
CreateRoomRequest
ReserveRoomRequest
CancelReservationRequest
CreateResourceRequest

// Facility Management
CreateFacilityRequest

// Admin Operations
CreateUserRequest
BanUserRequest
SearchStudentsRequest
CreateClubRequest
UpdateClubRequest
ApproveEventRequest

// Reporting
ReportEventRequest
ReportRoomRequest
ReportFacilityRequest
ReportClubRequest

// Posts & Events
UpdatePostRequest
CheckInRequest
```

### Response Models
```kotlin
Room
Resource
Facility
SystemStats
AttendanceOverview
PendingEvent
SystemLog
StudentSearchResult
Report
```

---

## 🔧 Architecture

### Repository Pattern
All repositories follow the same pattern:
```kotlin
suspend fun operationName(...): Result<T> {
    return try {
        val result = api.endpoint(...)
        // Optional: Update local cache
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Dependency Injection
All repositories are provided as Singletons via Hilt:
```kotlin
@Provides
@Singleton
fun provideRepository(api: ApiService): Repository {
    return Repository(api)
}
```

### Error Handling
- All repository methods return `Result<T>` for consistent error handling
- Network errors are caught and wrapped in `Result.failure(e)`
- UI layer can handle success/failure cases uniformly

---

## 🚀 Usage Examples

### Admin: Create a Room
```kotlin
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    
    fun createRoom(request: CreateRoomRequest) {
        viewModelScope.launch {
            adminRepository.createRoom(request).fold(
                onSuccess = { room -> /* Handle success */ },
                onFailure = { error -> /* Handle error */ }
            )
        }
    }
}
```

### Student: Reserve a Room
```kotlin
@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {
    
    fun reserveRoom(request: ReserveRoomRequest) {
        viewModelScope.launch {
            roomRepository.reserveRoom(request).fold(
                onSuccess = { reservation -> /* Handle success */ },
                onFailure = { error -> /* Handle error */ }
            )
        }
    }
}
```

### Club Manager: Check in Student
```kotlin
@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    
    fun checkInStudent(eventId: Int, studentId: Int) {
        viewModelScope.launch {
            eventRepository.checkInStudent(eventId, studentId).fold(
                onSuccess = { /* Student checked in */ },
                onFailure = { error -> /* Handle error */ }
            )
        }
    }
}
```

### Report an Issue
```kotlin
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {
    
    fun reportFacility(facilityId: Int, reason: String, details: String) {
        viewModelScope.launch {
            reportRepository.reportFacility(facilityId, reason, details).fold(
                onSuccess = { report -> /* Report submitted */ },
                onFailure = { error -> /* Handle error */ }
            )
        }
    }
}
```

---

## ⚠️ Important Notes

### Base URL Configuration
Update the base URL in [Constants.kt](app/src/main/java/com/taqsiim/compusconnect/util/Constants.kt):
```kotlin
const val BASE_URL = "https://your-actual-api-url.com/api/"
```

### Authentication
- All authenticated endpoints automatically include the Bearer token via `AuthInterceptor`
- Token is stored securely using `TokenManager` with DataStore
- Login automatically saves the token and fetches user profile

### Offline-First Strategy
- Repositories for Events, Clubs, Posts, and Users cache data locally
- Room and Facility repositories don't cache (stateful data)
- UI always reads from local cache first, then refreshes from API

### API Response Format
All endpoints should return data in the expected format. If the backend returns different field names, update the `@SerializedName` annotations in the data models.

---

## ✅ Testing Checklist

- [ ] Update `BASE_URL` in Constants.kt
- [ ] Test login flow
- [ ] Test admin operations (create room, facility, user)
- [ ] Test student operations (reserve room, register for event)
- [ ] Test club manager operations (create event, check-in students)
- [ ] Test reporting system
- [ ] Test comment functionality
- [ ] Verify offline caching works correctly
- [ ] Test error handling for network failures
- [ ] Verify token refresh works

---

## 📝 Next Steps

1. **Configure Backend URL**: Update `Constants.BASE_URL` with production API
2. **Create ViewModels**: Implement ViewModels for each feature using the repositories
3. **Update UI Screens**: Connect existing UI screens to the new repository methods
4. **Testing**: Write unit tests for repositories and integration tests for API calls
5. **Error Handling**: Implement comprehensive error messages for users
6. **Loading States**: Add loading indicators for all async operations

---

## 📚 Resources

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Repository Pattern](https://developer.android.com/topic/architecture/data-layer)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

**Generated on:** December 24, 2025  
**API Version:** 1.0  
**Android App:** CompusConnect v1.0
