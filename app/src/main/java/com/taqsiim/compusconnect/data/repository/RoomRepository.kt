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
    
    suspend fun reserveRoom(request: ReserveRoomRequest): Result<Reservation> {
        return try {
            val reservation = api.reserveRoom(request)
            Result.success(reservation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun cancelReservation(roomId: Int, startTime: String): Result<Unit> {
        return try {
            api.cancelRoomReservation(roomId, CancelReservationRequest(startTime))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createRoom(request: CreateRoomRequest): Result<Room> {
        return try {
            val room = api.createRoom(request)
            Result.success(room)
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
    
    suspend fun createResource(name: String): Result<Resource> {
        return try {
            val resource = api.createResource(CreateResourceRequest(name))
            Result.success(resource)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reportRoom(request: ReportRoomRequest): Result<Report> {
        return try {
            val report = api.reportRoom(request)
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
