package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Room(
    val id: Int,
    val name: Int,
    @SerializedName("room_number")
    val roomNumber: Int,
    @SerializedName("building_name")
    val buildingName: String,
    val capacity: Int,
    val type: String,
    val status: RoomStatus,
    @SerializedName("start_time")
    val startTime: Int,
    @SerializedName("end_time")
    val endTime: Int,
    val resources: List<String>
)

enum class RoomStatus {
    @SerializedName("available")
    AVAILABLE,
    @SerializedName("maintenance")
    MAINTENANCE
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

data class RoomReservationResponse(
    @SerializedName("room_id")
    val roomId: Int,
    @SerializedName("room_number")
    val roomNumber: Int,
    @SerializedName("building_name")
    val buildingName: String
)
