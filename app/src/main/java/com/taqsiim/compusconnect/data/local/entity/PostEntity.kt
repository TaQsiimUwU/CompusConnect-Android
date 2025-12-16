package com.taqsiim.compusconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val postId: Int,
    val clubId: Int,
    val eventId: Int?,
    val content: String,
    val imageUrl: String?,
    val createdAt: String,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean
)
