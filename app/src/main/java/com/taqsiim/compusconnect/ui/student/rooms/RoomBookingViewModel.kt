package com.taqsiim.compusconnect.ui.student.rooms

import androidx.lifecycle.viewModelScope
import com.taqsiim.compusconnect.data.model.ReserveRoomRequest
import com.taqsiim.compusconnect.data.model.Resource
import com.taqsiim.compusconnect.data.model.Room
import com.taqsiim.compusconnect.data.repository.RoomRepository
import com.taqsiim.compusconnect.mvi.MviViewModel
import com.taqsiim.compusconnect.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoomBookingState(
    val rooms: UiState<List<Room>> = UiState.Loading,
    val resources: UiState<List<Resource>> = UiState.Idle,
    val isSubmitting: Boolean = false
)

sealed class RoomBookingIntent {
    data object LoadRooms : RoomBookingIntent()
    data object LoadResources : RoomBookingIntent()
    data class ReserveRoom(val request: ReserveRoomRequest) : RoomBookingIntent()
}

sealed class RoomBookingEffect {
    data class ShowSnackbar(val message: String) : RoomBookingEffect()
    data object BookingSuccess : RoomBookingEffect()
}

private const val TAG = "RoomBookingViewModel"

@HiltViewModel
class RoomBookingViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : MviViewModel<RoomBookingState, RoomBookingIntent, RoomBookingEffect>() {

    override fun createInitialState() = RoomBookingState()

    init {
        processIntent(RoomBookingIntent.LoadRooms)
        processIntent(RoomBookingIntent.LoadResources)
    }

    override fun handleIntent(intent: RoomBookingIntent) {
        when (intent) {
            is RoomBookingIntent.LoadRooms -> loadRooms()
            is RoomBookingIntent.LoadResources -> loadResources()
            is RoomBookingIntent.ReserveRoom -> reserveRoom(intent.request)
        }
    }

    private fun loadRooms() {
        viewModelScope.launch {
            setState { copy(rooms = UiState.Loading) }
            roomRepository.getRooms().fold(
                onSuccess = { rooms -> setState { copy(rooms = UiState.Success(rooms)) } },
                onFailure = { e -> setState { copy(rooms = UiState.Error(e.message ?: "Failed")) } }
            )
        }
    }

    private fun loadResources() {
        viewModelScope.launch {
            setState { copy(resources = UiState.Loading) }
            roomRepository.getResources().fold(
                onSuccess = { res -> setState { copy(resources = UiState.Success(res)) } },
                onFailure = { e -> setState { copy(resources = UiState.Error(e.message ?: "Failed")) } }
            )
        }
    }

    private fun reserveRoom(request: ReserveRoomRequest) {
        viewModelScope.launch {
            setState { copy(isSubmitting = true) }
            roomRepository.reserveRoom(request).fold(
                onSuccess = {
                    setState { copy(isSubmitting = false) }
                    sendEffect(RoomBookingEffect.BookingSuccess)
                    sendEffect(RoomBookingEffect.ShowSnackbar("Room booked successfully"))
                },
                onFailure = { e ->
                    setState { copy(isSubmitting = false) }
                    sendEffect(RoomBookingEffect.ShowSnackbar(e.message ?: "Failed to book room"))
                }
            )
        }
    }
}
