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
    val requests: UiState<List<PendingEvent>> = UiState.Loading
)

sealed class RequestsIntent {
    data object LoadRequests : RequestsIntent()
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
            is RequestsIntent.LoadRequests -> loadRequests()
        }
    }

    private fun loadRequests() {
        viewModelScope.launch {
            Log.d(TAG, "Loading requested events...")
            setState { copy(requests = UiState.Loading) }
            eventRepository.getRequestedEvents().fold(
                onSuccess = { events ->
                    Log.d(TAG, "Requests loaded: ${events.size}")
                    setState { copy(requests = UiState.Success(events)) }
                },
                onFailure = { e ->
                    setState { copy(requests = UiState.Error(e.message ?: "Failed")) }
                }
            )
        }
    }
}
