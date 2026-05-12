package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class PostsResponse(
    @SerializedName("newsFeed")
    val newsFeed: List<Post>
)

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
    val isLiked: Boolean,
    // Enriched client-side from clubs data (not from API)
    @Transient
    val clubName: String? = null,
    @Transient
    val clubLogoUrl: String? = null
)

data class CreatePostRequest(
    @SerializedName("event_id")
    val eventId: Int?,
    val content: String,
    @SerializedName("image_url")
    val imageUrl: String?
)

data class GetClubPostsRequest(
    @SerializedName("club_id")
    val clubId: Int
)

data class UpdatePostRequest(
    @SerializedName("new_content")
    val newContent: String
)

