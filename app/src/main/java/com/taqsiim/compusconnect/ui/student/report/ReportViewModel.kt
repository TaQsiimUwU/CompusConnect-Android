package com.taqsiim.compusconnect.ui.student.report

import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.repository.ReportRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportState(
    val isSubmitting: Boolean = false
)

sealed class ReportIntent {
    data class ReportEvent(val eventId: Int, val reason: String, val details: String) : ReportIntent()
    data class ReportRoom(val roomId: Int, val reason: String, val details: String) : ReportIntent()
    data class ReportFacility(val facilityId: Int, val reason: String, val details: String) : ReportIntent()
    data class ReportClub(val clubId: Int, val reason: String, val details: String) : ReportIntent()
}

sealed class ReportEffect {
    data class ShowSnackbar(val message: String) : ReportEffect()
    data object ReportSubmitted : ReportEffect()
}

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : MviViewModel<ReportState, ReportIntent, ReportEffect>() {

    override fun createInitialState() = ReportState()

    override fun handleIntent(intent: ReportIntent) {
        when (intent) {
            is ReportIntent.ReportEvent -> report { reportRepository.reportEvent(intent.eventId, intent.reason, intent.details) }
            is ReportIntent.ReportRoom -> report { reportRepository.reportRoom(intent.roomId, intent.reason, intent.details) }
            is ReportIntent.ReportFacility -> report { reportRepository.reportFacility(intent.facilityId, intent.reason, intent.details) }
            is ReportIntent.ReportClub -> report { reportRepository.reportClub(intent.clubId, intent.reason, intent.details) }
        }
    }

    private fun report(call: suspend () -> Result<*>) {
        viewModelScope.launch {
            setState { copy(isSubmitting = true) }
            call().fold(
                onSuccess = {
                    setState { copy(isSubmitting = false) }
                    sendEffect(ReportEffect.ReportSubmitted)
                    sendEffect(ReportEffect.ShowSnackbar("Report submitted successfully"))
                },
                onFailure = { e ->
                    setState { copy(isSubmitting = false) }
                    sendEffect(ReportEffect.ShowSnackbar(e.message ?: "Failed to submit report"))
                }
            )
        }
    }
}
