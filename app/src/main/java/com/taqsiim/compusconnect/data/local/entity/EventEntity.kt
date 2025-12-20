package com.taqsiim.compusconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val eventId: Int,
    val clubName: String,
    val clubLogoUrl: String,
    val clubCoverUrl: String,
    val type: String, // "event" or "session"
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val location: String,
    val registrations: Int,
    val maxRegistrations: Int
)
