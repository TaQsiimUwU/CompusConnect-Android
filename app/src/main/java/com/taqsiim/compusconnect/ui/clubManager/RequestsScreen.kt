package com.taqsiim.compusconnect.ui.clubManager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.taqsiim.compusconnect.data.model.EventStatus
import com.taqsiim.compusconnect.viewmodel.ManagerViewModel

// TODO: Implement RequestsScreen composable
@Composable
fun RequestsScreen(
    viewModel: ManagerViewModel
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Requests Screen")
    }
}

// TODO: Implement RequestCard composable
@Composable
fun RequestCard(event: com.taqsiim.compusconnect.data.model.Event) {
    TODO("Implement request card")
}

// TODO: Implement StatusBadge composable
@Composable
fun StatusBadge(status: EventStatus) {
    TODO("Implement status badge")
}
