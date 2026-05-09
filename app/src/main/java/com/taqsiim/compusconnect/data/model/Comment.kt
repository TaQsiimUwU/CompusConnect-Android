package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("student_name")
    val studentName: String,
    @SerializedName("student_image_url")
    val studentImageUrl: String,
    val content: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class CommentRequest(
    @SerializedName("comment")
    val comment: String
)

data class CommentsResponse(
    val comments: List<Comment>
)
