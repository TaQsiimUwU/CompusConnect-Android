package com.taqsiim.compusconnect.ui.student.profile

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.User
import com.taqsiim.compusconnect.data.repository.UserRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val user: UiState<User> = UiState.Loading
)

sealed class ProfileIntent {
    data object LoadProfile : ProfileIntent()
}

sealed class ProfileEffect {
    data class ShowSnackbar(val message: String) : ProfileEffect()
}

private const val TAG = "ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : MviViewModel<ProfileState, ProfileIntent, ProfileEffect>() {

    override fun createInitialState() = ProfileState()

    init {
        processIntent(ProfileIntent.LoadProfile)
    }

    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> loadProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            setState { copy(user = UiState.Loading) }
            userRepository.getCurrentUser().fold(
                onSuccess = { user ->
                    Log.d(TAG, "Profile loaded: ${user.email}")
                    setState { copy(user = UiState.Success(user)) }
                },
                onFailure = { e ->
                    Log.e(TAG, "Failed to load profile: ${e.message}")
                    setState { copy(user = UiState.Error(e.message ?: "Failed")) }
                }
            )
        }
    }
}
