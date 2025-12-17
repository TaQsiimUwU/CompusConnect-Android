package com.taqsiim.compusconnect.ui.student

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.taqsiim.compusconnect.viewmodel.StudentViewModel

// TODO: Implement EventsScreen composable
@Composable
fun EventsScreen(
    viewModel: StudentViewModel,
    onNavigateToEventDetail: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Events Screen")
    }
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
//@Composable
//fun SessionsList(
//    sessions: List<com.taqsiim.compusconnect.data.model.Session>,
//    viewModel: StudentViewModel
//) {
//    TODO("Implement sessions list")
//}

// TODO: Implement SessionCard composable
//@Composable
//fun SessionCard(
//    session: com.taqsiim.compusconnect.data.model.Session,
//    onRegister: () -> Unit
//) {
//    TODO("Implement session card")
//}
