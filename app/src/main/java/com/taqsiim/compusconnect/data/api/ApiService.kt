package com.taqsiim.compusconnect.data.api

import com.taqsiim.compusconnect.data.model.*
import retrofit2.http.*

/**
 * API Service for all network calls
 * TODO: Replace BASE_URL with your actual API endpoint
 * TODO: Implement all API endpoints
 */
interface ApiService {
    
    // --- Auth ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("users/profile")
    suspend fun getUserProfile(): User
    
    // --- Events ---
    @GET("events")
    suspend fun getEvents(): List<Event>
    
    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Int): Event
    
    @POST("events")
    suspend fun createEvent(@Body request: CreateEventRequest): Event
    
    @POST("events/{id}/register")
    suspend fun registerForEvent(@Path("id") id: Int): Event
    
    @DELETE("events/{id}/register")
    suspend fun unregisterFromEvent(@Path("id") id: Int): Event
    
    @GET("events/{id}/attendees")
    suspend fun getEventAttendees(@Path("id") id: Int): List<RegisteredStudentResponse>

    // --- Sessions ---
    @GET("sessions")
    suspend fun getSessions(): List<Event>
    
    @POST("sessions")
    suspend fun createSession(@Body request: CreateEventRequest): Event
    
    @POST("sessions/{id}/register")
    suspend fun registerForSession(@Path("id") id: Int): Event
    
    // --- Clubs ---
    @GET("clubs")
    suspend fun getClubs(): List<Club>
    
    @POST("clubs/{id}/join")
    suspend fun joinClub(@Path("id") id: Int): Club
    
    @DELETE("clubs/{id}/join")
    suspend fun leaveClub(@Path("id") id: Int): Club
    
    // --- Posts ---
    @GET("posts")
    suspend fun getPosts(): List<Post>
    
    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequest): Post
    
    @POST("posts/{id}/like")
    suspend fun likePost(@Path("id") id: Int): Post
    
    @DELETE("posts/{id}/like")
    suspend fun unlikePost(@Path("id") id: Int): Post
    
    // TODO: Implement Facilities endpoints
    
    // TODO: Implement Reservations endpoints
    
    // TODO: Implement Notifications endpoints
    
    // TODO: Implement Manager endpoints
    
    // TODO: Implement Issue Reporting endpoints
}
