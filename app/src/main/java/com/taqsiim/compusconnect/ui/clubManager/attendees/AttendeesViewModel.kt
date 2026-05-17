package com.taqsiim.compusconnect.ui.clubManager.attendees

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.RegisteredStudentResponse
import com.taqsiim.compusconnect.data.repository.ClubRepository
import com.taqsiim.compusconnect.data.repository.EventRepository
import com.taqsiim.compusconnect.data.repository.UserRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AttendeesState(
    /** Club events for the dropdown */
    val events: UiState<List<Event>> = UiState.Loading,
    /** Registered attendees for the selected event */
    val attendees: UiState<List<RegisteredStudentResponse>> = UiState.Idle,
    val selectedEventId: Int? = null,
    /** IDs already confirmed attended (came from the server or successfully submitted) */
    val attendedStudentIds: Set<Int> = emptySet(),
    /** IDs staged locally, not yet submitted */
    val pendingStudentIds: Set<Int> = emptySet(),
    /** True while the batch submit API call is in-flight */
    val isSubmitting: Boolean = false,
    val submittingStudentIds: Set<Int> = emptySet()
)

sealed class AttendeesIntent {
    data class LoadAttendees(val eventId: Int) : AttendeesIntent()
    data class MarkAttended(val eventId: Int, val studentId: Int) : AttendeesIntent()
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
    private val eventRepository: EventRepository,
    private val clubRepository: ClubRepository,
    private val userRepository: UserRepository
) : MviViewModel<AttendeesState, AttendeesIntent, AttendeesEffect>() {

    override fun createInitialState() = AttendeesState()

    init {
        loadClubEvents()
    }

    override fun handleIntent(intent: AttendeesIntent) {
        when (intent) {
            is AttendeesIntent.LoadAttendees  -> loadAttendees(intent.eventId)
            is AttendeesIntent.MarkAttended   -> markAttended(intent.eventId, intent.studentId)
            is AttendeesIntent.StageAttendee  -> stageAttendee(intent.studentId)
            is AttendeesIntent.UnstagePending -> unstagePending(intent.studentId)
            is AttendeesIntent.SubmitPending  -> submitPending(intent.eventId)
        }
    }

    // -------------------------------------------------------------------------
    // Load club's own events
    // -------------------------------------------------------------------------

    private fun loadClubEvents() {
        viewModelScope.launch {
            setState { copy(events = UiState.Loading) }
            val clubId = resolveManagedClubId()
            if (clubId == null) {
                setState { copy(events = UiState.Error("Could not determine your club")) }
                return@launch
            }
            eventRepository.getEventsByClubId(clubId).fold(
                onSuccess = { list ->
                    Log.d(TAG, "Club events loaded: ${list.size} for club $clubId")
                    setState { copy(events = UiState.Success(list)) }
                    // Auto-select first event
                    if (list.isNotEmpty() && currentState.selectedEventId == null) {
                        loadAttendees(list.first().eventId)
                    }
                },
                onFailure = { e ->
                    setState { copy(events = UiState.Error(e.message ?: "Failed to load events")) }
                }
            )
        }
    }

    private suspend fun resolveManagedClubId(): Int? {
        val user = userRepository.getCurrentUser().getOrNull() ?: return null
        val clubs = clubRepository.getClubs().getOrNull() ?: return null
        val userName = "${user.firstName} ${user.lastName}".trim()
        return clubs.firstOrNull { it.clubAdminName.equals(userName, ignoreCase = true) }?.id
            ?: clubs.firstOrNull()?.id
    }

    // -------------------------------------------------------------------------
    // Load attendees for a specific event
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
    // Mark attended (immediate, one-by-one — kept for QR scanner flow)
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Stage / un-stage (local only, no API)
    // -------------------------------------------------------------------------

    private fun stageAttendee(studentId: Int) {
        if (studentId in currentState.attendedStudentIds) return
        if (studentId in currentState.pendingStudentIds) return
        val attendees = (currentState.attendees as? UiState.Success)?.data.orEmpty()
        if (attendees.none { it.studentId == studentId }) return
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
            Log.d(TAG, "Submitting ${pending.size} attendee(s) for event $eventId via /attendees")

            eventRepository.checkInAttendees(
                eventId = eventId,
                studentIds = pending.toList()
            ).fold(
                onSuccess = { response ->
                    val checkedIn = response.checkedInStudents.toSet()
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
