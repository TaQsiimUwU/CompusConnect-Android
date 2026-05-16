package com.taqsiim.compusconnect.ui.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.User
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.data.repository.UserRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/* ──────────────────────── State ──────────────────────── */

data class AuthState(
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val isCheckingSession: Boolean = true,
    val error: String? = null
)

/* ──────────────────────── Intent ─────────────────────── */

sealed class AuthIntent {
    data class Login(val email: String, val password: String) : AuthIntent()
    data object Logout : AuthIntent()
    data object RefreshUser : AuthIntent()
    data object ClearError : AuthIntent()
}

/* ──────────────────────── Effect ─────────────────────── */

sealed class AuthEffect {
    data class LoginSuccess(val role: UserRole) : AuthEffect()
    data class SessionRestored(val role: UserRole) : AuthEffect()
    data class ShowError(val message: String) : AuthEffect()
    data object LoggedOut : AuthEffect()
}

/* ──────────────────────── ViewModel ──────────────────── */

private const val TAG = "AuthViewModel"

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : MviViewModel<AuthState, AuthIntent, AuthEffect>() {

    override fun createInitialState() = AuthState()

    init {
        restoreSession()
    }

    override fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Login -> login(intent.email, intent.password)
            is AuthIntent.Logout -> logout()
            is AuthIntent.RefreshUser -> refreshUser()
            is AuthIntent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "Login initiated for: $email")
            setState { copy(isLoading = true, error = null) }

            val result = userRepository.login(email, password)

            result.fold(
                onSuccess = { user ->
                    Log.d(TAG, "Login successful! role=${user.role}")
                    setState { copy(currentUser = user, isLoading = false) }
                    sendEffect(AuthEffect.LoginSuccess(user.role ?: UserRole.STUDENT))
                },
                onFailure = { error ->
                    Log.e(TAG, "Login failed: ${error.message}")
                    val msg = error.message ?: "Login failed"
                    setState { copy(isLoading = false, error = msg) }
                    sendEffect(AuthEffect.ShowError(msg))
                }
            )
        }
    }

    private fun restoreSession() {
        viewModelScope.launch {
            Log.d(TAG, "Checking for saved session...")
            setState { copy(isCheckingSession = true) }

            val cachedUser = userRepository.getCachedSessionUser()
            if (cachedUser != null) {
                Log.d(TAG, "Restored cached user: ${cachedUser.email}, role=${cachedUser.role}")
                setState { copy(currentUser = cachedUser, isCheckingSession = false) }
            }

            userRepository.refreshSessionUser().fold(
                onSuccess = { user ->
                    when {
                        user != null -> {
                            Log.d(TAG, "Session refreshed from network: ${user.email}")
                            setState { copy(currentUser = user, isCheckingSession = false) }
                            if (cachedUser == null) {
                                sendEffect(AuthEffect.SessionRestored(user.role ?: UserRole.STUDENT))
                            }
                        }
                        cachedUser == null -> {
                            Log.d(TAG, "No saved session found")
                            setState { copy(currentUser = null, isCheckingSession = false) }
                        }
                        else -> {
                            Log.d(TAG, "Network session invalid, clearing cached user")
                            setState { copy(currentUser = null, isCheckingSession = false) }
                        }
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to refresh session: ${error.message}")
                    if (cachedUser == null) {
                        setState { copy(isCheckingSession = false) }
                    }
                }
            )
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            setState { copy(currentUser = null, isLoading = false, error = null) }
            sendEffect(AuthEffect.LoggedOut)
        }
    }

    private fun refreshUser() {
        viewModelScope.launch {
            val result = userRepository.getCurrentUser()
            result.fold(
                onSuccess = { user ->
                    setState { copy(currentUser = user) }
                    Log.d(TAG, "User refreshed: ${user.email}")
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to refresh user: ${error.message}")
                }
            )
        }
    }
}
