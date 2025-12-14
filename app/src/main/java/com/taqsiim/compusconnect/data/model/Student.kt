package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    val userId: Int,
    val role: UserRole,
    @SerializedName("user_name")
    val userName: String,
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val faculty: String,
    val major: String,
    val level: Int,
    val phone: String,
    @SerializedName("picture_url")
    val pictureUrl: String
)

enum class UserRole {
    @SerializedName("student")
    STUDENT,
    @SerializedName("clubManager")
    CLUB_MANAGER
}

// Auth Request/Response
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("user_id")
    val userId: Int,
    val role: String
)
