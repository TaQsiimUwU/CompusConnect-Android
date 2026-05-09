package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

enum class EventStatus {
    @SerializedName("pending")
    PENDING,
    @SerializedName(value = "scheduled", alternate = ["approved"])
    APPROVED,
    @SerializedName(value = "cancelled", alternate = ["rejected"])
    REJECTED
}

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
    val noOfMaxRegistrations: Int,
    @SerializedName("is_registered")
    val isRegistered: Boolean? = null
)


val Event.isEvent: Boolean get() = type == EventType.EVENT
val Event.isSession: Boolean get() = type == EventType.SESSION

data class RegisteredStudentResponse(
    @SerializedName("student_id")
    val studentId: Int,
    val name: String,
    val email: String,
    val major: String
) {
    val firstName: String
        get() = name.substringBefore(" ")
    val lastName: String
        get() = name.substringAfter(" ", "")
}

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

data class CreateEventRequest(
    val type: String,
    val title: String,
    val description: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("room_id")
    val roomId: Int,
    @SerializedName("max_registrations")
    val maxRegistrations: Int
)

data class RegisterEventRequest(
    @SerializedName("student_id")
    val studentId: Int
)

data class CheckInRequest(
    @SerializedName("student_id")
    val studentId: Int
)

data class PendingEvent(
    @SerializedName("event_id")
    val eventId: Int,
    val type: EventType,
    val title: String,
    val description: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    val status: EventStatus,
    @SerializedName("max_regestrations")
    val maxRegistrations: Int,
    @SerializedName("club_name")
    val clubName: String = ""
)

data class StudentSearchResult(
    @SerializedName("student_id")
    val studentId: Int,
    val name: String,
    val email: String,
    val faculty: String,
    val major: String,
    val level: Int
)

data class SearchStudentsRequest(
    val query: String
)

enum class EventType  {
    @SerializedName("session")
    SESSION,
    @SerializedName("event")
    EVENT
}

data class CreateEventResponse(
    @SerializedName("event_id")
    val eventId: Int
)
