package com.taqsiim.compusconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey
    val userId: Int,
    val role: String,
    val userName: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val faculty: String,
    val major: String,
    val level: Int,
    val phone: String,
    val pictureUrl: String
)
