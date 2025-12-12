package com.taqsiim.compusconnect.ui

import androidx.lifecycle.ViewModel
import com.taqsiim.compusconnect.ui.theme.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _activeRole = MutableStateFlow(UserRole.Student)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    fun toggleRole() {
        _activeRole.value = if (_activeRole.value == UserRole.Student) {
            UserRole.ClubManager
        } else {
            UserRole.Student
        }
    }
}
