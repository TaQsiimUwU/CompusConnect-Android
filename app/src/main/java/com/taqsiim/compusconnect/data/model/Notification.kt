package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val isRead: Boolean
)

enum class NotificationType {
    @SerializedName("event")
    EVENT,
    @SerializedName("club")
    CLUB,
    @SerializedName("room")
    ROOM,
    @SerializedName("system")
    SYSTEM,
    @SerializedName("announcement")
    ANNOUNCEMENT
}
