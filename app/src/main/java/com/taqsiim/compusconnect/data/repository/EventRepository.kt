package com.taqsiim.compusconnect.data.repository

import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.local.dao.CampusDao
import com.taqsiim.compusconnect.data.mapper.toDomainModel
import com.taqsiim.compusconnect.data.mapper.toEntity
import com.taqsiim.compusconnect.data.model.CreateEventRequest
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.EventType
import com.taqsiim.compusconnect.data.model.RegisteredStudentResponse
import com.taqsiim.compusconnect.data.model.PendingEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val api: ApiService,
    private val dao: CampusDao
) {
    
    // Events
    suspend fun getEvents(): Result<List<Event>> {
        return try {
            val events = api.getEvents() ?: emptyList()
            if (events.isNotEmpty()) {
                dao.refreshEventsByType(events.map { it.toEntity() }, EventType.EVENT.name)
            }
            Result.success(events)
        } catch (e: Exception) {
            // Handle 204 No Content or null response
            Result.success(emptyList())
        }
    }
    
    suspend fun getEventById(id: Int): Result<Event> {
        return try {
            val event = api.getEventById(id)
            // Optionally update local cache for this single event
            // dao.insertEvents(listOf(event.toEntity()))
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createEvent(request: CreateEventRequest): Result<Event> {
        return try {
            val response = api.createEvent(request)
            val event = api.getEventById(response.eventId)
            dao.insertEvents(listOf(event.toEntity()))
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun registerForEvent(eventId: Int): Result<Event> {
        return try {
            api.registerForEvent(eventId)
            val event = api.getEventById(eventId)
            dao.insertEvents(listOf(event.toEntity()))
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unregisterFromEvent(eventId: Int): Result<Event> {
        return try {
            api.unregisterFromEvent(eventId)
            val event = api.getEventById(eventId)
            dao.insertEvents(listOf(event.toEntity()))
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sessions - TODO: Enable when backend implements /api/sessions endpoint
    /* 
    suspend fun getSessions(): Result<List<Event>> {
        return try {
            val sessions = api.getSessions()
            dao.refreshEventsByType(sessions.map { it.toEntity() }, EventType.SESSION.name)
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    */
    
    suspend fun createSession(request: CreateEventRequest): Result<Event> {
        return try {
            val response = api.createSession(request)
            val session = api.getEventById(response.eventId)
            dao.insertEvents(listOf(session.toEntity()))
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /* TODO: Enable when backend implements /api/sessions endpoint
    suspend fun registerForSession(sessionId: Int): Result<Event> {
        return try {
            val session = api.registerForSession(sessionId)
            dao.insertEvents(listOf(session.toEntity()))
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    */

    // Manager - Attendees
    suspend fun getEventAttendees(eventId: Int): Result<List<RegisteredStudentResponse>> {
        return try {
            val attendees = api.getRegisteredStudents(eventId)
            Result.success(attendees)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRegisteredStudents(eventId: Int): Result<List<RegisteredStudentResponse>> {
        return try {
            val students = api.getRegisteredStudents(eventId)
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAttendanceList(eventId: Int): Result<List<RegisteredStudentResponse>> {
        return try {
            val attendees = api.getAttendanceList(eventId)
            Result.success(attendees)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun checkInStudent(eventId: Int, studentId: Int): Result<Unit> {
        return Result.failure(UnsupportedOperationException("check-in endpoint is not in the API contract"))
    }
    
    suspend fun deleteEvent(eventId: Int): Result<Unit> {
        return try {
            api.deleteEvent(eventId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRequestedEvents(): Result<List<PendingEvent>> {
        return try {
            val events = api.getRequestedEvents()
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Local Data Access
    fun getEventsLocal(): Flow<List<Event>> {
        return dao.getEvents().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
