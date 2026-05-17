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
    /** IDs already confirmed attended (came from the server or successfully submitted) */
    val attendedStudentIds: Set<Int> = emptySet(),
    /** IDs staged locally, not yet submitted */
    val pendingStudentIds: Set<Int> = emptySet(),
    /** True while the batch submit API call is in-flight */
    val isSubmitting: Boolean = false
)

sealed class AttendeesIntent {
    data class LoadAttendees(val eventId: Int) : AttendeesIntent()
    /** Stage a student for the next batch submit */
    data class StageAttendee(val studentId: Int) : AttendeesIntent()
    /** Remove a staged student before they are submitted */
    data class UnstagePending(val studentId: Int) : AttendeesIntent()
    /** Submit all pending IDs to the server */
    data class SubmitPending(val eventId: Int) : AttendeesIntent()
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
            is AttendeesIntent.LoadAttendees   -> loadAttendees(intent.eventId)
            is AttendeesIntent.StageAttendee   -> stageAttendee(intent.studentId)
            is AttendeesIntent.UnstagePending  -> unstagePending(intent.studentId)
            is AttendeesIntent.SubmitPending   -> submitPending(intent.eventId)
        }
    }

    // -------------------------------------------------------------------------
    // Load
    // -------------------------------------------------------------------------

    private fun loadAttendees(eventId: Int) {
        if (eventId == currentState.selectedEventId && currentState.attendees is UiState.Success) return

        viewModelScope.launch {
            Log.d(TAG, "Loading attendees for event: $eventId")
            setState {
                copy(
                    attendees = UiState.Loading,
                    selectedEventId = eventId,
                    attendedStudentIds = emptySet(),
                    pendingStudentIds = emptySet()
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

    // -------------------------------------------------------------------------
    // Stage / un-stage (local only, no API)
    // -------------------------------------------------------------------------

    private fun stageAttendee(studentId: Int) {
        if (studentId in currentState.attendedStudentIds) return   // already attended
        if (studentId in currentState.pendingStudentIds) return    // already staged
        val attendees = (currentState.attendees as? UiState.Success)?.data.orEmpty()
        if (attendees.none { it.studentId == studentId }) return   // unknown student

        setState { copy(pendingStudentIds = pendingStudentIds + studentId) }
    }

    private fun unstagePending(studentId: Int) {
        setState { copy(pendingStudentIds = pendingStudentIds - studentId) }
    }

    // -------------------------------------------------------------------------
    // Batch submit
    // -------------------------------------------------------------------------

    private fun submitPending(eventId: Int) {
        val pending = currentState.pendingStudentIds
        if (pending.isEmpty()) return
        if (currentState.isSubmitting) return

        viewModelScope.launch {
            setState { copy(isSubmitting = true) }

            Log.d(TAG, "Submitting ${pending.size} new attendee(s) for event $eventId via /attendees")

            // The endpoint receives ONLY the newly staged IDs
            eventRepository.checkInAttendees(
                eventId = eventId,
                studentIds = pending.toList()
            ).fold(
                onSuccess = { response ->
                    val checkedIn = response.checkedInStudents.toSet()
                    // Merge server-confirmed IDs (or fall back to all pending if server returns empty list)
                    val confirmedIds = checkedIn.ifEmpty { pending }
                    setState {
                        copy(
                            attendedStudentIds = attendedStudentIds + confirmedIds,
                            pendingStudentIds = pendingStudentIds - confirmedIds,
                            isSubmitting = false
                        )
                    }
                    sendEffect(AttendeesEffect.ShowSnackbar("${confirmedIds.size} student(s) checked in successfully"))
                },
                onFailure = { e ->
                    setState { copy(isSubmitting = false) }
                    sendEffect(AttendeesEffect.ShowSnackbar(e.message ?: "Failed to submit attendance"))
                }
            )
        }
    }
}
