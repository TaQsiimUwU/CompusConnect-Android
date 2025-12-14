package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Reservation(
    @SerializedName("reservation_id")
    val reservationId: String,
    val title: String,
    @SerializedName("start_time")
    val startTime: String, // ISO format
    @SerializedName("end_time")
    val endTime: String,   // ISO format
    val type: ReservationType
)

enum class ReservationType {
    @SerializedName("event")
    EVENT,
    @SerializedName("session")
    SESSION,
    @SerializedName("studyRoom")
    STUDY_ROOM,
    @SerializedName("sport")
    SPORT
}



// TODO: Implement ReservationStatus enum
enum class ReservationStatus {
    // TODO: Add status types
}

// TODO: Implement FacilityBooking data class
class FacilityBooking

// TODO: Implement FacilityType enum
enum class FacilityType {
    // TODO: Add facility types
}

// TODO: Implement StudyRoomBooking data class
class StudyRoomBooking
