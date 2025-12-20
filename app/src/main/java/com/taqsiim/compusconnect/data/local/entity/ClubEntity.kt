package com.taqsiim.compusconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clubs")
data class ClubEntity(
    @PrimaryKey
    val clubId: Int,
    val name: String,
    val description: String,
    val status: String, // Store enum as String
    val isJoined: Boolean,
    val logo: String,
    val cover: String,
    val noOfEvents: Int,
    val clubManagerName: String,
    val noOfFollowers: Int
)
