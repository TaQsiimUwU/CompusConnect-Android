package com.taqsiim.compusconnect.ui.clubManager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.taqsiim.compusconnect.data.model.EventStatus
import com.taqsiim.compusconnect.data.model.EventType
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.viewmodel.ManagerViewModel

// Mock Data Class for Request (since Event model might not have all fields yet)
data class EventRequest(
    val id: Int,
    val title: String,
    val description: String,
    val type: EventType,
    val status: EventStatus,
    val date: String,
    val time: String,
    val expectedRegistrations: Int,
    val submittedDate: String,
    val statusDate: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    viewModel: ManagerViewModel
) {
    val requestedEventsState by viewModel.requestedEventsState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadRequestedEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Requests") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when (val state = requestedEventsState) {
            is com.taqsiim.compusconnect.viewmodel.UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is com.taqsiim.compusconnect.viewmodel.UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is com.taqsiim.compusconnect.viewmodel.UiState.Success<*> -> {
                val requests = state.data as List<com.taqsiim.compusconnect.data.model.PendingEvent>
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
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
                        PendingEventCard(request = requests[index])
                    }
                }
            }
        }
    }
}

@Composable
fun PendingEventCard(request: com.taqsiim.compusconnect.data.model.PendingEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
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
                        text = "Club: ${request.clubName}",
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
            Color(0xFFFFF9C4), // Light Yellow
            Color(0xFFF57F17), // Dark Yellow/Orange
            Icons.Default.AccessTime,
            "Pending"
        )
        EventStatus.APPROVED -> Quadruple(
            Color(0xFFE8F5E9), // Light Green
            Color(0xFF2E7D32), // Dark Green
            Icons.Default.CheckCircleOutline,
            "Approved"
        )
        EventStatus.REJECTED -> Quadruple(
            Color(0xFFFFEBEE), // Light Red
            Color(0xFFC62828), // Dark Red
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Preview(name = "Light Mode")
@Composable
fun RequestsScreenPreview() {
    CampusAppTheme(userRole = UserRole.CLUB_MANAGER) {
        RequestsScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        )
    }
}
