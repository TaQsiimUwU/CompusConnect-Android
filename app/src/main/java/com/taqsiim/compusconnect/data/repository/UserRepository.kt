package com.taqsiim.compusconnect.data.repository

import android.util.Log
import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.local.TokenManager
import com.taqsiim.compusconnect.data.local.dao.CampusDao
import com.taqsiim.compusconnect.data.mapper.formatDates
import com.taqsiim.compusconnect.data.mapper.toDomainModel
import com.taqsiim.compusconnect.data.mapper.toEntity
import com.taqsiim.compusconnect.data.model.CancelReservationRequest
import com.taqsiim.compusconnect.data.model.LoginRequest
import com.taqsiim.compusconnect.data.model.MessageResponse
import com.taqsiim.compusconnect.data.model.Notification
import com.taqsiim.compusconnect.data.model.Reservation
import com.taqsiim.compusconnect.data.model.ReservationType
import com.taqsiim.compusconnect.data.model.User
import com.taqsiim.compusconnect.data.model.UserRole
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager,
    private val campusDao: CampusDao
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
            campusDao.refreshUser(user.toEntity())
            Log.d(TAG, "Combined user data: userId=${user.userId}, role=${user.role} ,${user.phone}")
            Log.d(TAG, "Login successful! Returning user with role: ${user.role}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.javaClass.simpleName} - ${e.message}")
            Log.e(TAG, "Stack trace:", e)
            Result.failure(e)
        }
    }




    suspend fun getCachedSessionUser(): User? {
        if (tokenManager.getTokenOnce().isNullOrBlank()) return null
        return campusDao.getUser().first()?.toDomainModel()
    }

    suspend fun refreshSessionUser(): Result<User?> {
        val token = tokenManager.getTokenOnce()
        if (token.isNullOrBlank()) {
            return Result.success(null)
        }

        val cachedUser = campusDao.getUser().first()?.toDomainModel()

        return try {
            val profile = api.getUserProfile()
            val user = profile.copy(
                role = cachedUser?.role ?: profile.role,
                userId = if (profile.userId > 0) profile.userId else cachedUser?.userId ?: profile.userId
            )
            campusDao.refreshUser(user.toEntity())
            Result.success(user)
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                clearSession()
                return Result.success(null)
            }
            if (cachedUser != null) {
                Result.success(cachedUser)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val cachedUser = campusDao.getUser().first()?.toDomainModel()
            val profile = api.getUserProfile()
            val user = profile.copy(
                role = cachedUser?.role ?: profile.role,
                userId = if (profile.userId > 0) profile.userId else cachedUser?.userId ?: profile.userId
            )
            campusDao.refreshUser(user.toEntity())
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

    suspend fun cancelReservation(reservation: Reservation): Result<MessageResponse> {
        return try {
            // The backend currently only supports cancelling via the room endpoint
            // and reservation.type might be null.
            val parsedId = Regex("\\d+").find(reservation.reservationId)?.value?.toIntOrNull() ?: 0
            
            // Convert start_time to backend expected format "yyyy-MM-dd HH:mm:ss"
            val dbStartTime = try {
                if (reservation.startTime.contains("T")) {
                    reservation.startTime.replace("T", " ").substringBefore(".").substringBefore("Z")
                } else {
                    val cleanString = reservation.startTime.substringBefore(" (")
                    val parser = java.text.SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", java.util.Locale.US)
                    val date = parser.parse(cleanString)
                    val dbFormatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).apply {
                        timeZone = java.util.TimeZone.getTimeZone("UTC")
                    }
                    date?.let { dbFormatter.format(it) } ?: reservation.startTime
                }
            } catch (e: Exception) {
                reservation.startTime
            }

            val response = when (reservation.type) {
                ReservationType.EVENT, ReservationType.SESSION -> {
                    api.unregisterFromEvent(parsedId)
                }
                else -> {
                    api.cancelRoomReservation(parsedId, CancelReservationRequest(dbStartTime))
                }
            }
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
            clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun clearSession() {
        tokenManager.clearToken()
        campusDao.clearUser()
    }


    companion object {
        private const val TAG = "UserRepository"
    }
}
