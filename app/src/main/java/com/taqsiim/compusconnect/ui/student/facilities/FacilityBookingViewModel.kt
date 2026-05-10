package com.taqsiim.compusconnect.ui.student.facilities

import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Facility
import com.taqsiim.compusconnect.data.model.ReserveFacilityRequest
import com.taqsiim.compusconnect.data.repository.FacilityRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FacilityBookingState(
    val facilities: UiState<List<Facility>> = UiState.Loading,
    val isSubmitting: Boolean = false
)

sealed class FacilityBookingIntent {
    data object LoadFacilities : FacilityBookingIntent()
    data class ReserveFacility(val facilityId: Int, val request: ReserveFacilityRequest) : FacilityBookingIntent()
}

sealed class FacilityBookingEffect {
    data class ShowSnackbar(val message: String) : FacilityBookingEffect()
    data object BookingSuccess : FacilityBookingEffect()
}

@HiltViewModel
class FacilityBookingViewModel @Inject constructor(
    private val facilityRepository: FacilityRepository
) : MviViewModel<FacilityBookingState, FacilityBookingIntent, FacilityBookingEffect>() {

    override fun createInitialState() = FacilityBookingState()

    init {
        processIntent(FacilityBookingIntent.LoadFacilities)
    }

    override fun handleIntent(intent: FacilityBookingIntent) {
        when (intent) {
            is FacilityBookingIntent.LoadFacilities -> loadFacilities()
            is FacilityBookingIntent.ReserveFacility -> reserveFacility(intent.facilityId, intent.request)
        }
    }

    private fun loadFacilities() {
        viewModelScope.launch {
            setState { copy(facilities = UiState.Loading) }
            facilityRepository.getFacilities().fold(
                onSuccess = { list -> setState { copy(facilities = UiState.Success(list)) } },
                onFailure = { e -> setState { copy(facilities = UiState.Error(e.message ?: "Failed")) } }
            )
        }
    }

    private fun reserveFacility(facilityId: Int, request: ReserveFacilityRequest) {
        viewModelScope.launch {
            setState { copy(isSubmitting = true) }
            facilityRepository.reserveFacility(facilityId, request).fold(
                onSuccess = {
                    setState { copy(isSubmitting = false) }
                    sendEffect(FacilityBookingEffect.BookingSuccess)
                    sendEffect(FacilityBookingEffect.ShowSnackbar("Facility reserved successfully"))
                },
                onFailure = { e ->
                    setState { copy(isSubmitting = false) }
                    sendEffect(FacilityBookingEffect.ShowSnackbar(e.message ?: "Failed to reserve"))
                }
            )
        }
    }
}
