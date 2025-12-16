package com.taqsiim.compusconnect.data.repository

import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.local.dao.CampusDao
import com.taqsiim.compusconnect.data.mapper.toDomainModel
import com.taqsiim.compusconnect.data.mapper.toEntity
import com.taqsiim.compusconnect.data.model.CreateEventRequest
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.EventType
import com.taqsiim.compusconnect.data.model.RegisteredStudentResponse
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
            val events = api.getEvents()
            dao.refreshEventsByType(events.map { it.toEntity() }, EventType.EVENT.name)
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
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
            val event = api.createEvent(request)
            dao.insertEvents(listOf(event.toEntity()))
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun registerForEvent(eventId: Int): Result<Event> {
        return try {
            val event = api.registerForEvent(eventId)
            dao.insertEvents(listOf(event.toEntity()))
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unregisterFromEvent(eventId: Int): Result<Event> {
        return try {
            val event = api.unregisterFromEvent(eventId)
            dao.insertEvents(listOf(event.toEntity()))
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sessions
    suspend fun getSessions(): Result<List<Event>> {
        return try {
            val sessions = api.getSessions()
            dao.refreshEventsByType(sessions.map { it.toEntity() }, EventType.SESSION.name)
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createSession(request: CreateEventRequest): Result<Event> {
        return try {
            val session = api.createSession(request)
            dao.insertEvents(listOf(session.toEntity()))
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun registerForSession(sessionId: Int): Result<Event> {
        return try {
            val session = api.registerForSession(sessionId)
            dao.insertEvents(listOf(session.toEntity()))
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Manager - Attendees
    suspend fun getEventAttendees(eventId: Int): Result<List<RegisteredStudentResponse>> {
        return try {
            val attendees = api.getEventAttendees(eventId)
            Result.success(attendees)
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
