package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("post_id")
    val postId: Int,
    @SerializedName("club_id")
    val clubId: Int,
    @SerializedName("event_id")
    val eventId: Int?,
    val content: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("created_at")
    val createdAt: String, // ISO format
    @SerializedName("like_count")
    val likeCount: Int,
    @SerializedName("comment_count")
    val commentCount: Int,
    @SerializedName("is_liked")
    val isLiked: Boolean
)

data class CreatePostRequest(
    @SerializedName("event_id")
    val eventId: Int?,
    val content: String,
    @SerializedName("image_url")
    val imageUrl: String?
)
