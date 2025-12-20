package com.taqsiim.compusconnect.data.repository

import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.local.dao.CampusDao
import com.taqsiim.compusconnect.data.mapper.toDomainModel
import com.taqsiim.compusconnect.data.mapper.toEntity
import com.taqsiim.compusconnect.data.model.Club
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
    
    suspend fun joinClub(clubId: Int): Result<Club> {
        return try {
            val club = api.joinClub(clubId)
            // Update local cache
            dao.insertClubs(listOf(club.toEntity()))
            Result.success(club)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun leaveClub(clubId: Int): Result<Club> {
        return try {
            val club = api.leaveClub(clubId)
            // Update local cache
            dao.insertClubs(listOf(club.toEntity()))
            Result.success(club)
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
