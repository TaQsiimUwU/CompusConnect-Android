package com.taqsiim.compusconnect.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taqsiim.compusconnect.data.model.Club
import com.taqsiim.compusconnect.data.model.ClubStatus
import com.taqsiim.compusconnect.viewmodel.StudentViewModel
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.launch

// TODO: Implement ClubsScreen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsScreen(
    viewModel: StudentViewModel,
    onNavigateToClubProfile: (String) -> Unit,
    isScrolling: (Boolean) -> Unit = {}
) {
    // Mock Data
    val clubs = remember {
        listOf(
            Club(
                clubId = 1,
                name = "Tech Club",
                description = "Explore cutting-edge technology, coding, and innovation",
                status = ClubStatus.ACTIVE,
                isJoined = true,
                logo = "",
                cover = "",
                noOfEvents = 12,
                clubManagerName = "John Doe",
                noOfFollowers = 245
            ),
            Club(
                clubId = 2,
                name = "Arts Club",
                description = "Express creativity through various art forms",
                status = ClubStatus.ACTIVE,
                isJoined = false,
                logo = "",
                cover = "",
                noOfEvents = 8,
                clubManagerName = "Jane Smith",
                noOfFollowers = 180
            ),
            Club(
                clubId = 3,
                name = "Entrepreneurship Club",
                description = "Business",
                status = ClubStatus.ACTIVE,
                isJoined = false,
                logo = "",
                cover = "",
                noOfEvents = 5,
                clubManagerName = "Mike Johnson",
                noOfFollowers = 120
            ),
            Club(
                clubId = 4,
                name = "Music Club",
                description = "For all music lovers and performers. Join our band or choir!",
                status = ClubStatus.ACTIVE,
                isJoined = true,
                logo = "",
                cover = "",
                noOfEvents = 15,
                clubManagerName = "Sarah Connor",
                noOfFollowers = 300
            ),
            Club(
                clubId = 5,
                name = "Sports Club",
                description = "Promoting health and fitness through various sports activities.",
                status = ClubStatus.ACTIVE,
                isJoined = false,
                logo = "",
                cover = "",
                noOfEvents = 20,
                clubManagerName = "Tom Brady",
                noOfFollowers = 500
            ),
            Club(
                clubId = 6,
                name = "Debate Club",
                description = "Sharpen your public speaking and critical thinking skills.",
                status = ClubStatus.ACTIVE,
                isJoined = false,
                logo = "",
                cover = "",
                noOfEvents = 10,
                clubManagerName = "Sherlock Holmes",
                noOfFollowers = 90
            ),
            Club(
                clubId = 7,
                name = "Photography Club",
                description = "Capture the world through your lens. Workshops and photo walks.",
                status = ClubStatus.ACTIVE,
                isJoined = true,
                logo = "",
                cover = "",
                noOfEvents = 7,
                clubManagerName = "Peter Parker",
                noOfFollowers = 150
            ),
            Club(
                clubId = 8,
                name = "Robotics Club",
                description = "Build and program robots. Participate in competitions.",
                status = ClubStatus.ACTIVE,
                isJoined = false,
                logo = "",
                cover = "",
                noOfEvents = 6,
                clubManagerName = "Tony Stark",
                noOfFollowers = 210
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    val pagerState = rememberPagerState(pageCount = { 2 })
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
                    title = { Text("Clubs") },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search clubs...") },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        TabButton(
                            text = "Explore Clubs",
                            selected = pagerState.currentPage == 0,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "My Clubs",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = if (page == 0) "Explore Clubs" else "My Clubs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                val filteredClubs = if (page == 0) clubs else clubs.filter { it.isJoined }

                items(filteredClubs) { club ->
                    ClubCard(
                        club = club,
                        onJoinLeave = { /* TODO */ },
                        onView = { onNavigateToClubProfile(club.clubId.toString()) }
                    )
                }
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// TODO: Implement ClubCard composable
@Composable
fun ClubCard(
    club: Club,
    onJoinLeave: () -> Unit,
    onView: () -> Unit
) {
    val cardColor = when (club.clubId % 5) {
        1 -> Color(0xFF2196F3) // Blue
        2 -> Color(0xFFE91E63) // Pink
        3 -> Color(0xFF00C853) // Green
        4 -> Color(0xFFFF9800) // Orange
        0 -> Color(0xFF9C27B0) // Purple
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColor)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Logo Placeholder
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        // Icon
                    }

                    Column {
                        Text(
                            text = club.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Technology", // TODO: Add category to Club model
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = club.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 2
                        )
                    }
                }
            }

            // Stats & Actions
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(icon = Icons.Outlined.Group, text = "${club.noOfFollowers} members")
                    StatItem(icon = Icons.Outlined.CalendarToday, text = "${club.noOfEvents} events")
                    StatItem(icon = Icons.Outlined.TrendingUp, text = "Active")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (club.isJoined) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Following",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = onJoinLeave,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = cardColor)
                        ) {
                            Text("Join Club")
                        }
                    }

                    OutlinedButton(
                        onClick = onView,
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                    ) {
                        Text("View")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
