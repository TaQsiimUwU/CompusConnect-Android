package com.taqsiim.compusconnect.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("student_id")
    val userId: Int = 0,
    val role: UserRole? = null,
    @SerializedName("user_name")
    val userName: String = "",
    val email: String = "",
    @SerializedName("first_name")
    val firstName: String = "",
    @SerializedName("last_name")
    val lastName: String = "",
    val faculty: String = "",
    val major: String = "",
    val level: String = "",
    val phone: String? = null,
    @SerializedName("picture")
    val pictureUrl: String? = null,
    @SerializedName("in_dorms")
    val inDorms: Int = 0,
    @SerializedName("hasClub")
    val hasClub: Boolean = false
)

enum class UserRole {
    @SerializedName("student")
    STUDENT,
    @SerializedName("club_manager")
    CLUB_MANAGER,
    @SerializedName("student_manager")
    STUDENT_MANAGER;
    
    companion object {
        fun fromString(role: String): UserRole {
            return when(role.lowercase()) {
                "student" -> STUDENT
                "club_manager", "clubmanager" -> CLUB_MANAGER
                "student_manager", "studentmanager" -> STUDENT_MANAGER
                else -> STUDENT
            }
        }
    }
}

// Auth Request/Response
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: LoginUser
)

data class LoginUser(
    val id: String,
    val email: String,
    val role: String,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("user_name")
    val userName: String? = null
)
