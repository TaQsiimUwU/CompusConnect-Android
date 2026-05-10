package com.taqsiim.compusconnect.ui.student.events

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.repository.EventRepository
import com.taqsiim.compusconnect.data.repository.ReportRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/* ──────────────────────── State ──────────────────────── */

data class EventDetailState(
    val event: UiState<Event> = UiState.Loading,
    val isRegistering: Boolean = false
)

/* ──────────────────────── Intent ─────────────────────── */

sealed class EventDetailIntent {
    data class LoadEvent(val eventId: Int) : EventDetailIntent()
    data class Register(val eventId: Int) : EventDetailIntent()
    data class Unregister(val eventId: Int) : EventDetailIntent()
    data class ReportEvent(val eventId: Int, val reason: String, val details: String) : EventDetailIntent()
}

/* ──────────────────────── Effect ─────────────────────── */

sealed class EventDetailEffect {
    data class ShowSnackbar(val message: String) : EventDetailEffect()
    data object ReportSubmitted : EventDetailEffect()
}

/* ──────────────────────── ViewModel ──────────────────── */

private const val TAG = "EventDetailViewModel"

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val reportRepository: ReportRepository
) : MviViewModel<EventDetailState, EventDetailIntent, EventDetailEffect>() {

    override fun createInitialState() = EventDetailState()

    override fun handleIntent(intent: EventDetailIntent) {
        when (intent) {
            is EventDetailIntent.LoadEvent -> loadEvent(intent.eventId)
            is EventDetailIntent.Register -> registerForEvent(intent.eventId)
            is EventDetailIntent.Unregister -> unregisterFromEvent(intent.eventId)
            is EventDetailIntent.ReportEvent -> reportEvent(intent.eventId, intent.reason, intent.details)
        }
    }

    private fun loadEvent(eventId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Loading event detail: $eventId")
            setState { copy(event = UiState.Loading) }

            eventRepository.getEventById(eventId).fold(
                onSuccess = { event ->
                    Log.d(TAG, "Event detail loaded")
                    setState { copy(event = UiState.Success(event)) }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load event detail: ${error.message}")
                    setState { copy(event = UiState.Error(error.message ?: "Failed to load event")) }
                }
            )
        }
    }

    private fun registerForEvent(eventId: Int) {
        viewModelScope.launch {
            setState { copy(isRegistering = true) }

            eventRepository.registerForEvent(eventId).fold(
                onSuccess = { event ->
                    Log.d(TAG, "Registered for event")
                    setState { copy(event = UiState.Success(event), isRegistering = false) }
                    sendEffect(EventDetailEffect.ShowSnackbar("Registered successfully"))
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to register: ${error.message}")
                    setState { copy(isRegistering = false) }
                    sendEffect(EventDetailEffect.ShowSnackbar(error.message ?: "Failed to register"))
                }
            )
        }
    }

    private fun unregisterFromEvent(eventId: Int) {
        viewModelScope.launch {
            setState { copy(isRegistering = true) }

            eventRepository.unregisterFromEvent(eventId).fold(
                onSuccess = { event ->
                    Log.d(TAG, "Unregistered from event")
                    setState { copy(event = UiState.Success(event), isRegistering = false) }
                    sendEffect(EventDetailEffect.ShowSnackbar("Unregistered successfully"))
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to unregister: ${error.message}")
                    setState { copy(isRegistering = false) }
                    sendEffect(EventDetailEffect.ShowSnackbar(error.message ?: "Failed to unregister"))
                }
            )
        }
    }

    private fun reportEvent(eventId: Int, reason: String, details: String) {
        viewModelScope.launch {
            reportRepository.reportEvent(eventId, reason, details).fold(
                onSuccess = {
                    sendEffect(EventDetailEffect.ReportSubmitted)
                    sendEffect(EventDetailEffect.ShowSnackbar("Report submitted"))
                },
                onFailure = { error ->
                    sendEffect(EventDetailEffect.ShowSnackbar(error.message ?: "Failed to submit report"))
                }
            )
        }
    }
}
