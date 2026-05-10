package com.taqsiim.compusconnect.viewmodel

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.User
import com.taqsiim.compusconnect.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "Login initiated for: $email")
            _loginState.value = LoginState.Loading
            Log.d(TAG, "LoginState -> Loading")
            
            val result = userRepository.login(email, password)
            
            result.fold(
                onSuccess = { user ->
                    Log.d(TAG, "Login successful!")
                    Log.d(TAG, "User: userId=${user.userId}, email=${user.email}, role=${user.role} , name=${user.firstName}")
                    _currentUser.update { user }
                    Log.d(TAG, "CurrentUser updated")
                    _loginState.value = LoginState.Success(user)
                    Log.d(TAG, "LoginState -> Success(${user.role})")
                    Log.d(TAG , "current: ${currentUser.value}")
                },
                onFailure = { error ->
                    Log.e(TAG, "Login failed: ${error.message}")
                    val errorMessage = error.message ?: "Login failed"
                    _loginState.value = LoginState.Error(errorMessage)
                    Log.d(TAG, "LoginState -> Error: $errorMessage")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _currentUser.value = null
            _loginState.value = LoginState.Idle
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun refreshCurrentUser() {
        viewModelScope.launch {
            val result = userRepository.getCurrentUser()
            result.fold(
                onSuccess = { user ->
                    _currentUser.update { user }
                    Log.d(TAG, "CurrentUser refreshed: ${user.email}")
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to refresh user: ${error.message}")
                }
            )
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

private const val TAG = "AuthViewModel"
