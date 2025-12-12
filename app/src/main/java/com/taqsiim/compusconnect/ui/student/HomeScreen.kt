package com.taqsiim.compusconnect.ui.student

import androidx.compose.runtime.Composable
import com.taqsiim.compusconnect.viewmodel.StudentViewModel

// TODO: Implement HomeScreen composable
@Composable
fun HomeScreen(
    viewModel: StudentViewModel,
    onNavigateToEventDetail: (String) -> Unit
) {
    TODO("Implement home screen with feed")
}

// TODO: Implement PostCard composable
@Composable
fun PostCard(
    post: com.taqsiim.compusconnect.data.model.Post,
    onLike: () -> Unit,
    onViewDetails: () -> Unit
) {
    TODO("Implement post card")
}
