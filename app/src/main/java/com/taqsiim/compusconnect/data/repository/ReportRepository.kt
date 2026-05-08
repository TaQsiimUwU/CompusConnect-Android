package com.taqsiim.compusconnect.data.repository

import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.model.*
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val api: ApiService
) {
    
    suspend fun reportEvent(eventId: Int, reason: String, details: String): Result<Report> {
        return try {
            val report = api.reportEvent(ReportEventRequest(eventId, reason, details))
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportRoom(roomId: Int, reason: String, details: String): Result<Report> {
        return try {
            val report = api.reportRoom(ReportRoomRequest(roomId, reason, details))
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportFacility(facilityId: Int, reason: String, details: String): Result<Report> {
        return try {
            val report = api.reportFacility(ReportFacilityRequest(facilityId, reason, details))
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportClub(clubId: Int, reason: String, details: String): Result<Report> {
        return try {
            val report = api.reportClub(ReportClubRequest(clubId, reason, details))
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
