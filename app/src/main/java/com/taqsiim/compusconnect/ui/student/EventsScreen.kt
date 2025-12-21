package com.taqsiim.compusconnect.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.EventType
import com.taqsiim.compusconnect.viewmodel.StudentViewModel
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import android.content.res.Configuration

// TODO: Implement EventsScreen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    viewModel: StudentViewModel,
    onNavigateToEventDetail: (String) -> Unit,
    isScrolling: (Boolean) -> Unit = {}
) {
    // Mock Data
    val events = remember {
        listOf(
            Event(
                eventId = 1,
                clubName = "Tech Club",
                clubLogoUrl = "",
                clubCoverUrl = "",
                type = EventType.EVENT,
                title = "AI & Machine Learning Workshop",
                description = "Learn the fundamentals of AI...",
                startTime = "2025-12-05T15:00:00",
                endTime = "2025-12-05T18:00:00",
                location = "Engineering Building - Room 201",
                noOfRegistrations = 20,
                noOfMaxRegistrations = 60
            ),
            Event(
                eventId = 2,
                clubName = "University Events",
                clubLogoUrl = "",
                clubCoverUrl = "",
                type = EventType.EVENT,
                title = "Annual Cultural Festival",
                description = "Celebrate diversity with performances...",
                startTime = "2025-12-10T10:00:00",
                endTime = "2025-12-10T20:00:00",
                location = "Main Campus Plaza",
                noOfRegistrations = 120,
                noOfMaxRegistrations = 500
            ),
            Event(
                eventId = 3,
                clubName = "Music Club",
                clubLogoUrl = "",
                clubCoverUrl = "",
                type = EventType.EVENT,
                title = "Winter Concert",
                description = "Join us for a night of classical and modern music performances by our talented students.",
                startTime = "2025-12-15T18:00:00",
                endTime = "2025-12-15T21:00:00",
                location = "Auditorium",
                noOfRegistrations = 200,
                noOfMaxRegistrations = 300
            ),
            Event(
                eventId = 4,
                clubName = "Art Society",
                clubLogoUrl = "",
                clubCoverUrl = "",
                type = EventType.EVENT,
                title = "Modern Art Exhibition",
                description = "Showcasing the best artwork from our student body. Paintings, sculptures, and digital art.",
                startTime = "2025-12-20T10:00:00",
                endTime = "2025-12-22T18:00:00",
                location = "Art Gallery",
                noOfRegistrations = 85,
                noOfMaxRegistrations = 150
            )
        )
    }

    val sessions = remember {
        listOf(
            Event(
                eventId = 101,
                clubName = "CS Department",
                clubLogoUrl = "",
                clubCoverUrl = "",
                type = EventType.SESSION,
                title = "Study Session: Algorithms",
                description = "Group study for the upcoming Algorithms midterm. We will cover dynamic programming and graph algorithms.",
                startTime = "2025-12-06T14:00:00",
                endTime = "2025-12-06T16:00:00",
                location = "Library Room 3B",
                noOfRegistrations = 5,
                noOfMaxRegistrations = 10
            ),
            Event(
                eventId = 102,
                clubName = "Math Club",
                clubLogoUrl = "",
                clubCoverUrl = "",
                type = EventType.SESSION,
                title = "Calculus II Review",
                description = "Reviewing integration techniques and series. Bring your questions!",
                startTime = "2025-12-08T16:00:00",
                endTime = "2025-12-08T18:00:00",
                location = "Science Hall 101",
                noOfRegistrations = 15,
                noOfMaxRegistrations = 20
            ),
            Event(
                eventId = 103,
                clubName = "Physics Society",
                clubLogoUrl = "",
                clubCoverUrl = "",
                type = EventType.SESSION,
                title = "Quantum Mechanics Discussion",
                description = "Open discussion on the principles of quantum mechanics and recent discoveries.",
                startTime = "2025-12-12T15:00:00",
                endTime = "2025-12-12T17:00:00",
                location = "Physics Lab 2",
                noOfRegistrations = 8,
                noOfMaxRegistrations = 12
            ),
            Event(
                eventId = 104,
                clubName = "Language Exchange",
                clubLogoUrl = "",
                clubCoverUrl = "",
                type = EventType.SESSION,
                title = "English-Spanish Conversation",
                description = "Practice your language skills with native speakers. All levels welcome.",
                startTime = "2025-12-14T17:00:00",
                endTime = "2025-12-14T19:00:00",
                location = "Student Center Cafe",
                noOfRegistrations = 12,
                noOfMaxRegistrations = 20
            )
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Events", "Sessions")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Detect scrolling state
    LaunchedEffect(scrollBehavior.state.collapsedFraction) {
        isScrolling(scrollBehavior.state.collapsedFraction > 0.0f)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Events") },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    TabButton(
                        text = "Events",
                        selected = pagerState.currentPage == 0,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Sessions",
                        selected = pagerState.currentPage == 1,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val items = if (page == 0) events else sessions
                items(items) { event ->
                    EventCard(
                        event = event,
                        onRegister = { /* TODO */ },
                        onViewDetails = { onNavigateToEventDetail(event.eventId.toString()) }
                    )
                }
            }
        }
    }
}

// TODO: Implement EventsList composable
@Composable
fun EventsList(
    events: List<Event>,
    viewModel: StudentViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    // Not used in this implementation, logic moved to EventsScreen
}

// TODO: Implement EventCard composable
@Composable
fun EventCard(
    event: Event,
    onRegister: () -> Unit,
    onViewDetails: () -> Unit
) {
    val isRegistered = event.eventId == 1 // Mock registration status

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewDetails),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header with Color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(if (event.eventId == 1) Color(0xFF2196F3) else Color(0xFFA020F0))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Placeholder for Club Logo
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            // Icon or Image
                        }

                        if (isRegistered) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "✓ Registered",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    Text(
                        text = event.clubName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarToday, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dec 5, 2025", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Schedule, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("3:00 PM - 6:00 PM", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Place, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(event.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Group, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${event.noOfRegistrations}/${event.noOfMaxRegistrations} registered", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                LinearProgressIndicator(
                    progress = { event.noOfRegistrations.toFloat() / event.noOfMaxRegistrations },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    color = Color(0xFF2196F3),
                    trackColor = Color.LightGray.copy(alpha = 0.5f),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onViewDetails,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Text("View Details")
                    }

                    if (isRegistered) {
                        OutlinedButton(
                            onClick = { /* Cancel */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancel")
                        }
                    } else {
                        Button(
                            onClick = onRegister,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text("Register")
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Light Mode")
@androidx.compose.ui.tooling.preview.Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun EventCardPreview() {
    val event = Event(
        eventId = 1,
        clubName = "Tech Club",
        clubLogoUrl = "",
        clubCoverUrl = "",
        type = EventType.EVENT,
        title = "AI & Machine Learning Workshop",
        description = "Learn the fundamentals of AI...",
        startTime = "2025-12-05T15:00:00",
        endTime = "2025-12-05T18:00:00",
        location = "Engineering Building - Room 201",
        noOfRegistrations = 46,
        noOfMaxRegistrations = 60
    )
    CampusAppTheme {
        EventCard(event = event, onRegister = {}, onViewDetails = {})
    }
}
