package com.taqsiim.compusconnect.ui.student.clubs

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Club
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.EventType
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.data.repository.ClubRepository
import com.taqsiim.compusconnect.data.repository.EventRepository
import com.taqsiim.compusconnect.data.repository.PostRepository
import com.taqsiim.compusconnect.data.repository.ReportRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import com.taqsiim.compusconnect.ui.student.home.HomeIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClubDetailState(
    val club: UiState<Club> = UiState.Loading,
    val posts: UiState<List<Post>> = UiState.Idle,
    val events: UiState<List<Event>> = UiState.Idle,
    val isJoining: Boolean = false
)

sealed class ClubDetailIntent {
    data class LoadClub(val clubId: Int) : ClubDetailIntent()
    data class LoadClubPosts(val clubId: Int) : ClubDetailIntent()
    data class LoadClubEvents(val clubId: Int) : ClubDetailIntent()
    data class JoinClub(val clubId: Int) : ClubDetailIntent()
    data class LeaveClub(val clubId: Int) : ClubDetailIntent()
    data class RegisterForEvent(val eventId: Int) : ClubDetailIntent()
    data class UnregisterFromEvent(val eventId: Int) : ClubDetailIntent()
    data class ReportClub(val clubId: Int, val reason: String, val details: String) : ClubDetailIntent()
    data class LikePost(val postId: Int) : ClubDetailIntent()
    data class UnlikePost(val postId: Int) : ClubDetailIntent()
}

sealed class ClubDetailEffect {
    data class ShowSnackbar(val message: String) : ClubDetailEffect()
}

private const val TAG = "ClubDetailViewModel"

