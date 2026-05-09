package com.taqsiim.compusconnect.data.repository

import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.local.dao.CampusDao
import com.taqsiim.compusconnect.data.mapper.toDomainModel
import com.taqsiim.compusconnect.data.mapper.toEntity
import com.taqsiim.compusconnect.data.model.Club
import com.taqsiim.compusconnect.data.model.MessageResponse
import com.taqsiim.compusconnect.data.model.UpdateClubRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClubRepository @Inject constructor(
    private val api: ApiService,
    private val dao: CampusDao
) {
    
    suspend fun getClubs(): Result<List<Club>> {
        return try {
            val clubs = api.getClubs()
            dao.refreshClubs(clubs.map { it.toEntity() })
            Result.success(clubs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun joinClub(clubId: Int): Result<Unit> {
        return try {
            api.followClub(clubId)
            // Refresh clubs to get updated isJoined status
            getClubs()
            Result.success(Unit)
        } catch (e: Exception) {
            // Handle 409 Conflict (already following) as success
            if (e.message?.contains("409") == true) {
                getClubs()
                Result.success(Unit)
            } else {
                Result.failure(e)
            }
        }
    }
    
    suspend fun leaveClub(clubId: Int): Result<Unit> {
        return try {
            api.unfollowClub(clubId)
            // Refresh clubs to get updated isJoined status
            getClubs()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getClubDetails(clubId: Int): Result<Club> {
        return try {
            val club = api.getClubById(clubId)
            dao.insertClubs(listOf(club.toEntity()))
            Result.success(club)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateClub(clubId: Int, request: UpdateClubRequest): Result<MessageResponse> {
        return try {
            val response = api.updateClub(clubId, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Local Data Access
    fun getClubsLocal(): Flow<List<Club>> {
        return dao.getClubs().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
