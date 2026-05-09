package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Report(
    @SerializedName("report_id")
    val reportId: Int,
    @SerializedName("reported_by")
    val reportedBy: Int,
    @SerializedName("reporter_name")
    val reporterName: String,
    val type: ReportType,
    @SerializedName("target_id")
    val targetId: Int,
    val reason: String,
    val details: String,
    val status: ReportStatus,
    @SerializedName("created_at")
    val createdAt: String
)

enum class ReportType {
    @SerializedName("event")
    EVENT,
    @SerializedName("room")
    ROOM,
    @SerializedName("facility")
    FACILITY,
    @SerializedName("club")
    CLUB
}

enum class ReportStatus {
    @SerializedName("pending")
    PENDING,
    @SerializedName("in_progress")
    IN_PROGRESS,
    @SerializedName("resolved")
    RESOLVED,
    @SerializedName("rejected")
    REJECTED
}

data class ReportEventRequest(
    @SerializedName("event_id")
    val eventId: Int,
    val reason: String,
    val details: String
)

data class ReportRoomRequest(
    @SerializedName("room_id")
    val roomId: Int,
    val reason: String,
    val details: String
)

data class ReportFacilityRequest(
    @SerializedName("facility_id")
    val facilityId: Int,
    val reason: String,
    val details: String
)

data class ReportClubRequest(
    @SerializedName("club_id")
    val clubId: Int,
    val reason: String,
    val details: String
)

data class MessageResponse(
    val message: String
)
