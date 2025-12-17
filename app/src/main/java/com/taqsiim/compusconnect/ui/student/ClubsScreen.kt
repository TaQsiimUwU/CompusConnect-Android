package com.taqsiim.compusconnect.ui.student

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.taqsiim.compusconnect.viewmodel.StudentViewModel

// TODO: Implement ClubsScreen composable
@Composable
fun ClubsScreen(
    viewModel: StudentViewModel
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Clubs Screen")
    }
}

// TODO: Implement ClubCard composable
@Composable
fun ClubCard(
    club: com.taqsiim.compusconnect.data.model.Club,
    onJoinLeave: () -> Unit
) {
    TODO("Implement club card")
}
