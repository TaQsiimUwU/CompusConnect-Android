package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Club(
    val id: Int,
    val name: String,
    val description: String?,
    val email: String,
    val logo: String?,
    val cover: String?,
    @SerializedName("followers_count")
    val followersCount: Int,
    val members: Int,
    @SerializedName("event_number")
    val eventNumber: Int,
    @SerializedName("sessions_number")
    val sessionsNumber: Int,
    @SerializedName("posts_number")
    val postsNumber: Int,
    @SerializedName("club_admin_name")
    val clubAdminName: String,
    val status: ClubStatus,
    @SerializedName("is_joined")
    val isJoined: Boolean
)

enum class ClubStatus {
    @SerializedName("active")
    ACTIVE,
    @SerializedName("idle")
    IDLE
}

data class FollowClubRequest(
    @SerializedName("club_id")
    val clubId: Int
)

data class UpdateClubRequest(
    val name: String,
    val description: String,
    val logo: String,
    val cover: String
)
