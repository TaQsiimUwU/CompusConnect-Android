package com.taqsiim.compusconnect.ui.student.reservations

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.Reservation
import com.taqsiim.compusconnect.data.repository.UserRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReservationsState(
    val reservations: UiState<List<Reservation>> = UiState.Loading
)

sealed class ReservationsIntent {
    data object LoadReservations : ReservationsIntent()
    data class CancelReservation(val reservation: Reservation) : ReservationsIntent()
}

sealed class ReservationsEffect {
    data class ShowSnackbar(val message: String) : ReservationsEffect()
    data object ReservationCancelled : ReservationsEffect()
}

private const val TAG = "ReservationsViewModel"

@HiltViewModel
class ReservationsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : MviViewModel<ReservationsState, ReservationsIntent, ReservationsEffect>() {

    override fun createInitialState() = ReservationsState()

    init {
        processIntent(ReservationsIntent.LoadReservations)
    }

    override fun handleIntent(intent: ReservationsIntent) {
        when (intent) {
            is ReservationsIntent.LoadReservations -> loadReservations()
            is ReservationsIntent.CancelReservation -> cancelReservation(intent.reservation)
        }
    }

    private fun loadReservations() {
        viewModelScope.launch {
            Log.d(TAG, "Loading reservations...")
            setState { copy(reservations = UiState.Loading) }
            userRepository.getMyReservations().fold(
                onSuccess = { list ->
                    Log.d(TAG, "Reservations loaded: ${list.size}")
                    setState { copy(reservations = UiState.Success(list)) }
                },
                onFailure = { e ->
                    setState { copy(reservations = UiState.Error(e.message ?: "Failed")) }
                }
            )
        }
    }

    private fun cancelReservation(reservation: Reservation) {
        viewModelScope.launch {
            userRepository.cancelReservation(reservation).fold(
                onSuccess = {
                    sendEffect(ReservationsEffect.ReservationCancelled)
                    loadReservations()
                },
                onFailure = { e ->
                    sendEffect(ReservationsEffect.ShowSnackbar(e.message ?: "Failed to cancel"))
                }
            )
        }
    }
}
