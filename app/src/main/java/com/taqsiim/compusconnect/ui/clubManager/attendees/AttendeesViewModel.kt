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
    val selectedEventId: Int? = null,
    val attendedStudentIds: Set<Int> = emptySet(),
    val submittingStudentIds: Set<Int> = emptySet()
)

sealed class AttendeesIntent {
    data class LoadAttendees(val eventId: Int) : AttendeesIntent()
    data class MarkAttended(val eventId: Int, val studentId: Int) : AttendeesIntent()
}

sealed class AttendeesEffect {
    data class ShowSnackbar(val message: String) : AttendeesEffect()
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
            is AttendeesIntent.MarkAttended -> markAttended(intent.eventId, intent.studentId)
        }
    }

    private fun loadAttendees(eventId: Int) {
        if (eventId == currentState.selectedEventId && currentState.attendees is UiState.Success) {
            return
        }
        viewModelScope.launch {
            Log.d(TAG, "Loading attendees for event: $eventId")
            setState {
                copy(
                    attendees = UiState.Loading,
                    selectedEventId = eventId,
                    attendedStudentIds = emptySet()
                )
            }
            eventRepository.getEventAttendees(eventId).fold(
                onSuccess = { list ->
                    Log.d(TAG, "Attendees loaded: ${list.size}")
                    val attendedIds = eventRepository.getAttendanceList(eventId)
                        .getOrNull()
                        .orEmpty()
                        .map { it.studentId }
                        .toSet()
                    setState {
                        copy(
                            attendees = UiState.Success(list),
                            attendedStudentIds = attendedIds
                        )
                    }
                },
                onFailure = { e ->
                    setState { copy(attendees = UiState.Error(e.message ?: "Failed")) }
                }
            )
        }
    }

    private fun markAttended(eventId: Int, studentId: Int) {
        val attendees = (currentState.attendees as? UiState.Success)?.data.orEmpty()
        if (attendees.none { it.studentId == studentId }) return
        if (studentId in currentState.attendedStudentIds) return
        if (studentId in currentState.submittingStudentIds) return

        viewModelScope.launch {
            val updatedAttendedIds = currentState.attendedStudentIds + studentId
            setState {
                copy(
                    attendedStudentIds = updatedAttendedIds,
                    submittingStudentIds = submittingStudentIds + studentId
                )
            }

            eventRepository.submitAttendanceList(
                eventId = eventId,
                studentIds = updatedAttendedIds.toList()
            ).fold(
                onSuccess = {
                    setState { copy(submittingStudentIds = submittingStudentIds - studentId) }
                    sendEffect(AttendeesEffect.ShowSnackbar("Student marked attended"))
                },
                onFailure = { e ->
                    setState {
                        copy(
                            attendedStudentIds = attendedStudentIds - studentId,
                            submittingStudentIds = submittingStudentIds - studentId
                        )
                    }
                    sendEffect(AttendeesEffect.ShowSnackbar(e.message ?: "Failed to submit attendance"))
                }
            )
        }
    }
}
