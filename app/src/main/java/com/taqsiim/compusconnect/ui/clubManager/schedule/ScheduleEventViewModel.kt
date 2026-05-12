package com.taqsiim.compusconnect.ui.clubManager.schedule

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.CreateEventRequest
import com.taqsiim.compusconnect.data.repository.EventRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScheduleEventState(
    val isSubmitting: Boolean = false
)

sealed class ScheduleEventIntent {
    data class CreateEvent(val request: CreateEventRequest) : ScheduleEventIntent()
    data class CreateSession(val request: CreateEventRequest) : ScheduleEventIntent()
}

sealed class ScheduleEventEffect {
    data class ShowSnackbar(val message: String) : ScheduleEventEffect()
    data object EventCreated : ScheduleEventEffect()
}

private const val TAG = "ScheduleEventViewModel"

@HiltViewModel
class ScheduleEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : MviViewModel<ScheduleEventState, ScheduleEventIntent, ScheduleEventEffect>() {

    override fun createInitialState() = ScheduleEventState()

    override fun handleIntent(intent: ScheduleEventIntent) {
        when (intent) {
            is ScheduleEventIntent.CreateEvent -> createEvent(intent.request)
            is ScheduleEventIntent.CreateSession -> createSession(intent.request)
        }
    }

    private fun createEvent(request: CreateEventRequest) {
        viewModelScope.launch {
            setState { copy(isSubmitting = true) }
            eventRepository.createEvent(request).fold(
                onSuccess = {
                    setState { copy(isSubmitting = false) }
                    sendEffect(ScheduleEventEffect.EventCreated)
                    sendEffect(ScheduleEventEffect.ShowSnackbar("Event created successfully"))
                },
                onFailure = { e ->
                    setState { copy(isSubmitting = false) }
                    sendEffect(ScheduleEventEffect.ShowSnackbar(e.message ?: "Failed to create event"))
                }
            )
        }
    }

    private fun createSession(request: CreateEventRequest) {
        viewModelScope.launch {
            setState { copy(isSubmitting = true) }
            eventRepository.createSession(request).fold(
                onSuccess = {
                    setState { copy(isSubmitting = false) }
                    sendEffect(ScheduleEventEffect.EventCreated)
                    sendEffect(ScheduleEventEffect.ShowSnackbar("Session created successfully"))
                },
                onFailure = { e ->
                    setState { copy(isSubmitting = false) }
                    sendEffect(ScheduleEventEffect.ShowSnackbar(e.message ?: "Failed to create session"))
                }
            )
        }
    }
}
