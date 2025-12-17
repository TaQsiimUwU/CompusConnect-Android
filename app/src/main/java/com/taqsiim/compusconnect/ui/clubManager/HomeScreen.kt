package com.taqsiim.compusconnect.ui.clubManager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.taqsiim.compusconnect.viewmodel.ManagerViewModel

// TODO: Implement ManagerHomeScreen composable
@Composable
fun ManagerHomeScreen(
    viewModel: ManagerViewModel,
    onCreatePost: () -> Unit,
    onScheduleEvent: () -> Unit,
    onScheduleSession: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Manager Home Screen")
    }
}

// TODO: Implement AnnouncementCard composable
@Composable
fun AnnouncementCard(post: com.taqsiim.compusconnect.data.model.Post) {
    TODO("Implement announcement card")
}
