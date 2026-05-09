package com.taqsiim.compusconnect.data.repository

import android.util.Log
import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.local.dao.CampusDao
import com.taqsiim.compusconnect.data.model.*
import com.taqsiim.compusconnect.data.local.TokenManager
import com.taqsiim.compusconnect.data.mapper.toDomainModel
import com.taqsiim.compusconnect.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: ApiService,
    private val dao: CampusDao,
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

            dao.refreshUser(user.toEntity())
            Log.d(TAG, "User cached in database")

            Log.d(TAG, "Login successful! Returning user with role: ${user.role}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.javaClass.simpleName} - ${e.message}")
            Log.e(TAG, "Stack trace:", e)
            Result.failure(e)
        }
    }
    
    suspend fun switchRole(role: UserRole): Result<User> {
        TODO("Implement role switching")
    }
    
    suspend fun getCurrentUser(): Result<User> {
        return try {
            val user = api.getUserProfile()
            dao.refreshUser(user.toEntity())
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyReservations(): Result<List<Reservation>> {
        return try {
            val reservations = api.getMyReservations()
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

    suspend fun reserveRoom(request: ReserveRoomRequest): Result<RoomReservationResponse> {
        return try {
            val reservation = api.reserveRoom(request)
            Result.success(reservation)
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
            dao.clearUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Local Data Access
    fun getUser(): Flow<User?> {
        return dao.getUser().map { it?.toDomainModel() }
    }
    
    companion object {
        private const val TAG = "UserRepository"
    }
}
