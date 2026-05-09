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
    // Auth
    @POST(Endpoints.LOGIN)
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // User
    @GET(Endpoints.USER_PROFILE)
    suspend fun getUserProfile(): User

    @GET(Endpoints.USER_RESERVATIONS)
    suspend fun getMyReservations(): List<Reservation>

    // Clubs
    @GET(Endpoints.CLUBS)
    suspend fun getClubs(): List<Club>

    @GET(Endpoints.CLUB_BY_ID)
    suspend fun getClubById(@Path("id") id: Int): Club

    @POST(Endpoints.CLUB_FOLLOW)
    suspend fun followClub(@Path("id") id: Int): MessageResponse

    @DELETE(Endpoints.CLUB_FOLLOW)
    suspend fun unfollowClub(@Path("id") id: Int): MessageResponse

    @PUT(Endpoints.CLUB_BY_ID)
    suspend fun updateClub(@Path("id") clubId: Int, @Body request: UpdateClubRequest): MessageResponse

    @POST(Endpoints.CLUB_REPORT)
    suspend fun reportClub(@Body request: ReportClubRequest): MessageResponse

    // Events
    @GET(Endpoints.EVENTS)
    suspend fun getEvents(
        @Query("type") type: String? = null,
        @Query("club_id") clubId: Int? = null
    ): List<Event>?

    @GET(Endpoints.EVENT_BY_ID)
    suspend fun getEventById(@Path("event_id") id: Int): Event

    @POST(Endpoints.EVENTS)
    suspend fun createEvent(@Body request: CreateEventRequest): CreateEventResponse

    @POST(Endpoints.EVENT_REGISTER)
    suspend fun registerForEvent(@Path("event_id") id: Int)

    @DELETE(Endpoints.EVENT_REGISTER)
    suspend fun unregisterFromEvent(@Path("event_id") id: Int): MessageResponse

    @GET(Endpoints.EVENT_REGISTERED_STUDENTS)
    suspend fun getRegisteredStudents(@Path("event_id") eventId: Int): List<RegisteredStudentResponse>

    @GET(Endpoints.EVENT_ATTENDANCE)
    suspend fun getAttendanceList(@Path("event_id") eventId: Int): List<RegisteredStudentResponse>

    @GET(Endpoints.EVENT_POSTS)
    suspend fun getPostsForEvent(@Path("id") eventId: Int): List<Post>

    @DELETE(Endpoints.EVENT_BY_ID)
    suspend fun deleteEvent(@Path("event_id") eventId: Int)

    @GET(Endpoints.EVENTS_REQUESTED)
    suspend fun getRequestedEvents(): List<PendingEvent>

    @POST(Endpoints.EVENT_REPORT)
    suspend fun reportEvent(@Body request: ReportEventRequest): MessageResponse

    @POST(Endpoints.EVENTS)
    suspend fun createSession(@Body request: CreateEventRequest): CreateEventResponse

    // Posts
    @GET(Endpoints.POSTS)
    suspend fun getPosts(): PostsResponse

    @POST(Endpoints.POSTS)
    suspend fun createPost(@Body request: CreatePostRequest): MessageResponse

    @POST(Endpoints.POST_LIKE)
    suspend fun likePost(@Path("id") id: Int): MessageResponse

    @DELETE(Endpoints.POST_LIKE)
    suspend fun unlikePost(@Path("id") id: Int): MessageResponse

    @PUT(Endpoints.POST_BY_ID)
    suspend fun updatePost(@Path("id") postId: Int, @Body content: UpdatePostRequest): MessageResponse

    @POST(Endpoints.POST_COMMENTS)
    suspend fun addComment(@Path("id") postId: Int, @Body request: CommentRequest): MessageResponse

    @GET(Endpoints.POST_COMMENTS)
    suspend fun getComments(@Path("id") postId: Int): CommentsResponse

    // Rooms
    @GET(Endpoints.ROOMS)
    suspend fun getRooms(): List<Room>

    @POST(Endpoints.ROOMS_RESERVE)
    suspend fun reserveRoom(@Body request: ReserveRoomRequest): RoomReservationResponse

    @PATCH(Endpoints.ROOMS_CANCEL)
    suspend fun cancelRoomReservation(@Path("id") reservationId: Int, @Body request: CancelReservationRequest): MessageResponse

    @GET(Endpoints.ROOMS_RESOURCES)
    suspend fun getResources(): List<Resource>

    @POST(Endpoints.ROOM_REPORT)
    suspend fun reportRoom(@Body request: ReportRoomRequest): MessageResponse

    // Facilities
    @GET(Endpoints.FACILITIES)
    suspend fun getFacilities(): List<Facility>

    @POST(Endpoints.FACILITIES_RESERVE)
    suspend fun reserveFacility(@Path("id") facilityId: Int, @Body request: ReserveFacilityRequest): FacilityReservationResponse

    @POST(Endpoints.FACILITY_REPORT)
    suspend fun reportFacility(@Body request: ReportFacilityRequest): MessageResponse
}
