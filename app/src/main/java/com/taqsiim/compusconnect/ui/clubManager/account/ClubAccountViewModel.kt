package com.taqsiim.compusconnect.ui.clubManager.account

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Club
import com.taqsiim.compusconnect.data.model.UpdateClubRequest
import com.taqsiim.compusconnect.data.repository.ClubRepository
import com.taqsiim.compusconnect.data.repository.UserRepository
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
    private val clubRepository: ClubRepository,
    private val userRepository: UserRepository
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
            // Get the user profile to find their club admin name, then match it against clubs
            val userResult = userRepository.getCurrentUser()
            val clubsResult = clubRepository.getClubs()

            clubsResult.fold(
                onSuccess = { clubs ->
                    val user = userResult.getOrNull()
                    // Try to find the club managed by this user
                    val userName = user?.let { "${it.firstName} ${it.lastName}".trim() } ?: ""
                    val myClub = clubs.firstOrNull { it.clubAdminName.equals(userName, ignoreCase = true) }
                        ?: clubs.firstOrNull() // fallback to first club if no match
                    
                    if (myClub != null) {
                        Log.d(TAG, "Found club: ${myClub.name} (id=${myClub.id})")
                        setState { copy(club = UiState.Success(myClub)) }
                    } else {
                        setState { copy(club = UiState.Error("No club found")) }
                    }
                },
                onFailure = { e ->
                    Log.e(TAG, "Failed to load clubs: ${e.message}")
                    setState { copy(club = UiState.Error(e.message ?: "Failed to load club info")) }
                }
            )
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
                    loadClubInfo() // Reload to reflect changes
                },
                onFailure = { e ->
                    setState { copy(isUpdating = false) }
                    sendEffect(ClubAccountEffect.ShowSnackbar(e.message ?: "Failed to update"))
                }
            )
        }
    }
}
