package com.taqsiim.compusconnect.data.model
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    INFO, WARNING, SUCCESS, ALERT
}
