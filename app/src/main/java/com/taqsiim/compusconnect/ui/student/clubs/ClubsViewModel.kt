package com.taqsiim.compusconnect.ui.student.clubs

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Club
import com.taqsiim.compusconnect.data.repository.ClubRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/* ──────────────────────── State ──────────────────────── */

data class ClubsState(
    val clubs: UiState<List<Club>> = UiState.Loading,
    val isRefreshing: Boolean = false
)

/* ──────────────────────── Intent ─────────────────────── */

sealed class ClubsIntent {
    data object LoadClubs : ClubsIntent()
    data object Refresh : ClubsIntent()
    data class JoinClub(val clubId: Int) : ClubsIntent()
    data class LeaveClub(val clubId: Int) : ClubsIntent()
}

/* ──────────────────────── Effect ─────────────────────── */

sealed class ClubsEffect {
    data class ShowSnackbar(val message: String) : ClubsEffect()
}

/* ──────────────────────── ViewModel ──────────────────── */

private const val TAG = "ClubsViewModel"

@HiltViewModel
class ClubsViewModel @Inject constructor(
    private val clubRepository: ClubRepository
) : MviViewModel<ClubsState, ClubsIntent, ClubsEffect>() {

    override fun createInitialState() = ClubsState()

    init {
        processIntent(ClubsIntent.LoadClubs)
    }

    override fun handleIntent(intent: ClubsIntent) {
        when (intent) {
            is ClubsIntent.LoadClubs -> loadClubs()
            is ClubsIntent.Refresh -> refresh()
            is ClubsIntent.JoinClub -> joinClub(intent.clubId)
            is ClubsIntent.LeaveClub -> leaveClub(intent.clubId)
        }
    }

    private fun loadClubs() {
        viewModelScope.launch {
            Log.d(TAG, "Loading clubs...")
            setState { copy(clubs = UiState.Loading) }

            clubRepository.getClubs().fold(
                onSuccess = { clubs ->
                    Log.d(TAG, "Clubs loaded: ${clubs.size}")
                    setState { copy(clubs = UiState.Success(clubs)) }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load clubs: ${error.message}")
                    setState { copy(clubs = UiState.Error(error.message ?: "Failed to load clubs")) }
                }
            )
        }
    }

    private fun joinClub(clubId: Int) {
        // Optimistic update
        val currentClubs = (currentState.clubs as? UiState.Success)?.data ?: return
        val updatedClubs = currentClubs.map { club ->
            if (club.id == clubId) club.copy(isJoined = true, followersCount = club.followersCount + 1)
            else club
        }
        setState { copy(clubs = UiState.Success(updatedClubs)) }

        viewModelScope.launch {
            clubRepository.joinClub(clubId).fold(
                onSuccess = {
                    sendEffect(ClubsEffect.ShowSnackbar("Joined club!"))
                },
                onFailure = { e ->
                    // Revert
                    setState { copy(clubs = UiState.Success(currentClubs)) }
                    sendEffect(ClubsEffect.ShowSnackbar(e.message ?: "Failed to join club"))
                }
            )
        }
    }

    private fun leaveClub(clubId: Int) {
        // Optimistic update
        val currentClubs = (currentState.clubs as? UiState.Success)?.data ?: return
        val updatedClubs = currentClubs.map { club ->
            if (club.id == clubId) club.copy(isJoined = false, followersCount = (club.followersCount - 1).coerceAtLeast(0))
            else club
        }
        setState { copy(clubs = UiState.Success(updatedClubs)) }

        viewModelScope.launch {
            clubRepository.leaveClub(clubId).fold(
                onSuccess = {
                    sendEffect(ClubsEffect.ShowSnackbar("Left club"))
                },
                onFailure = { e ->
                    // Revert
                    setState { copy(clubs = UiState.Success(currentClubs)) }
                    sendEffect(ClubsEffect.ShowSnackbar(e.message ?: "Failed to leave club"))
                }
            )
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }
            loadClubs()
            setState { copy(isRefreshing = false) }
        }
    }
}
