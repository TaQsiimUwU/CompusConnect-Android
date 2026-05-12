package com.taqsiim.compusconnect.data.repository

import android.util.Log
import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.local.TokenManager
import com.taqsiim.compusconnect.data.mapper.formatDates
import com.taqsiim.compusconnect.data.model.CancelReservationRequest
import com.taqsiim.compusconnect.data.model.LoginRequest
import com.taqsiim.compusconnect.data.model.MessageResponse
import com.taqsiim.compusconnect.data.model.Notification
import com.taqsiim.compusconnect.data.model.Reservation
import com.taqsiim.compusconnect.data.model.User
import com.taqsiim.compusconnect.data.model.UserRole
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            Log.d(TAG, "Attempting login for email: $email")

            val response = api.login(LoginRequest(email, password))
            Log.d(TAG, "Login API response received")
            Log.d(TAG, "Token: ${response.token.take(20)}...")
            Log.d(TAG, "User from login: id=${response.user.id}, email=${response.user.email}, role=${response.user.role}")

            val userRole = UserRole.fromString(response.user.role)
            Log.d(TAG, "Parsed role: $userRole")

            tokenManager.saveToken(response.token)
            Log.d(TAG, "Token saved to TokenManager")
            Log.d(TAG , "${response.user}")
            Log.d(TAG, "Fetching user profile...")
            val userProfile = api.getUserProfile()
            Log.d(TAG, "User profile fetched: userId=${userProfile.userId}, email=${userProfile.email}")

            val user = userProfile.copy(role = userRole, userId = response.user.id.toIntOrNull() ?: userProfile.userId)
            Log.d(TAG, "Combined user data: userId=${user.userId}, role=${user.role} ,${user.phone}")
            Log.d(TAG, "Login successful! Returning user with role: ${user.role}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.javaClass.simpleName} - ${e.message}")
            Log.e(TAG, "Stack trace:", e)
            Result.failure(e)
        }
    }




    suspend fun getCurrentUser(): Result<User> {
        return try {
            val user = api.getUserProfile()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyReservations(): Result<List<Reservation>> {
        return try {
            val reservations = api.getMyReservations().map { it.formatDates() }
            Result.success(reservations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelReservation(reservationId: String, startTime: String = ""): Result<MessageResponse> {
        return try {
            val response = api.cancelRoomReservation(reservationId.toInt(), CancelReservationRequest(startTime))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotifications(): Result<List<Notification>> {
        TODO("Implement get notifications")
    }

    suspend fun logout(): Result<Unit> {
        return try {
            tokenManager.clearToken()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    companion object {
        private const val TAG = "UserRepository"
    }
}
