package com.taqsiim.compusconnect.data.repository

import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.model.*
import javax.inject.Inject

class RoomRepository @Inject constructor(
    private val api: ApiService
) {
    
    suspend fun getRooms(): Result<List<Room>> {
        return try {
            val rooms = api.getRooms()
            Result.success(rooms)
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
    
    suspend fun cancelReservation(roomId: Int, startTime: String): Result<MessageResponse> {
        return try {
            val response = api.cancelRoomReservation(roomId, CancelReservationRequest(startTime))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getResources(): Result<List<Resource>> {
        return try {
            val resources = api.getResources()
            Result.success(resources)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reportRoom(request: ReportRoomRequest): Result<MessageResponse> {
        return try {
            val response = api.reportRoom(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
