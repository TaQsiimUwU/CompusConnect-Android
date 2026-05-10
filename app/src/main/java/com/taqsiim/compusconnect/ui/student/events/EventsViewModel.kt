package com.taqsiim.compusconnect.ui.student.events

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.repository.EventRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/* ──────────────────────── State ──────────────────────── */

data class EventsState(
    val events: UiState<List<Event>> = UiState.Loading,
    val isRefreshing: Boolean = false
)

/* ──────────────────────── Intent ─────────────────────── */

sealed class EventsIntent {
    data object LoadEvents : EventsIntent()
    data object Refresh : EventsIntent()
    data class RegisterForEvent(val eventId: Int) : EventsIntent()
    data class UnregisterFromEvent(val eventId: Int) : EventsIntent()
}

/* ──────────────────────── Effect ─────────────────────── */

sealed class EventsEffect {
    data class ShowSnackbar(val message: String) : EventsEffect()
}

/* ──────────────────────── ViewModel ──────────────────── */

private const val TAG = "EventsViewModel"

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : MviViewModel<EventsState, EventsIntent, EventsEffect>() {

    override fun createInitialState() = EventsState()

    init {
        processIntent(EventsIntent.LoadEvents)
    }

    override fun handleIntent(intent: EventsIntent) {
        when (intent) {
            is EventsIntent.LoadEvents -> loadEvents()
            is EventsIntent.Refresh -> refresh()
            is EventsIntent.RegisterForEvent -> registerForEvent(intent.eventId)
            is EventsIntent.UnregisterFromEvent -> unregisterFromEvent(intent.eventId)
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            Log.d(TAG, "Loading events...")
            setState { copy(events = UiState.Loading) }

            eventRepository.getEvents().fold(
                onSuccess = { events ->
                    Log.d(TAG, "Events loaded: ${events.size}")
                    setState { copy(events = UiState.Success(events)) }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to load events: ${error.message}")
                    setState { copy(events = UiState.Error(error.message ?: "Failed to load events")) }
                }
            )
        }
    }

    private fun registerForEvent(eventId: Int) {
        // Optimistic update
        val currentEvents = (currentState.events as? UiState.Success)?.data ?: return
        val updatedEvents = currentEvents.map { event ->
            if (event.eventId == eventId) event.copy(
                isRegistered = true,
                noOfRegistrations = event.noOfRegistrations + 1
            ) else event
        }
        setState { copy(events = UiState.Success(updatedEvents)) }

        viewModelScope.launch {
            eventRepository.registerForEvent(eventId).fold(
                onSuccess = { updatedEvent ->
                    // Replace with server response to get accurate counts
                    val latestEvents = (currentState.events as? UiState.Success)?.data ?: return@fold
                    val refreshedEvents = latestEvents.map { if (it.eventId == eventId) updatedEvent else it }
                    setState { copy(events = UiState.Success(refreshedEvents)) }
                    sendEffect(EventsEffect.ShowSnackbar("Registered successfully!"))
                },
                onFailure = { e ->
                    // Revert
                    setState { copy(events = UiState.Success(currentEvents)) }
                    sendEffect(EventsEffect.ShowSnackbar(e.message ?: "Failed to register"))
                }
            )
        }
    }

    private fun unregisterFromEvent(eventId: Int) {
        // Optimistic update
        val currentEvents = (currentState.events as? UiState.Success)?.data ?: return
        val updatedEvents = currentEvents.map { event ->
            if (event.eventId == eventId) event.copy(
                isRegistered = false,
                noOfRegistrations = (event.noOfRegistrations - 1).coerceAtLeast(0)
            ) else event
        }
        setState { copy(events = UiState.Success(updatedEvents)) }

        viewModelScope.launch {
            eventRepository.unregisterFromEvent(eventId).fold(
                onSuccess = { updatedEvent ->
                    val latestEvents = (currentState.events as? UiState.Success)?.data ?: return@fold
                    val refreshedEvents = latestEvents.map { if (it.eventId == eventId) updatedEvent else it }
                    setState { copy(events = UiState.Success(refreshedEvents)) }
                    sendEffect(EventsEffect.ShowSnackbar("Unregistered successfully"))
                },
                onFailure = { e ->
                    // Revert
                    setState { copy(events = UiState.Success(currentEvents)) }
                    sendEffect(EventsEffect.ShowSnackbar(e.message ?: "Failed to unregister"))
                }
            )
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }
            loadEvents()
            setState { copy(isRefreshing = false) }
        }
    }
}
