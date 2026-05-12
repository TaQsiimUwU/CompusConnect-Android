package com.taqsiim.compusconnect.data.repository

import com.taqsiim.compusconnect.data.api.ApiService
import com.taqsiim.compusconnect.data.model.AttendanceListRequest
import com.taqsiim.compusconnect.data.mapper.formatDates
import com.taqsiim.compusconnect.data.model.CreateEventRequest
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.PendingEvent
import com.taqsiim.compusconnect.data.model.RegisteredStudentResponse
import retrofit2.HttpException
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val api: ApiService,
) {

    // Events
    suspend fun getEvents(): Result<List<Event>> {
        return try {
            val events = (api.getEvents() ?: emptyList()).map { it.formatDates() }
            Result.success(events)
        } catch (e: HttpException) {
            if (e.code() == 204) {
                Result.success(emptyList())
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventById(id: Int): Result<Event> {
        return try {
            val event = api.getEventById(id).formatDates()
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(request: CreateEventRequest): Result<Event> {
        return try {
            val response = api.createEvent(request)
            val event = api.getEventById(response.eventId).formatDates()
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerForEvent(eventId: Int): Result<Event> {
        return try {
            api.registerForEvent(eventId)
            val event = api.getEventById(eventId).formatDates()
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unregisterFromEvent(eventId: Int): Result<Event> {
        return try {
            api.unregisterFromEvent(eventId)
            val event = api.getEventById(eventId).formatDates()
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun createSession(request: CreateEventRequest): Result<Event> {
        return try {
            val response = api.createSession(request)
            val session = api.getEventById(response.eventId).formatDates()
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

    suspend fun submitAttendanceList(eventId: Int, studentIds: List<Int>): Result<Unit> {
        return try {
            api.submitAttendanceList(
                eventId = eventId,
                request = AttendanceListRequest(studentIds = studentIds)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
            val events = api.getRequestedEvents().map { it.formatDates() }
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
