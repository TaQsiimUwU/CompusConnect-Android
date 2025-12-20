package com.taqsiim.compusconnect.data.repository

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
            val response = api.login(LoginRequest(email, password))
            tokenManager.saveToken(response.token)
            
            // Fetch full user profile
            val user = api.getUserProfile()
            
            // Cache user locally
            dao.refreshUser(user.toEntity())
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun switchRole(role: UserRole): Result<User> {
        TODO("Implement role switching")
    }

    suspend fun getMyReservations(): Result<List<Reservation>> {
        TODO("Implement get reservations")
    }
    
    suspend fun cancelReservation(reservationId: String): Result<Unit> {
        TODO("Implement cancel reservation")
    }

    suspend fun getNotifications(): Result<List<Notification>> {
        TODO("Implement get notifications")
    }
    
    // Local Data Access
    fun getUser(): Flow<User?> {
        return dao.getUser().map { it?.toDomainModel() }
    }
}
