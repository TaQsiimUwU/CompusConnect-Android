package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("student_name")
    val studentName: String,
    @SerializedName("student_image_url")
    val studentImageUrl: String,
    val content: String
)

data class CommentRequest(
    val content: String
)