@HiltViewModel
class ClubDetailViewModel @Inject constructor(
    private val clubRepository: ClubRepository,
    private val postRepository: PostRepository,
    private val eventRepository: EventRepository,
    private val reportRepository: ReportRepository
) : MviViewModel<ClubDetailState, ClubDetailIntent, ClubDetailEffect>() {

    override fun createInitialState() = ClubDetailState()

    override fun handleIntent(intent: ClubDetailIntent) {
        when (intent) {
            is ClubDetailIntent.LoadClub -> loadClub(intent.clubId)
            is ClubDetailIntent.LoadClubPosts -> loadClubPosts(intent.clubId)
            is ClubDetailIntent.LoadClubEvents -> loadClubEvents(intent.clubId)
            is ClubDetailIntent.JoinClub -> joinClub(intent.clubId)
            is ClubDetailIntent.LeaveClub -> leaveClub(intent.clubId)
            is ClubDetailIntent.RegisterForEvent -> registerForEvent(intent.eventId)
            is ClubDetailIntent.UnregisterFromEvent -> unregisterFromEvent(intent.eventId)
            is ClubDetailIntent.ReportClub -> reportClub(intent.clubId, intent.reason, intent.details)
            else -> {}
        }
    }

    private fun loadClub(clubId: Int) {
        viewModelScope.launch {
            setState { copy(club = UiState.Loading) }
            clubRepository.getClubDetails(clubId).fold(
                onSuccess = { club -> setState { copy(club = UiState.Success(club)) } },
                onFailure = { e -> setState { copy(club = UiState.Error(e.message ?: "Failed")) } }
            )
        }
    }

    private fun loadClubPosts(clubId: Int) {
        viewModelScope.launch {
            setState { copy(posts = UiState.Loading) }
            postRepository.getPostsByClubId(clubId).fold(
                onSuccess = { posts -> setState { copy(posts = UiState.Success(posts)) } },
                onFailure = { e -> setState { copy(posts = UiState.Error(e.message ?: "Failed to load posts")) } }
            )
        }
    }

    private fun loadClubEvents(clubId: Int) {
        viewModelScope.launch {
            setState { copy(events = UiState.Loading) }
            eventRepository.getEventsByClubId(clubId).fold(
                onSuccess = { allEvents ->
                    // Only show events (not sessions) in the club events tab
                    val events = allEvents.filter { it.type == EventType.EVENT }
                    setState { copy(events = UiState.Success(events)) }
                },
                onFailure = { e -> setState { copy(events = UiState.Error(e.message ?: "Failed to load events")) } }
            )
        }
    }

    private fun registerForEvent(eventId: Int) {
        val currentEvents = (currentState.events as? UiState.Success)?.data ?: return
        // Optimistic update
        setState {
            copy(events = UiState.Success(currentEvents.map {
                if (it.eventId == eventId) it.copy(isRegistered = true, noOfRegistrations = it.noOfRegistrations + 1) else it
            }))
        }
        viewModelScope.launch {
            eventRepository.registerForEvent(eventId).fold(
                onSuccess = { updatedEvent ->
                    val latest = (currentState.events as? UiState.Success)?.data ?: return@fold
                    setState { copy(events = UiState.Success(latest.map { if (it.eventId == eventId) updatedEvent else it })) }
                    sendEffect(ClubDetailEffect.ShowSnackbar("Registered successfully!"))
                },
                onFailure = { e ->
                    setState { copy(events = UiState.Success(currentEvents)) }
                    sendEffect(ClubDetailEffect.ShowSnackbar(e.message ?: "Failed to register"))
                }
            )
        }
    }

    private fun unregisterFromEvent(eventId: Int) {
        val currentEvents = (currentState.events as? UiState.Success)?.data ?: return
        // Optimistic update
        setState {
            copy(events = UiState.Success(currentEvents.map {
                if (it.eventId == eventId) it.copy(isRegistered = false, noOfRegistrations = (it.noOfRegistrations - 1).coerceAtLeast(0)) else it
            }))
        }
        viewModelScope.launch {
            eventRepository.unregisterFromEvent(eventId).fold(
                onSuccess = { updatedEvent ->
                    val latest = (currentState.events as? UiState.Success)?.data ?: return@fold
                    setState { copy(events = UiState.Success(latest.map { if (it.eventId == eventId) updatedEvent else it })) }
                    sendEffect(ClubDetailEffect.ShowSnackbar("Unregistered successfully"))
                },
                onFailure = { e ->
                    setState { copy(events = UiState.Success(currentEvents)) }
                    sendEffect(ClubDetailEffect.ShowSnackbar(e.message ?: "Failed to unregister"))
                }
            )
        }
    }

    private fun joinClub(clubId: Int) {
        viewModelScope.launch {
            setState { copy(isJoining = true) }
            clubRepository.joinClub(clubId).fold(
                onSuccess = {
                    setState { copy(isJoining = false) }
                    loadClub(clubId)
                    sendEffect(ClubDetailEffect.ShowSnackbar("Joined club successfully"))
                },
                onFailure = { e ->
                    setState { copy(isJoining = false) }
                    sendEffect(ClubDetailEffect.ShowSnackbar(e.message ?: "Failed to join club"))
                }
            )
        }
    }

    private fun leaveClub(clubId: Int) {
        viewModelScope.launch {
            setState { copy(isJoining = true) }
            clubRepository.leaveClub(clubId).fold(
                onSuccess = {
                    setState { copy(isJoining = false) }
                    loadClub(clubId)
                    sendEffect(ClubDetailEffect.ShowSnackbar("Left club successfully"))
                },
                onFailure = { e ->
                    setState { copy(isJoining = false) }
                    sendEffect(ClubDetailEffect.ShowSnackbar(e.message ?: "Failed to leave club"))
                }
            )
        }
    }

    private fun reportClub(clubId: Int, reason: String, details: String) {
        viewModelScope.launch {
            reportRepository.reportClub(clubId, reason, details).fold(
                onSuccess = { sendEffect(ClubDetailEffect.ShowSnackbar("Report submitted")) },
                onFailure = { e -> sendEffect(ClubDetailEffect.ShowSnackbar(e.message ?: "Failed")) }
            )
        }
    }
}
