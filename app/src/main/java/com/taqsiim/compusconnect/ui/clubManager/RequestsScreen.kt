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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.taqsiim.compusconnect.data.model.EventStatus
import com.taqsiim.compusconnect.data.model.EventType
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import com.taqsiim.compusconnect.ui.theme.UserRole
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
    // Mock Data
    val requests = remember {
        listOf(
            EventRequest(
                id = 1,
                title = "React Workshop 2024",
                description = "Introduction to React and modern web development",
                type = EventType.EVENT,
                status = EventStatus.PENDING,
                date = "2024-12-15",
                time = "14:00 - 17:00",
                expectedRegistrations = 50,
                submittedDate = "Dec 1, 2024"
            ),
            EventRequest(
                id = 2,
                title = "Python Basics",
                description = "Learn Python programming fundamentals",
                type = EventType.SESSION,
                status = EventStatus.APPROVED,
                date = "2024-12-10",
                time = "10:00 - 12:00",
                expectedRegistrations = 30,
                submittedDate = "Nov 28, 2024",
                statusDate = "Nov 29, 2024"
            ),
            EventRequest(
                id = 3,
                title = "Annual Tech Fest",
                description = "Full day technology festival with workshops and competitions",
                type = EventType.EVENT,
                status = EventStatus.REJECTED,
                date = "2024-12-20",
                time = "09:00 - 18:00",
                expectedRegistrations = 200,
                submittedDate = "Nov 25, 2024",
                statusDate = "Nov 26, 2024"
            )
        )
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
            items(requests) { request ->
                RequestCard(request = request)
            }
        }
    }
}

@Composable
fun RequestCard(request: EventRequest) {
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = request.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = if (request.type == EventType.EVENT) Color(0xFFE3F2FD) else Color(0xFFF3E5F5),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (request.type == EventType.EVENT) "event" else "session",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (request.type == EventType.EVENT) Color(0xFF1976D2) else Color(0xFF7B1FA2)
                            )
                        }
                    }
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
                        text = "${request.date} • ${request.time}",
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
                        text = "Expected: ${request.expectedRegistrations} registrations",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (request.status == EventStatus.APPROVED && request.type == EventType.SESSION) {
                    Surface(
                        color = Color(0xFFE8EAF6),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Online",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF3F51B5)
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Footer
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Submitted: ${request.submittedDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (request.statusDate != null) {
                    val statusText = if (request.status == EventStatus.APPROVED) "Approved" else "Rejected"
                    val statusColor = if (request.status == EventStatus.APPROVED) Color(0xFF2E7D32) else Color(0xFFC62828)
                    Text(
                        text = "$statusText: ${request.statusDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }

                if (request.status == EventStatus.APPROVED) {
                    Button(
                        onClick = { /* TODO: Publish Announcement */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD81B60) // Pinkish color from screenshot
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publish Announcement")
                    }
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
    CampusAppTheme(userRole = UserRole.ClubManager) {
        RequestsScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        )
    }
}
