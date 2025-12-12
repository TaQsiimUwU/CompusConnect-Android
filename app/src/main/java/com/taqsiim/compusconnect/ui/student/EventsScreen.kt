package com.taqsiim.compusconnect.ui.student

import androidx.compose.runtime.Composable
import com.taqsiim.compusconnect.viewmodel.StudentViewModel

// TODO: Implement EventsScreen composable
@Composable
fun EventsScreen(
    viewModel: StudentViewModel,
    onNavigateToEventDetail: (String) -> Unit
) {
    TODO("Implement events screen with tabs")
}

// TODO: Implement EventsList composable
@Composable
fun EventsList(
    events: List<com.taqsiim.compusconnect.data.model.Event>,
    viewModel: StudentViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    TODO("Implement events list")
}

// TODO: Implement EventCard composable
@Composable
fun EventCard(
    event: com.taqsiim.compusconnect.data.model.Event,
    onRegister: () -> Unit,
    onViewDetails: () -> Unit
) {
    TODO("Implement event card")
}

// TODO: Implement SessionsList composable
@Composable
fun SessionsList(
    sessions: List<com.taqsiim.compusconnect.data.model.Session>,
    viewModel: StudentViewModel
) {
    TODO("Implement sessions list")
}

// TODO: Implement SessionCard composable
@Composable
fun SessionCard(
    session: com.taqsiim.compusconnect.data.model.Session,
    onRegister: () -> Unit
) {
    TODO("Implement session card")
}
