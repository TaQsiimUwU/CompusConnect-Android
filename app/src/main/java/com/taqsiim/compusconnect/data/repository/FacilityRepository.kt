package com.taqsiim.compusconnect.data.repository

import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.model.*
import javax.inject.Inject

class FacilityRepository @Inject constructor(
    private val api: ApiService
) {
    
    suspend fun getFacilities(): Result<List<Facility>> {
        return try {
            val facilities = api.getFacilities()
            Result.success(facilities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createFacility(request: CreateFacilityRequest): Result<Facility> {
        return try {
            val facility = api.createFacility(request)
            Result.success(facility)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportFacility(request: ReportFacilityRequest): Result<Report> {
        return try {
            val report = api.reportFacility(request)
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
