package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

// Used for BOTH events AND sessions
data class Event(
    @SerializedName("event_id")
    val eventId: Int,
    @SerializedName("club_name")
    val clubName: String,
    @SerializedName("club_logo_url")
    val clubLogoUrl: String,
    @SerializedName("club_cover_url")
    val clubCoverUrl: String,
    val type: EventType,
    val title: String,
    val description: String,
    @SerializedName("start_time")
    val startTime: String, // ISO format
    @SerializedName("end_time")
    val endTime: String,   // ISO format
    val location: String,
    @SerializedName("regestrations")
    val noOfRegistrations: Int,
    @SerializedName("max_regestrations")
    val noOfMaxRegistrations: Int
)


val Event.isEvent: Boolean get() = type == EventType.EVENT
val Event.isSession: Boolean get() = type == EventType.SESSION

data class RegisteredStudentResponse(
    @SerializedName("student_id")
    val studentId: String,
    val name: String,
    val email: String,
    val major: String
)

// this response is for the club manager
data class ClubEventResponse(
    @SerializedName("event_id")
    val eventId: Int,
    val type: String,
    val title: String,
    val description: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    val status: EventStatus,
    @SerializedName("max_regestrations")
    val maxRegistrations: Int
)

enum class EventStatus {
    @SerializedName("pending")
    PENDING,
    @SerializedName("approved")
    APPROVED,
    @SerializedName("rejected")
    REJECTED
}

data class CreateEventRequest(
    val type: String,
    val title: String,
    val description: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("max_regestrations")
    val maxRegistrations: Int
)

data class RegisterEventRequest(
    @SerializedName("student_id")
    val studentId: Int
)

// TODO: Implement EventStatus enum

enum class EventType  { 
    @SerializedName("session")
    SESSION,
    @SerializedName("event")
    EVENT
}