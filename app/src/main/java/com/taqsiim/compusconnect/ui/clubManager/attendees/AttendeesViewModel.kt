package com.taqsiim.compusconnect.ui.clubManager.attendees

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.RegisteredStudentResponse
import com.taqsiim.compusconnect.data.repository.EventRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AttendeesState(
    val attendees: UiState<List<RegisteredStudentResponse>> = UiState.Loading,
    val selectedEventId: Int? = null
)

sealed class AttendeesIntent {
    data class LoadAttendees(val eventId: Int) : AttendeesIntent()
    data class CheckInStudent(val eventId: Int, val studentId: Int) : AttendeesIntent()
}

sealed class AttendeesEffect {
    data class ShowSnackbar(val message: String) : AttendeesEffect()
    data object CheckInSuccess : AttendeesEffect()
}

private const val TAG = "AttendeesViewModel"

@HiltViewModel
class AttendeesViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : MviViewModel<AttendeesState, AttendeesIntent, AttendeesEffect>() {

    override fun createInitialState() = AttendeesState()

    override fun handleIntent(intent: AttendeesIntent) {
        when (intent) {
            is AttendeesIntent.LoadAttendees -> loadAttendees(intent.eventId)
            is AttendeesIntent.CheckInStudent -> checkInStudent(intent.eventId, intent.studentId)
        }
    }

    private fun loadAttendees(eventId: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Loading attendees for event: $eventId")
            setState { copy(attendees = UiState.Loading, selectedEventId = eventId) }
            eventRepository.getEventAttendees(eventId).fold(
                onSuccess = { list ->
                    Log.d(TAG, "Attendees loaded: ${list.size}")
                    setState { copy(attendees = UiState.Success(list)) }
                },
                onFailure = { e ->
                    setState { copy(attendees = UiState.Error(e.message ?: "Failed")) }
                }
            )
        }
    }

    private fun checkInStudent(eventId: Int, studentId: Int) {
        viewModelScope.launch {
            eventRepository.checkInStudent(eventId, studentId).fold(
                onSuccess = {
                    sendEffect(AttendeesEffect.CheckInSuccess)
                    loadAttendees(eventId)
                },
                onFailure = { e ->
                    sendEffect(AttendeesEffect.ShowSnackbar(e.message ?: "Failed to check in"))
                }
            )
        }
    }
}
