package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Facility(
    @SerializedName("facility_id")
    val facilityId: Int,
    val name: String,
    @SerializedName("location_description")
    val locationDescription: String,
    @SerializedName("min_capacity")
    val minCapacity: Int,
    @SerializedName("max_capacity")
    val maxCapacity: Int,
    val type: FacilityType,
    val status: FacilityStatus
)

enum class FacilityType {
    @SerializedName("gym")
    GYM,
    @SerializedName("court")
    COURT,
    @SerializedName("pool")
    POOL,
    @SerializedName("library")
    LIBRARY,
    @SerializedName("cafeteria")
    CAFETERIA,
    @SerializedName("lab")
    LAB,
    @SerializedName("other")
    OTHER
}

enum class FacilityStatus {
    @SerializedName("available")
    AVAILABLE,
    @SerializedName("closed")
    CLOSED,
    @SerializedName("under_maintenance")
    UNDER_MAINTENANCE
}

data class CreateFacilityRequest(
    val name: String,
    @SerializedName("location_description")
    val locationDescription: String,
    @SerializedName("min_capacity")
    val minCapacity: Int,
    @SerializedName("max_capacity")
    val maxCapacity: Int,
    val type: String,
    val status: String
)

data class FacilityReservation(
    @SerializedName("facility_id")
    val facilityId: Int,
    @SerializedName("team_size")
    val teamSize: Int,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    val status: String
)

data class FacilityReservationResponse(
    val message: String,
    val reservation: FacilityReservation
)

data class ReserveFacilityRequest(
    @SerializedName("facility_id")
    val facilityId: Int? = null,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("team_ids")
    val teamIds: List<Int>
)
