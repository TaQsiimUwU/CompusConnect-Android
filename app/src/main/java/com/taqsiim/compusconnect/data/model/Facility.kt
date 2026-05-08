package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Facility(
    @SerializedName("facility_id")
    val facilityId: Int,
    val name: String,
    val location: String,
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
    @SerializedName("occupied")
    OCCUPIED,
    @SerializedName("maintenance")
    MAINTENANCE,
    @SerializedName("closed")
    CLOSED
}

data class CreateFacilityRequest(
    val name: String,
    val location: String,
    @SerializedName("min_capacity")
    val minCapacity: Int,
    @SerializedName("max_capacity")
    val maxCapacity: Int,
    val type: String,
    val status: String
)
