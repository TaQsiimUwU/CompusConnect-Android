package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Club(
    @SerializedName("club_id")
    val clubId: Int,
    val name: String,
    @SerializedName("descrption")
    val description: String,
    val status: ClubStatus,
    @SerializedName("is_joined")
    val isJoined: Boolean,
    val logo: String,
    val cover: String,
    @SerializedName("noOfevents")
    val noOfEvents: Int,
    @SerializedName("clubMangerName")
    val clubManagerName: String,
    @SerializedName("noOfFollowers")
    val noOfFollowers: Int
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
