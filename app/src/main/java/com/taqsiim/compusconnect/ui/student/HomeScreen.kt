@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.taqsiim.compusconnect.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.data.model.Reservation
import com.taqsiim.compusconnect.data.model.ReservationType
import com.taqsiim.compusconnect.ui.components.PostCard
import com.taqsiim.compusconnect.ui.components.ReservationCard
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import com.taqsiim.compusconnect.viewmodel.StudentViewModel
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

/**
 * Student Home Screen
 * Shows: Quick Actions, Upcoming Reservations, Post
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StudentViewModel,
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToReservations: () -> Unit = {},
    onNavigateToRoomForm: () -> Unit = {},
    onNavigateToSportForm: () -> Unit = {},
    onNavigateToReportIssue: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    isScrolling: (Boolean) -> Unit = {},
    scrollToTop: Boolean = false,
    onScrollToTopComplete: () -> Unit = {}
) {
    // TODO: Collect state from ViewModel
    // val posts by viewModel.posts.collectAsState()
    // val reservations by viewModel.upcomingReservations.collectAsState()
    // val isLoading by viewModel.isLoading.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            // TODO: Trigger refresh in ViewModel
            delay(3500) // Mock delay
            isRefreshing = false
        }
    }

    // Mock data for now
    val posts = remember {
        listOf(
            Post(
                postId = 1,
                clubId = 101,
                eventId = null,
                content = "Welcome to the new semester! Join us for the opening ceremony.",
                imageUrl = "https://picsum.photos/seed/campus1/400/200",
                createdAt = "2023-10-01T09:00:00Z",
                likeCount = 42,
                commentCount = 5,
                isLiked = false
            ),
            Post(
                postId = 2,
                clubId = 102,
                eventId = 201,
                content = "Hackathon this weekend! Don't miss out on the prizes.",
                imageUrl = "https://picsum.photos/seed/hackathon/400/200",
                createdAt = "2023-10-05T14:30:00Z",
                likeCount = 128,
                commentCount = 23,
                isLiked = true
            ),
            Post(
                postId = 3,
                clubId = 103,
                eventId = null,
                content = "Library hours extended for finals week.",
                imageUrl = null,
                createdAt = "2023-10-10T08:00:00Z",
                likeCount = 89,
                commentCount = 12,
                isLiked = false
            )
        )
    }
    val reservations = remember {
        listOf(
            Reservation(
                reservationId = "res_001",
                title = "Study Room A",
                startTime = "2023-10-20T10:00:00Z",
                endTime = "2023-10-20T12:00:00Z",
                type = ReservationType.STUDY_ROOM
            ),
            Reservation(
                reservationId = "res_002",
                title = "Basketball Court",
                startTime = "2023-10-21T16:00:00Z",
                endTime = "2023-10-21T17:30:00Z",
                type = ReservationType.SPORT
            ),
            Reservation(
                reservationId = "res_003",
                title = "Intro to AI Workshop",
                startTime = "2023-10-22T14:00:00Z",
                endTime = "2023-10-22T16:00:00Z",
                type = ReservationType.EVENT
            )
        )
    }
    val notificationCount = 2 // Mock notification count
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()

    // Handle scroll to top
    LaunchedEffect(scrollToTop) {
        if (scrollToTop) {
            listState.animateScrollToItem(0)
            onScrollToTopComplete()
        }
    }

    // Detect scrolling state
    LaunchedEffect(scrollBehavior.state.collapsedFraction) {
        isScrolling(scrollBehavior.state.collapsedFraction > 0.0f)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Campus Connect",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (notificationCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = notificationCount.toString(),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onNavigateToNotifications) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullRefreshState,
            modifier = Modifier.padding(paddingValues),
            indicator = {
                if (isRefreshing) {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                    // Quick Actions Section
                    item {
                        QuickActionsSection(
                            onBookStudyRoom = onNavigateToRoomForm,
                            onReserveSports = onNavigateToSportForm,
                            onReportIssue = onNavigateToReportIssue
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Upcoming Reservations Section
                    if (reservations.isNotEmpty()) {
                        item {
                            UpcomingReservationsSection(
                                reservations = reservations,
                                onViewAll = onNavigateToReservations
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Posts List
                    items(posts) { post ->
                        PostCard(
                            post = post,
                            onLike = { /* viewModel.likePost(post.postId) */ },
                            onViewDetails = { onNavigateToEventDetail(post.eventId?.toString() ?: "") }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Empty state
                    if (posts.isEmpty()) {
                        item {
                            EmptyStateCard()
                        }
                    }
                }
            }
        }
    }


/**
 * Quick Actions Section
 */
@Composable
private fun QuickActionsSection(
    onBookStudyRoom: () -> Unit,
    onReserveSports: () -> Unit,
    onReportIssue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                label = "Book Study Room",
                iconBackground = Color(0xFFE3F2FD),
                iconTint = Color(0xFF1976D2),
                onClick = onBookStudyRoom,
                modifier = Modifier.weight(1f)
            )

            QuickActionButton(
                icon = Icons.Default.SportsBasketball,
                label = "Reserve Sports",
                iconBackground = Color(0xFFFFF3E0),
                iconTint = Color(0xFFF57C00),
                onClick = onReserveSports,
                modifier = Modifier.weight(1f)
            )

            QuickActionButton(
                icon = Icons.Default.Report,
                label = "Report Issue",
                iconBackground = Color(0xFFFFEBEE),
                iconTint = Color(0xFFD32F2F),
                onClick = onReportIssue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Quick Action Button
 */
@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBackground, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Upcoming Reservations Section
 */
@Composable
private fun UpcomingReservationsSection(
    reservations: List<Reservation>,
    onViewAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        SectionHeader(
            onViewAll = onViewAll
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(reservations) { reservation ->
                ReservationCard(reservation = reservation)
            }
        }
    }
}

/**
 * Section Header with Optional View All
 */
@Composable
private fun SectionHeader(
    onViewAll: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Upcoming Reservations",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (onViewAll != null) {
            TextButton(onClick = onViewAll) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Empty State Card
 */
@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Icon(
//                imageVector = Icons.Default.Feed,
//                contentDescription = null,
//                modifier = Modifier.size(64.dp),
//                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
//            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No updates Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "try to follow some clubs",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F2F2, name = "Student Home - Light")
@Composable
private fun HomeScreenPreviewLight() {
    CampusAppTheme(darkTheme = false) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Campus Connect",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    actions = {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = Color.White
                                ) {
//                                    Text(text = "3", fontSize = 12.sp)
                                }
                            }
                        ) {
                            IconButton(onClick = { /* preview only */ }) {
//                                Icon(
//                                    imageVector = Icons.Default.Notifications,
//                                    contentDescription = "Notifications"
//                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    QuickActionsSection(
                        onBookStudyRoom = {},
                        onReserveSports = {},
                        onReportIssue = {}
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    EmptyStateCard()
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Student Home - Dark")
@Composable
private fun HomeScreenPreviewDark() {
    CampusAppTheme(darkTheme = true) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Campus Connect",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    actions = {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = Color.White
                                ) {
//                                    Text(text = "3", fontSize = 12.sp)
                                }
                            }
                        ) {
                            IconButton(onClick = { /* preview only */ }) {
//                                Icon(
//                                    imageVector = Icons.Default.Notifications,
//                                    contentDescription = "Notifications"
//                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    QuickActionsSection(
                        onBookStudyRoom = {},
                        onReserveSports = {},
                        onReportIssue = {}
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }



                item {
                    EmptyStateCard()
                }
            }
        }
    }
}
