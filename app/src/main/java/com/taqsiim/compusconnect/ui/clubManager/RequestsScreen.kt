@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.taqsiim.compusconnect.ui.clubManager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.taqsiim.compusconnect.data.model.EventStatus
import com.taqsiim.compusconnect.data.model.PendingEvent
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.ui.clubManager.requests.RequestsViewModel
import com.taqsiim.compusconnect.ui.clubManager.requests.RequestsIntent
import com.taqsiim.compusconnect.ui.clubManager.requests.RequestsEffect
import com.taqsiim.compusconnect.mvi.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    viewModel: RequestsViewModel
) {
    val requestsScreenState by viewModel.state.collectAsState()
    val requestedEventsState = requestsScreenState.requests
    val pullRefreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }
    var eventToDelete by remember { mutableStateOf<PendingEvent?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RequestsEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    // Delete confirmation dialog
    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Event") },
            text = {
                Text("Are you sure you want to delete \"${event.title}\"? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.processIntent(RequestsIntent.DeleteEvent(event.eventId))
                        eventToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("My Requests") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = requestsScreenState.isRefreshing,
            onRefresh = { viewModel.processIntent(RequestsIntent.RefreshRequests) },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = requestedEventsState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is UiState.Idle -> { }
                is UiState.Success -> {
                    val requests = state.data
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            Text(
                                text = "All Requests",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        if (requests.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No pending requests",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        items(requests.size) { index ->
                            val request = requests[index]
                            PendingEventCard(
                                request = request,
                                isDeleting = request.eventId in requestsScreenState.deletingEventIds,
                                onDeleteClick = { eventToDelete = request }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PendingEventCard(
    request: PendingEvent,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: title + delete button (approved only)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = request.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (request.status == EventStatus.APPROVED) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(start = 8.dp).size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete event",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Status Badge
            StatusBadge(status = request.status)

            // Details
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${request.startTime} - ${request.endTime}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Max registrations: ${request.maxRegistrations}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: EventStatus) {
    val (backgroundColor, contentColor, icon, text) = when (status) {
        EventStatus.PENDING -> Quadruple(
            Color(0xFFFFF9C4),
            Color(0xFFF57F17),
            Icons.Default.AccessTime,
            "Pending"
        )
        EventStatus.APPROVED -> Quadruple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            Icons.Default.CheckCircleOutline,
            "Approved"
        )
        EventStatus.REJECTED -> Quadruple(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            Icons.Default.Close,
            "Rejected"
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
            Text(text = text, style = MaterialTheme.typography.labelLarge, color = contentColor, fontWeight = FontWeight.Bold)
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Preview(name = "Light Mode")
@Composable
fun RequestsScreenPreview() {
    MaterialExpressiveTheme {
        RequestsScreen(viewModel = androidx.lifecycle.viewmodel.compose.viewModel())
    }
}
