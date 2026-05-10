package com.taqsiim.compusconnect.ui.student.notifications

import com.taqsiim.compusconnect.data.model.Notification
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class NotificationsState(
    val notifications: UiState<List<Notification>> = UiState.Loading
)

sealed class NotificationsIntent {
    data object LoadNotifications : NotificationsIntent()
}

sealed class NotificationsEffect {
    data class ShowSnackbar(val message: String) : NotificationsEffect()
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    // TODO: Inject UserRepository when notifications API is available
) : MviViewModel<NotificationsState, NotificationsIntent, NotificationsEffect>() {

    override fun createInitialState() = NotificationsState()

    init {
        processIntent(NotificationsIntent.LoadNotifications)
    }

    override fun handleIntent(intent: NotificationsIntent) {
        when (intent) {
            is NotificationsIntent.LoadNotifications -> loadNotifications()
        }
    }

    private fun loadNotifications() {
        // TODO: Implement when notifications API endpoint is available
        setState { copy(notifications = UiState.Success(emptyList())) }
    }
}
