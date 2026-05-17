package com.taqsiim.compusconnect.ui.clubManager.requests

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.PendingEvent
import com.taqsiim.compusconnect.data.repository.EventRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestsState(
    val requests: UiState<List<PendingEvent>> = UiState.Loading,
    val isRefreshing: Boolean = false,
    val deletingEventIds: Set<Int> = emptySet()
)

sealed class RequestsIntent {
    data object LoadRequests : RequestsIntent()
    data object RefreshRequests : RequestsIntent()
    data class DeleteEvent(val eventId: Int) : RequestsIntent()
}

sealed class RequestsEffect {
    data class ShowSnackbar(val message: String) : RequestsEffect()
}

private const val TAG = "RequestsViewModel"

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : MviViewModel<RequestsState, RequestsIntent, RequestsEffect>() {

    override fun createInitialState() = RequestsState()

    init {
        processIntent(RequestsIntent.LoadRequests)
    }

    override fun handleIntent(intent: RequestsIntent) {
        when (intent) {
            is RequestsIntent.LoadRequests    -> loadRequests(isRefresh = false)
            is RequestsIntent.RefreshRequests -> loadRequests(isRefresh = true)
            is RequestsIntent.DeleteEvent     -> deleteEvent(intent.eventId)
        }
    }

    private fun loadRequests(isRefresh: Boolean) {
        viewModelScope.launch {
            Log.d(TAG, "Loading requested events...")
            setState {
                copy(
                    requests = if (isRefresh && requests is UiState.Success) requests else UiState.Loading,
                    isRefreshing = isRefresh
                )
            }
            eventRepository.getRequestedEvents().fold(
                onSuccess = { events ->
                    Log.d(TAG, "Requests loaded: ${events.size}")
                    setState { copy(requests = UiState.Success(events), isRefreshing = false) }
                },
                onFailure = { e ->
                    setState { copy(requests = UiState.Error(e.message ?: "Failed"), isRefreshing = false) }
                }
            )
        }
    }

    private fun deleteEvent(eventId: Int) {
        if (eventId in currentState.deletingEventIds) return
        viewModelScope.launch {
            setState { copy(deletingEventIds = deletingEventIds + eventId) }
            eventRepository.deleteEvent(eventId).fold(
                onSuccess = {
                    val current = (currentState.requests as? UiState.Success)?.data.orEmpty()
                    setState {
                        copy(
                            requests = UiState.Success(current.filter { it.eventId != eventId }),
                            deletingEventIds = deletingEventIds - eventId
                        )
                    }
                    sendEffect(RequestsEffect.ShowSnackbar("Event deleted successfully"))
                },
                onFailure = { e ->
                    setState { copy(deletingEventIds = deletingEventIds - eventId) }
                    sendEffect(RequestsEffect.ShowSnackbar(e.message ?: "Failed to delete event"))
                }
            )
        }
    }
}
