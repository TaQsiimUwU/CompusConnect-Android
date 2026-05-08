package com.taqsiim.compusconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clubs")
data class ClubEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String?,
    val email: String,
    val logo: String?,
    val cover: String?,
    val followersCount: Int,
    val members: Int,
    val eventNumber: Int,
    val sessionsNumber: Int,
    val postsNumber: Int,
    val clubAdminName: String,
    val status: String, // Store enum as String
    val isJoined: Boolean
)
