@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.taqsiim.compusconnect.ui.clubManager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.utils.QrScannerUtil
import com.taqsiim.compusconnect.ui.clubManager.attendees.AttendeesViewModel
import com.taqsiim.compusconnect.ui.clubManager.attendees.AttendeesIntent
import com.taqsiim.compusconnect.ui.clubManager.attendees.AttendeesEffect
import com.taqsiim.compusconnect.mvi.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendeesScreen(
    events: List<Event> = emptyList(),
    viewModel: AttendeesViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val qrScanner = remember { QrScannerUtil(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val attendeesScreenState by viewModel.state.collectAsState()
    val attendeesState = attendeesScreenState.attendees

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AttendeesEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    // Event selector state
    var isEventDropdownExpanded by remember { mutableStateOf(false) }
    val selectedEvent = remember(events, attendeesScreenState.selectedEventId) {
        val selectedId = attendeesScreenState.selectedEventId
        when {
            selectedId != null -> events.firstOrNull { it.eventId == selectedId }
            else -> events.firstOrNull()
        }
    }

    // Auto-select first event and load attendees
    LaunchedEffect(events, attendeesScreenState.selectedEventId) {
        if (events.isNotEmpty() && attendeesScreenState.selectedEventId == null) {
            viewModel.processIntent(AttendeesIntent.LoadAttendees(events.first().eventId))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                                val event = selectedEvent
                                if (event == null) {
                                    snackbarHostState.showSnackbar("Select an event before scanning")
                                    return@launch
                                }

                                val result = qrScanner.scanQrCode()
                                if (result.isNullOrBlank()) {
                                    snackbarHostState.showSnackbar("QR scan cancelled")
                                    return@launch
                                }

                                val attendees = (attendeesState as? UiState.Success)?.data.orEmpty()
                                if (attendees.isEmpty()) {
                                    snackbarHostState.showSnackbar("No attendees loaded for this event")
                                    return@launch
                                }

                                val scannedStudentId = extractStudentId(result)
                                val matchedAttendee = attendees.firstOrNull { attendee ->
                                    scannedStudentId?.let { attendee.studentId == it } == true
                                }

                                if (matchedAttendee != null) {
                                    selectedTab = 0
                                    searchQuery = matchedAttendee.name
                                    when {
                                        matchedAttendee.studentId in attendeesScreenState.attendedStudentIds ->
                                            snackbarHostState.showSnackbar("${matchedAttendee.name} is already marked attended")
                                        matchedAttendee.studentId in attendeesScreenState.pendingStudentIds ->
                                            snackbarHostState.showSnackbar("${matchedAttendee.name} is already staged")
                                        else ->
                                            viewModel.processIntent(AttendeesIntent.StageAttendee(matchedAttendee.studentId))
                                    }
                                } else {
                                    snackbarHostState.showSnackbar("Scanned student is not registered for this event")
                                }
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFFD500F9)
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
        },
        floatingActionButton = {
            val pendingCount = attendeesScreenState.pendingStudentIds.size
            AnimatedVisibility(
                visible = pendingCount > 0 && selectedTab == 0,
                enter = slideInVertically { it } + fadeIn(),
                exit  = slideOutVertically { it } + fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        selectedEvent?.let { event ->
                            viewModel.processIntent(AttendeesIntent.SubmitPending(event.eventId))
                        }
                    },
                    icon = {
                        if (attendeesScreenState.isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Upload, contentDescription = null, tint = Color.White)
                        }
                    },
                    text = {
                        Text(
                            text = if (attendeesScreenState.isSubmitting) "Submitting…" else "Submit Check-ins ($pendingCount)",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    containerColor = Color(0xFFD500F9),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Event Selector Dropdown
            if (events.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Select Event",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = isEventDropdownExpanded,
                            onExpandedChange = { isEventDropdownExpanded = !isEventDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedEvent?.title ?: "Select an event",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isEventDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = isEventDropdownExpanded,
                                onDismissRequest = { isEventDropdownExpanded = false }
                            ) {
                                events.forEach { event ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(event.title, fontWeight = FontWeight.Medium)
                                                Text(
                                                    event.startTime.substringBefore("T"),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            isEventDropdownExpanded = false
                                            viewModel.processIntent(AttendeesIntent.LoadAttendees(event.eventId))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "No events available",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Attendees content
            when (val state = attendeesState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
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

                    val attendedIds = attendeesScreenState.attendedStudentIds
                    val pendingIds  = attendeesScreenState.pendingStudentIds
                    val registeredCount = attendees.count { it.studentId !in attendedIds }
                    val attendedCount   = attendees.count { it.studentId in attendedIds }

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
                        onValueChange = { searchQuery = it },
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
                        text = "Tap the check mark beside a student to mark attendance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = if (selectedTab == 0) "Registered Attendees" else "Attended Attendees",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // List
                    val filteredAttendees = (if (selectedTab == 0) {
                        attendees.filter { it.studentId !in attendedIds }
                    } else {
                        attendees.filter { it.studentId in attendedIds }
                    }).filter {
                        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp) // room for FAB
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
                        items(
                            items = filteredAttendees,
                            key = { attendee -> attendee.studentId }
                        ) { attendee ->
                            RegisteredStudentCard(
                                attendee = attendee,
                                showAttendanceAction = selectedTab == 0,
                                isSubmittingAttendance = attendee.studentId in pendingIds,
                                onMarkAttended = {
                                    viewModel.processIntent(AttendeesIntent.StageAttendee(attendee.studentId))
                                }
                            )
                        }
                    }
                }
                is UiState.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select an event to view attendees",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegisteredStudentCard(
    attendee: com.taqsiim.compusconnect.data.model.RegisteredStudentResponse,
    showAttendanceAction: Boolean,
    isSubmittingAttendance: Boolean,
    onMarkAttended: () -> Unit
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = attendee.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    if (showAttendanceAction) {
                        FilledIconButton(
                            onClick = onMarkAttended,
                            enabled = !isSubmittingAttendance,
                            modifier = Modifier.size(34.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color(0xFFD500F9),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Mark attended"
                            )
                        }
                    }
                }
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
    MaterialExpressiveTheme {
        AttendeesScreen()
    }
}

private fun extractStudentId(qrRawValue: String): Int? {
    val digits = qrRawValue.filter { it.isDigit() }
    return digits.toIntOrNull()
}
