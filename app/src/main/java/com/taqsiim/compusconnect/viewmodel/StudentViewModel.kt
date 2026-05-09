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
class StudentViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val eventRepository: EventRepository,
    private val clubRepository: ClubRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _postsState = MutableStateFlow<UiState<List<Post>>>(UiState.Loading)
    val postsState: StateFlow<UiState<List<Post>>> = _postsState.asStateFlow()

    private val _eventsState = MutableStateFlow<UiState<List<Event>>>(UiState.Loading)
    val eventsState: StateFlow<UiState<List<Event>>> = _eventsState.asStateFlow()

    private val _sessionsState = MutableStateFlow<UiState<List<Event>>>(UiState.Loading)
    val sessionsState: StateFlow<UiState<List<Event>>> = _sessionsState.asStateFlow()

    private val _clubsState = MutableStateFlow<UiState<List<Club>>>(UiState.Loading)
    val clubsState: StateFlow<UiState<List<Club>>> = _clubsState.asStateFlow()

    private val _reservationsState = MutableStateFlow<UiState<List<Reservation>>>(UiState.Loading)
    val reservationsState: StateFlow<UiState<List<Reservation>>> = _reservationsState.asStateFlow()

    private val _eventDetailState = MutableStateFlow<UiState<Event>>(UiState.Loading)
    val eventDetailState: StateFlow<UiState<Event>> = _eventDetailState.asStateFlow()

    private val _clubDetailState = MutableStateFlow<UiState<Club>>(UiState.Loading)
    val clubDetailState: StateFlow<UiState<Club>> = _clubDetailState.asStateFlow()

    private val _clubPostsState = MutableStateFlow<UiState<List<Post>>>(UiState.Loading)
    val clubPostsState: StateFlow<UiState<List<Post>>> = _clubPostsState.asStateFlow()

    init {
        Log.d(TAG, "StudentViewModel initialized")
        loadPosts()

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

    fun likePost(postId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Liking post: $postId")
            val result = postRepository.likePost(postId)
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Post liked successfully")
                    loadPosts()
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to like post: ${error.message}")
                }
            )
        }
    }

    fun unlikePost(postId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Unliking post: $postId")
            val result = postRepository.unlikePost(postId)
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Post unliked successfully")
                    loadPosts()
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to unlike post: ${error.message}")
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

    fun loadEventDetail(eventId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Loading event detail: $eventId")
            _eventDetailState.value = UiState.Loading

            val result = eventRepository.getEventById(eventId)
            result.fold(
                onSuccess = { event ->
                    Log.d(TAG, "Event detail loaded successfully")
                    _eventDetailState.value = UiState.Success(event)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load event detail: ${error.message}")
                    _eventDetailState.value = UiState.Error(error.message ?: "Failed to load event")
                }
            )
        }
    }

    fun registerForEvent(eventId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Registering for event: $eventId")
            val result = eventRepository.registerForEvent(eventId)
            result.fold(
                onSuccess = { event ->
                    Log.d(TAG, "Registered for event successfully")
                    loadEvents() // Refresh events
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to register for event: ${error.message}")
                }
            )
        }
    }

    fun unregisterFromEvent(eventId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Unregistering from event: $eventId")
            val result = eventRepository.unregisterFromEvent(eventId)
            result.fold(
                onSuccess = { event ->
                    Log.d(TAG, "Unregistered from event successfully")
                    loadEvents() // Refresh events
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to unregister from event: ${error.message}")
                }
            )
        }
    }

    /* TODO: Enable when backend implements /api/sessions endpoint
    fun loadSessions() {
        viewModelScope.launch {
            Log.d(TAG, "Loading sessions...")
            _sessionsState.value = UiState.Loading
            
            val result = eventRepository.getSessions()
            result.fold(
                onSuccess = { sessions ->
                    Log.d(TAG, "Sessions loaded successfully: ${sessions.size} sessions")
                    _sessionsState.value = UiState.Success(sessions)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load sessions: ${error.message}")
                    _sessionsState.value = UiState.Error(error.message ?: "Failed to load sessions")
                }
            )
        }
    }
    */

    /* TODO: Enable when backend implements /api/sessions endpoint
    fun registerForSession(sessionId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Registering for session: $sessionId")
            val result = eventRepository.registerForSession(sessionId)
            result.fold(
                onSuccess = { session ->
                    Log.d(TAG, "Registered for session successfully")
                    loadSessions() // Refresh sessions
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to register for session: ${error.message}")
                }
            )
        }
    }
    */

    fun loadClubs() {
        viewModelScope.launch {
            Log.d(TAG, "Loading clubs...")
            _clubsState.value = UiState.Loading

            val result = clubRepository.getClubs()
            result.fold(
                onSuccess = { clubs ->
                    Log.d(TAG, "Clubs loaded successfully: ${clubs.size} clubs")
                    _clubsState.value = UiState.Success(clubs)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load clubs: ${error.message}")
                    _clubsState.value = UiState.Error(error.message ?: "Failed to load clubs")
                }
            )
        }
    }

    fun joinClub(clubId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Joining club: $clubId")
            val result = clubRepository.joinClub(clubId)
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Joined club successfully")
                    loadClubs() // Refresh clubs list
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to join club: ${error.message}")
                }
            )
        }
    }

    fun leaveClub(clubId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Leaving club: $clubId")
            val result = clubRepository.leaveClub(clubId)
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Left club successfully")
                    loadClubs() // Refresh clubs list
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to leave club: ${error.message}")
                }
            )
        }
    }

    fun loadClubDetail(clubId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Loading club detail: $clubId")
            _clubDetailState.value = UiState.Loading

            val result = clubRepository.getClubDetails(clubId)
            result.fold(
                onSuccess = { club ->
                    Log.d(TAG, "Club detail loaded successfully")
                    _clubDetailState.value = UiState.Success(club)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load club detail: ${error.message}")
                    _clubDetailState.value = UiState.Error(error.message ?: "Failed to load club")
                }
            )
        }
    }

    fun loadClubPosts(eventId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Loading club posts for event: $eventId")
            _clubPostsState.value = UiState.Loading

            val result = postRepository.getPostsForEvent(eventId)
            result.fold(
                onSuccess = { posts ->
                    Log.d(TAG, "Club posts loaded successfully: ${posts.size} posts")
                    _clubPostsState.value = UiState.Success(posts)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load club posts: ${error.message}")
                    _clubPostsState.value = UiState.Error(error.message ?: "Failed to load posts")
                }
            )
        }
    }

    fun loadReservations() {
        viewModelScope.launch {
            Log.d(TAG, "Loading reservations...")
            _reservationsState.value = UiState.Loading

            val result = userRepository.getMyReservations()
            result.fold(
                onSuccess = { reservations ->
                    Log.d(
                        TAG,
                        "Reservations loaded successfully: ${reservations.size} reservations"
                    )
                    _reservationsState.value = UiState.Success(reservations)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load reservations: ${error.message}")
                    _reservationsState.value =
                        UiState.Error(error.message ?: "Failed to load reservations")
                }
            )
        }
    }

    fun cancelReservation(reservationId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Cancelling reservation: $reservationId")
            val result = userRepository.cancelReservation(reservationId)
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Reservation cancelled successfully")
                    loadReservations() // Refresh reservations
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to cancel reservation: ${error.message}")
                }
            )
        }
    }

    fun refresh() {
        Log.d(TAG, "Refreshing all data...")
        loadPosts()
    }

    companion object {
        private const val TAG = "StudentViewModel"
    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
