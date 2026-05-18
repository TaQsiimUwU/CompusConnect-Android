package com.taqsiim.compusconnect.ui.student

import android.content.res.Configuration
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.Club
import com.taqsiim.compusconnect.data.model.ClubStatus
import com.taqsiim.compusconnect.mvi.UiState
import com.taqsiim.compusconnect.ui.student.clubs.ClubsIntent
import com.taqsiim.compusconnect.ui.student.clubs.ClubsViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsScreen(
    viewModel: ClubsViewModel,
    onNavigateToClubProfile: (String) -> Unit,
    isScrolling: (Boolean) -> Unit = {}
) {
    val clubsScreenState by viewModel.state.collectAsState()
    val clubsState = clubsScreenState.clubs

    ClubsScreenContent(
        clubsState = clubsState,
        onNavigateToClubProfile = onNavigateToClubProfile,
        onToggleJoin = { club ->
            if (club.isJoined) {
                viewModel.processIntent(ClubsIntent.LeaveClub(club.id))
            } else {
                viewModel.processIntent(ClubsIntent.JoinClub(club.id))
            }
        },
        isScrolling = isScrolling
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClubsScreenContent(
    clubsState: UiState<List<Club>>,
    onNavigateToClubProfile: (String) -> Unit,
    onToggleJoin: (Club) -> Unit,
    isScrolling: (Boolean) -> Unit = {}
) {
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

                when (clubsState) {
                    is UiState.Loading -> {
                        item { CircularProgressIndicator() }
                    }
                    is UiState.Error -> {
                        item { Text(clubsState.message) }
                    }
                    is UiState.Success -> {
                        val allClubs = clubsState.data
                        val filteredClubs = if (page == 0) allClubs else allClubs.filter { it.isJoined }
                        items(filteredClubs) { club ->
                            ClubCard(
                                club = club,
                                onJoinLeave = { onToggleJoin(club) },
                                onView = { onNavigateToClubProfile(club.id.toString()) }
                            )
                        }
                    }
                    is UiState.Idle -> { }
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
        color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0f),
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

@Composable
fun ClubCard(
    club: Club,
    onJoinLeave: () -> Unit,
    onView: () -> Unit
) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onView),
        shape = RoundedCornerShape(16.dp),
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
                // Cover image or fallback color
                if (!club.cover.isNullOrEmpty()) {
                    AsyncImage(
                        model = club.cover,
                        contentDescription = "${club.name} cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Dark scrim for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                // Content overlay
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Club Logo
                    if (!club.logo.isNullOrEmpty()) {
                        AsyncImage(
                            model = club.logo,
                            contentDescription = "${club.name} logo",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) { }
                    }

                    Column {
                        Text(
                            text = club.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = club.description ?: "No description available",
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
                    StatItem(icon = Icons.Outlined.Group, text = "${club.followersCount} members")
                    StatItem(icon = Icons.Outlined.CalendarToday, text = "${club.eventNumber} events")
                    StatItem(icon = Icons.AutoMirrored.Outlined.TrendingUp, text = "Active")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (club.isJoined) {
                        Surface(
                            onClick = onJoinLeave,
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
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Join Club")
                        }
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun ClubCardPreview() {
    val club = Club(
        id = 1,
        name = "Tech Club",
        description = "Explore cutting-edge technology, coding, and innovation",
        email = "tech@example.com",
        logo = null,
        cover = null,
        followersCount = 245,
        members = 200,
        eventNumber = 12,
        sessionsNumber = 8,
        postsNumber = 50,
        clubAdminName = "John Doe",
        status = ClubStatus.ACTIVE,
        isJoined = true
    )
    MaterialExpressiveTheme {
        ClubCard(club = club, onJoinLeave = {}, onView = {} )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(name = "Clubs Screen - Light", showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Clubs Screen - Dark",
    showBackground = true
)
@Composable
fun ClubsScreenPreview() {
    val sampleClubs = listOf(
        Club(
            id = 1,
            name = "Tech Club",
            description = "Explore cutting-edge technology, coding, and innovation",
            email = "tech@example.com",
            logo = null,
            cover = null,
            followersCount = 245,
            members = 200,
            eventNumber = 12,
            sessionsNumber = 8,
            postsNumber = 50,
            clubAdminName = "John Doe",
            status = ClubStatus.ACTIVE,
            isJoined = true
        ),
        Club(
            id = 2,
            name = "Design Club",
            description = "Design, UX, and product workshops",
            email = "design@example.com",
            logo = null,
            cover = null,
            followersCount = 180,
            members = 120,
            eventNumber = 6,
            sessionsNumber = 4,
            postsNumber = 20,
            clubAdminName = "Jane Smith",
            status = ClubStatus.ACTIVE,
            isJoined = false
        )
    )

    MaterialExpressiveTheme {
        ClubsScreenContent(
            clubsState = UiState.Success(sampleClubs),
            onNavigateToClubProfile = {},
            onToggleJoin = {}
        )
    }
}
