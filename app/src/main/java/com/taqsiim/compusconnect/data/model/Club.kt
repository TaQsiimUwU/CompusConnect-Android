package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Club(
    val id: Int,
    val name: String,
    val description: String? = null,
    val email: String = "",
    val logo: String? = null,
    val cover: String? = null,
    @SerializedName("followers_count")
    val followersCount: Int = 0,
    val members: Int = 0,
    @SerializedName("event_number")
    val eventNumber: Int = 0,
    @SerializedName("sessions_number")
    val sessionsNumber: Int = 0,
    @SerializedName("posts_number")
    val postsNumber: Int = 0,
    @SerializedName("club_admin_name")
    val clubAdminName: String = "",
    val status: ClubStatus = ClubStatus.ACTIVE,
    @SerializedName("is_joined")
    val isJoined: Boolean = false
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
