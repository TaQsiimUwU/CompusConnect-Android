package com.taqsiim.compusconnect.ui.clubManager.account

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Club
import com.taqsiim.compusconnect.data.model.UpdateClubRequest
import com.taqsiim.compusconnect.data.repository.ClubRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClubAccountState(
    val club: UiState<Club> = UiState.Loading,
    val isUpdating: Boolean = false
)

sealed class ClubAccountIntent {
    data object LoadClubInfo : ClubAccountIntent()
    data class UpdateClub(val clubId: Int, val request: UpdateClubRequest) : ClubAccountIntent()
}

sealed class ClubAccountEffect {
    data class ShowSnackbar(val message: String) : ClubAccountEffect()
    data object UpdateSuccess : ClubAccountEffect()
}

private const val TAG = "ClubAccountViewModel"

@HiltViewModel
class ClubAccountViewModel @Inject constructor(
    private val clubRepository: ClubRepository
) : MviViewModel<ClubAccountState, ClubAccountIntent, ClubAccountEffect>() {

    override fun createInitialState() = ClubAccountState()

    override fun handleIntent(intent: ClubAccountIntent) {
        when (intent) {
            is ClubAccountIntent.LoadClubInfo -> loadClubInfo()
            is ClubAccountIntent.UpdateClub -> updateClub(intent.clubId, intent.request)
        }
    }

    private fun loadClubInfo() {
        viewModelScope.launch {
            setState { copy(club = UiState.Loading) }
            // TODO: Need a way to get the manager's own club ID
            // For now, this will be called with a specific club ID from the UI
            setState { copy(club = UiState.Idle) }
        }
    }

    private fun updateClub(clubId: Int, request: UpdateClubRequest) {
        viewModelScope.launch {
            setState { copy(isUpdating = true) }
            clubRepository.updateClub(clubId, request).fold(
                onSuccess = {
                    setState { copy(isUpdating = false) }
                    sendEffect(ClubAccountEffect.UpdateSuccess)
                    sendEffect(ClubAccountEffect.ShowSnackbar("Club updated successfully"))
                },
                onFailure = { e ->
                    setState { copy(isUpdating = false) }
                    sendEffect(ClubAccountEffect.ShowSnackbar(e.message ?: "Failed to update"))
                }
            )
        }
    }
}
