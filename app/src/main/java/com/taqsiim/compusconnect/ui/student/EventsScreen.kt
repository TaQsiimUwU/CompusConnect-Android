package com.taqsiim.compusconnect.ui.student

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.Event
import com.taqsiim.compusconnect.data.model.EventType
import com.taqsiim.compusconnect.mvi.UiState
import com.taqsiim.compusconnect.ui.student.events.EventsIntent
import com.taqsiim.compusconnect.ui.student.events.EventsViewModel
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import kotlinx.coroutines.launch

// TODO: Implement EventsScreen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    viewModel: EventsViewModel,
    onNavigateToEventDetail: (String) -> Unit,
    isScrolling: (Boolean) -> Unit = {}
) {
    val eventsScreenState by viewModel.state.collectAsState()
    val allEvents = eventsScreenState.events

    // Split events by type for the two tabs
    val eventsState = when (allEvents) {
        is UiState.Success -> UiState.Success(allEvents.data.filter { it.type == EventType.EVENT })
        else -> allEvents
    }
    val sessionsState = when (allEvents) {
        is UiState.Success -> UiState.Success(allEvents.data.filter { it.type == EventType.SESSION })
        else -> allEvents
    }

    EventsScreenContent(
        eventsState = eventsState,
        sessionsState = sessionsState,
        onNavigateToEventDetail = onNavigateToEventDetail,
        onRegister = { eventId -> viewModel.processIntent(EventsIntent.RegisterForEvent(eventId)) },
        onUnregister = { eventId -> viewModel.processIntent(EventsIntent.UnregisterFromEvent(eventId)) },
        isScrolling = isScrolling
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventsScreenContent(
    eventsState: UiState<List<Event>>,
    sessionsState: UiState<List<Event>>,
    onNavigateToEventDetail: (String) -> Unit,
    onRegister: (Int) -> Unit = {},
    onUnregister: (Int) -> Unit = {},
    isScrolling: (Boolean) -> Unit = {}
) {
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
                val state = if (page == 0) eventsState else sessionsState
                when (state) {
                    is UiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is UiState.Error -> {
                        item {
                            Text(
                                text = state.message,
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    is UiState.Success -> {
                        items(state.data) { event ->
                            EventCard(
                                event = event,
                                onRegister = { onRegister(event.eventId) },
                                onUnregister = { onUnregister(event.eventId) },
                                onViewDetails = { onNavigateToEventDetail(event.eventId.toString()) }
                            )
                        }
                    }
                    is UiState.Idle -> { }
                }
            }
        }
    }
}

// TODO: Implement EventsList composable
@Composable
fun EventsList(
    events: List<Event>,
    onNavigateToDetail: (String) -> Unit
) {
    // Not used in this implementation, logic moved to EventsScreen
}

// TODO: Implement EventCard composable
@Composable
fun EventCard(
    event: Event,
    onRegister: () -> Unit,
    onUnregister: () -> Unit = {},
    onViewDetails: () -> Unit
) {
    val isRegistered = event.isRegistered == true // safe-cast from Boolean? to Boolean

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewDetails),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header with Cover Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                // Cover image
                if (!event.clubCoverUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = event.clubCoverUrl,
                        contentDescription = "Event cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Dark scrim overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (event.type == EventType.EVENT) Color(0xFF2196F3) else Color(0xFFA020F0))
                    )
                }

                // Content on top of cover
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Club Logo
                        if (!event.clubLogoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = event.clubLogoUrl,
                                contentDescription = "${event.clubName} logo",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) { }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (isRegistered) {
                            Surface(
                                color = Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "✓ Registered",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Column {
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
                    Text(event.startTime.substringBefore("T", event.startTime), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Schedule, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${event.startTime.substringAfter("T", "").take(5)} - ${event.endTime.substringAfter("T", "").take(5)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Place, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text( text = event.location ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            onClick = onUnregister,
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

@Preview(name = "Light Mode")
@Preview(
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

@Preview(name = "Events Screen - Light", showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Events Screen - Dark",
    showBackground = true
)
@Composable
fun EventsScreenPreview() {
    val sampleEvents = listOf(
        Event(
            eventId = 1,
            clubName = "Tech Club",
            clubLogoUrl = "",
            clubCoverUrl = "",
            type = EventType.EVENT,
            title = "AI Workshop",
            description = "Intro to machine learning basics.",
            startTime = "2025-12-05T15:00:00",
            endTime = "2025-12-05T18:00:00",
            location = "Engineering Building - Room 201",
            noOfRegistrations = 46,
            noOfMaxRegistrations = 60,
            isRegistered = true
        ),
        Event(
            eventId = 2,
            clubName = "Design Club",
            clubLogoUrl = "",
            clubCoverUrl = "",
            type = EventType.EVENT,
            title = "UX Sprint",
            description = "Hands-on UX design sprint.",
            startTime = "2025-12-06T10:00:00",
            endTime = "2025-12-06T13:00:00",
            location = "Design Lab",
            noOfRegistrations = 28,
            noOfMaxRegistrations = 40,
            isRegistered = false
        )
    )
    val sampleSessions = listOf(
        Event(
            eventId = 3,
            clubName = "Robotics Club",
            clubLogoUrl = "",
            clubCoverUrl = "",
            type = EventType.SESSION,
            title = "Robotics Lab Session",
            description = "Bring your kits and build.",
            startTime = "2025-12-07T09:00:00",
            endTime = "2025-12-07T11:00:00",
            location = "Lab A",
            noOfRegistrations = 12,
            noOfMaxRegistrations = 20,
            isRegistered = false
        )
    )

    CampusAppTheme {
        EventsScreenContent(
            eventsState = UiState.Success(sampleEvents),
            sessionsState = UiState.Success(sampleSessions),
            onNavigateToEventDetail = {}
        )
    }
}
