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
    val type: ReservationType? = null
)

enum class ReservationType {
    @SerializedName("event")
    EVENT,
    @SerializedName("session")
    SESSION,
    @SerializedName(value = "studyRoom", alternate = ["study_room", "room"])
    STUDY_ROOM,
    @SerializedName(value = "sport", alternate = ["sports", "facility"])
    SPORT
}