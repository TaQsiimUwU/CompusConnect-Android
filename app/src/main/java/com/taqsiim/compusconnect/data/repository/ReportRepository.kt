package com.taqsiim.compusconnect.data.repository

import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.model.MessageResponse
import com.taqsiim.compusconnect.data.model.ReportClubRequest
import com.taqsiim.compusconnect.data.model.ReportEventRequest
import com.taqsiim.compusconnect.data.model.ReportFacilityRequest
import com.taqsiim.compusconnect.data.model.ReportRoomRequest
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val api: ApiService
) {
    
    suspend fun reportEvent(eventId: Int, reason: String, details: String): Result<MessageResponse> {
        return try {
            val response = api.reportEvent(ReportEventRequest(eventId, reason, details))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportRoom(roomId: Int, reason: String, details: String): Result<MessageResponse> {
        return try {
            val response = api.reportRoom(ReportRoomRequest(roomId, reason, details))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportFacility(facilityId: Int, reason: String, details: String): Result<MessageResponse> {
        return try {
            val response = api.reportFacility(ReportFacilityRequest(facilityId, reason, details))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportClub(clubId: Int, reason: String, details: String): Result<MessageResponse> {
        return try {
            val response = api.reportClub(ReportClubRequest(clubId, reason, details))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
