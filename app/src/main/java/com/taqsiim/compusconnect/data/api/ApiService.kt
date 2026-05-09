package com.taqsiim.compusconnect.data.api

import com.taqsiim.compusconnect.data.model.*
import retrofit2.http.*
import com.taqsiim.compusconnect.util.Constants.Endpoints

/**
 * API Service for all network calls
 * TODO: Replace BASE_URL with your actual API endpoint
 * TODO: Implement all API endpoints
 */
interface ApiService {
    
    @POST(Endpoints.LOGIN)
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET(Endpoints.EVENTS)
    suspend fun getEvents(): List<Event>?
    
    @GET(Endpoints.EVENT_BY_ID)
    suspend fun getEventById(@Path("event_id") id: Int): Event

    @POST(Endpoints.EVENTS)
    suspend fun createEvent(@Body request: CreateEventRequest): CreateEventResponse

    @POST(Endpoints.EVENT_REGISTER)
    suspend fun registerForEvent(@Path("event_id") id: Int)

    @DELETE(Endpoints.EVENT_REGISTER)
    suspend fun unregisterFromEvent(@Path("event_id") id: Int): MessageResponse

    // Registered students and attendance list per API docs
    @GET(Endpoints.EVENT_REGISTERED_STUDENTS)
    suspend fun getRegisteredStudents(@Path("event_id") eventId: Int): List<RegisteredStudentResponse>

    @GET(Endpoints.EVENT_ATTENDANCE)
    suspend fun getAttendanceList(@Path("event_id") eventId: Int): List<RegisteredStudentResponse>

    // --- Sessions ---
    // Sessions endpoint not available on backend yet
    // @GET("sessions")
    // suspend fun getSessions(): List<Event>
    
    @POST(Endpoints.SESSIONS)
    suspend fun createSession(@Body request: CreateEventRequest): Event
    
    @POST(Endpoints.SESSION_REGISTER)
    suspend fun registerForSession(@Path("id") id: Int): Event
    
    @GET(Endpoints.CLUBS)
    suspend fun getClubs(): List<Club>

    @GET(Endpoints.CLUB_BY_ID)
    suspend fun getClubById(@Path("id") id: Int): Club
    
    @POST(Endpoints.CLUB_FOLLOW)
    suspend fun followClub(@Path("id") id: Int): MessageResponse

    @DELETE(Endpoints.CLUB_FOLLOW)
    suspend fun unfollowClub(@Path("id") id: Int): MessageResponse

    @GET(Endpoints.POSTS)
    suspend fun getPosts(): PostsResponse
    
    @POST(Endpoints.POSTS)
    suspend fun createPost(@Body request: CreatePostRequest): MessageResponse

    @POST(Endpoints.POST_LIKE)
    suspend fun likePost(@Path("id") id: Int): MessageResponse

    @DELETE(Endpoints.POST_LIKE)
    suspend fun unlikePost(@Path("id") id: Int): MessageResponse

    @GET(Endpoints.EVENT_POSTS)
    suspend fun getPostsForEvent(@Path("id") eventId: Int): List<Post>
    
    @PUT(Endpoints.POST_BY_ID)
    suspend fun updatePost(@Path("id") postId: Int, @Body content: UpdatePostRequest): MessageResponse

    @POST(Endpoints.POST_COMMENTS)
    suspend fun addComment(@Path("id") postId: Int, @Body request: CommentRequest): MessageResponse

    @GET(Endpoints.POST_COMMENTS)
    suspend fun getComments(@Path("id") postId: Int): CommentsResponse

    @PUT(Endpoints.CLUB_BY_ID)
    suspend fun updateClub(@Path("id") clubId: Int, @Body request: UpdateClubRequest): MessageResponse

    @DELETE(Endpoints.EVENT_BY_ID)
    suspend fun deleteEvent(@Path("event_id") eventId: Int)

    @GET(Endpoints.EVENTS_REQUESTED)
    suspend fun getRequestedEvents(): List<PendingEvent>
    
    @POST(Endpoints.EVENT_CHECK_IN)
    suspend fun checkInStudent(@Path("event_id") eventId: Int, @Body request: CheckInRequest)
    
    @POST(Endpoints.ROOMS_RESERVE)
    suspend fun reserveRoom(@Body request: ReserveRoomRequest): RoomReservationResponse

    @PATCH(Endpoints.ROOMS_CANCEL)
    suspend fun cancelRoomReservation(@Path("id") reservationId: Int, @Body request: CancelReservationRequest): MessageResponse

    @GET(Endpoints.ROOMS)
    suspend fun getRooms(): List<Room>
    
    @POST(Endpoints.ROOMS)
    suspend fun createRoom(@Body request: CreateRoomRequest): Room
    
    @POST(Endpoints.ROOMS_RESOURCES)
    suspend fun createResource(@Body request: CreateResourceRequest): Resource
    
    @GET(Endpoints.ROOMS_RESOURCES)
    suspend fun getResources(): List<Resource>

    @POST(Endpoints.FACILITIES)
    suspend fun createFacility(@Body request: CreateFacilityRequest): Facility
    
    @GET(Endpoints.FACILITIES)
    suspend fun getFacilities(): List<Facility>

    @GET(Endpoints.USER_PROFILE)
    suspend fun getUserProfile(): User
    
    @GET(Endpoints.USER_RESERVATIONS)
    suspend fun getMyReservations(): List<Reservation>
    
    @POST(Endpoints.USERS)
    suspend fun searchStudents(@Body request: SearchStudentsRequest): List<StudentSearchResult>

    @POST(Endpoints.EVENT_REPORT)
    suspend fun reportEvent(@Body request: ReportEventRequest): MessageResponse

    @POST(Endpoints.ROOM_REPORT)
    suspend fun reportRoom(@Body request: ReportRoomRequest): MessageResponse

    @POST(Endpoints.FACILITY_REPORT)
    suspend fun reportFacility(@Body request: ReportFacilityRequest): MessageResponse

    @POST(Endpoints.CLUB_REPORT)
    suspend fun reportClub(@Body request: ReportClubRequest): MessageResponse

    @POST(Endpoints.FACILITIES_RESERVE)
    suspend fun reserveFacility(@Path("id") facilityId: Int, @Body request: ReserveFacilityRequest): FacilityReservationResponse
}
