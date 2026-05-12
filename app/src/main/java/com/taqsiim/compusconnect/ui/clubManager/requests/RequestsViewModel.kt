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
    val isRefreshing: Boolean = false
)

sealed class RequestsIntent {
    data object LoadRequests : RequestsIntent()
    data object RefreshRequests : RequestsIntent()
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
            is RequestsIntent.LoadRequests -> loadRequests(isRefresh = false)
            is RequestsIntent.RefreshRequests -> loadRequests(isRefresh = true)
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
}
