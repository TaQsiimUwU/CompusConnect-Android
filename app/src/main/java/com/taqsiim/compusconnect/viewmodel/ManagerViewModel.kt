package com.taqsiim.compusconnect.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.*
import com.taqsiim.compusconnect.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _postsState = MutableStateFlow<UiState<List<Post>>>(UiState.Loading)
    val postsState: StateFlow<UiState<List<Post>>> = _postsState.asStateFlow()

    private val _eventsState = MutableStateFlow<UiState<List<Event>>>(UiState.Loading)
    val eventsState: StateFlow<UiState<List<Event>>> = _eventsState.asStateFlow()

    private val _requestedEventsState = MutableStateFlow<UiState<List<PendingEvent>>>(UiState.Loading)
    val requestedEventsState: StateFlow<UiState<List<PendingEvent>>> = _requestedEventsState.asStateFlow()

    private val _attendeesState = MutableStateFlow<UiState<List<RegisteredStudentResponse>>>(UiState.Loading)
    val attendeesState: StateFlow<UiState<List<RegisteredStudentResponse>>> = _attendeesState.asStateFlow()

    init {
        Log.d(TAG, "ManagerViewModel initialized")
        loadAllData()
    }

    private fun loadAllData() {
        loadPosts()
        loadEvents()
        loadRequestedEvents()
    }

    fun loadPosts() {
        viewModelScope.launch {
            Log.d(TAG, "Loading posts...")
            _postsState.value = UiState.Loading
            
            val result = postRepository.getPosts()
            result.fold(
                onSuccess = { posts ->
                    Log.d(TAG, "Posts loaded successfully: ${posts.size} posts")
                    _postsState.value = UiState.Success(posts)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load posts: ${error.message}")
                    _postsState.value = UiState.Error(error.message ?: "Failed to load posts")
                }
            )
        }
    }

    fun createPost(content: String, eventId: Int?, imageUrl: String? = null) {
        viewModelScope.launch {
            Log.d(TAG, "Creating post...")
            val request = CreatePostRequest(content = content, eventId = eventId, imageUrl = imageUrl)
            val result = postRepository.createPost(request)
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Post created successfully")
                    loadPosts() // Refresh posts
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to create post: ${error.message}")
                }
            )
        }
    }

    fun loadEvents() {
        viewModelScope.launch {
            Log.d(TAG, "Loading events...")
            _eventsState.value = UiState.Loading
            
            val result = eventRepository.getEvents()
            result.fold(
                onSuccess = { events ->
                    Log.d(TAG, "Events loaded successfully: ${events.size} events")
                    _eventsState.value = UiState.Success(events)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load events: ${error.message}")
                    _eventsState.value = UiState.Error(error.message ?: "Failed to load events")
                }
            )
        }
    }

    fun createEvent(request: CreateEventRequest) {
        viewModelScope.launch {
            Log.d(TAG, "Creating event...")
            val result = eventRepository.createEvent(request)
            result.fold(
                onSuccess = { event ->
                    Log.d(TAG, "Event created successfully")
                    loadEvents() // Refresh events
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to create event: ${error.message}")
                }
            )
        }
    }

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Deleting event: $eventId")
            val result = eventRepository.deleteEvent(eventId)
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Event deleted successfully")
                    loadEvents() // Refresh events
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to delete event: ${error.message}")
                }
            )
        }
    }

    fun loadRequestedEvents() {
        viewModelScope.launch {
            Log.d(TAG, "Loading requested events...")
            _requestedEventsState.value = UiState.Loading
            
            val result = eventRepository.getRequestedEvents()
            result.fold(
                onSuccess = { events ->
                    Log.d(TAG, "Requested events loaded successfully: ${events.size} events")
                    _requestedEventsState.value = UiState.Success(events)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load requested events: ${error.message}")
                    _requestedEventsState.value = UiState.Error(error.message ?: "Failed to load requested events")
                }
            )
        }
    }

    fun loadEventAttendees(eventId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Loading attendees for event: $eventId")
            _attendeesState.value = UiState.Loading
            val result = eventRepository.getEventAttendees(eventId)
            result.fold(
                onSuccess = { attendees ->
                    Log.d(TAG, "Attendees loaded successfully: ${attendees.size} attendees")
                    _attendeesState.value = UiState.Success(attendees)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load attendees: ${error.message}")
                    _attendeesState.value = UiState.Error(error.message ?: "Failed to load attendees")
                }
            )
        }
    }

    fun checkInStudent(eventId: Int, studentId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Checking in student: $studentId for event: $eventId")
            val result = eventRepository.checkInStudent(eventId, studentId)
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Student checked in successfully")
                    loadEventAttendees(eventId) // Refresh attendees
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to check in student: ${error.message}")
                }
            )
        }
    }

    fun refresh() {
        Log.d(TAG, "Refreshing all data...")
        loadAllData()
    }

    companion object {
        private const val TAG = "ManagerViewModel"
    }
}
