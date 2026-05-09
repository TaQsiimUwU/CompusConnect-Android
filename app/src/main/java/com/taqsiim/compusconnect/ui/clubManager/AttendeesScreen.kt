package com.taqsiim.compusconnect.ui.clubManager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.viewmodel.ManagerViewModel
import com.taqsiim.compusconnect.viewmodel.UiState
import androidx.compose.ui.platform.LocalContext
import com.taqsiim.compusconnect.utils.QrScannerUtil
import kotlinx.coroutines.launch

// Mock Data Class
data class Attendee(
    val id: String,
    val name: String,
    val studentId: String,
    val email: String,
    val major: String,
    val registeredDate: String,
    val isCheckedIn: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendeesScreen(
    onScanQrCode: () -> Unit, // This callback might be redundant if we handle scanning internally, but keeping it for flexibility
    eventId: Int = 1, // TODO: Pass actual event ID
    viewModel: ManagerViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Registered, 1: Attended
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val qrScanner = remember { QrScannerUtil(context) }
    var scannedResult by remember { mutableStateOf<String?>(null) }
    val attendeesState by viewModel.attendeesState.collectAsState()
    
    LaunchedEffect(eventId) {
        viewModel.loadEventAttendees(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Attendees", fontWeight = FontWeight.Bold)
                        Text(
                            "Manage event registrations and check-ins",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    FilledIconButton(
                        onClick = {
                            scope.launch {
                                val result = qrScanner.scanQrCode()
                                if (result != null) {
                                    scannedResult = result
                                    // TODO: Handle the scanned result (e.g., check in the attendee)
                                    println("Scanned QR Code: $result")
                                }
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFFD500F9) // Purple from screenshot
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR Code",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when (val state = attendeesState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is UiState.Success -> {
                val attendees = state.data
                AttendeeContent(
                    attendees = attendees,
                    paddingValues = paddingValues,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it }
                )
            }
        }
    }
}

@Composable
private fun AttendeeContent(
    attendees: List<com.taqsiim.compusconnect.data.model.RegisteredStudentResponse>,
    paddingValues: PaddingValues,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Registered, 1: Attended
    
    val registeredCount = attendees.size
    val attendedCount = 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Event Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI & Machine Learning Workshop",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Dec 5",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AttendeeTabButton(
                    text = "Registered ($registeredCount)",
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.AccessTime
                )
                AttendeeTabButton(
                    text = "Attended ($attendedCount)",
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircleOutline
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search attendees...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = if (selectedTab == 0) "Registered Attendees" else "Attended Attendees",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // List
            val filteredAttendees = if (selectedTab == 0) {
                attendees
            } else {
                emptyList()
            }.filter {
                searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (filteredAttendees.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No attendees found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                items(filteredAttendees) { attendee ->
                    RegisteredStudentCard(attendee = attendee)
                }
            }
        }
    }

@Composable
fun RegisteredStudentCard(
    attendee: com.taqsiim.compusconnect.data.model.RegisteredStudentResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attendee.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = attendee.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Major: ${attendee.major}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Student ID: ${attendee.studentId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AttendeeTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFFD500F9) else Color.White,
            contentColor = if (selected) Color.White else Color.Gray
        ),
        elevation = if (selected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else ButtonDefaults.buttonElevation(0.dp),
        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(text = text)
        }
    }
}

@Preview(name = "Light Mode")
@Composable
fun AttendeesScreenPreview() {
    CampusAppTheme(userRole = UserRole.CLUB_MANAGER) {
        AttendeesScreen(onScanQrCode = {}, eventId = 1)
    }
}
