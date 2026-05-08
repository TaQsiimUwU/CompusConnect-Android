package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("room_id")
    val roomId: Int,
    @SerializedName("room_number")
    val roomNumber: String,
    @SerializedName("building_name")
    val buildingName: String,
    val capacity: Int,
    val type: RoomType,
    @SerializedName("is_available")
    val isAvailable: Boolean,
    val resources: List<Resource>
)

enum class RoomType {
    @SerializedName("classroom")
    CLASSROOM,
    @SerializedName("lab")
    LAB,
    @SerializedName("study_room")
    STUDY_ROOM,
    @SerializedName("meeting_room")
    MEETING_ROOM,
    @SerializedName("auditorium")
    AUDITORIUM
}

data class Resource(
    @SerializedName("resource_id")
    val resourceId: Int,
    val name: String
)

// Request models
data class CreateRoomRequest(
    @SerializedName("room_number")
    val roomNumber: String,
    @SerializedName("building_name")
    val buildingName: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    val capacity: Int,
    val type: String,
    @SerializedName("is_available")
    val isAvailable: Boolean,
    @SerializedName("resources_ids")
    val resourcesIds: List<Int>
)

data class ReserveRoomRequest(
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    val purpose: String,
    @SerializedName("std_ids")
    val stdIds: List<Int>
)

data class CancelReservationRequest(
    @SerializedName("start_time")
    val startTime: String
)

data class CreateResourceRequest(
    val name: String
)
