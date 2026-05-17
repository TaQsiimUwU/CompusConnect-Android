package com.taqsiim.compusconnect.ui.student

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PostAdd
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.mvi.UiState
import com.taqsiim.compusconnect.ui.student.clubs.ClubDetailEffect
import com.taqsiim.compusconnect.ui.student.clubs.ClubDetailIntent
import com.taqsiim.compusconnect.ui.student.clubs.ClubDetailViewModel
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubProfileScreen(
    clubId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEventDetail: (String) -> Unit = {}
) {
    val viewModel: ClubDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val club = (state.club as? UiState.Success)?.data
    val snackbarHostState = remember { SnackbarHostState() }

    val clubIdInt = clubId.toIntOrNull()
    LaunchedEffect(clubIdInt) {
        clubIdInt?.let {
            viewModel.processIntent(ClubDetailIntent.LoadClub(it))
            viewModel.processIntent(ClubDetailIntent.LoadClubPosts(it))
            viewModel.processIntent(ClubDetailIntent.LoadClubEvents(it))
        }
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ClubDetailEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Posts", "Sessions", "Events")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // No TopAppBar — back button floats over the hero image
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {

            // ── Hero + Info Header ───────────────────────────────────────
            item {
                Column {
                    // ── Hero image with floating back button ────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        // Cover image (or gradient placeholder)
                        if (!club?.cover.isNullOrEmpty()) {
                            AsyncImage(
                                model = club!!.cover,
                                contentDescription = "${club.name} cover",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF1A237E),
                                                Color(0xFF4B5EFC),
                                                Color(0xFF7C4DFF)
                                            )
                                        )
                                    )
                            )
                        }

                        // Dark gradient scrim at the bottom for logo overlap readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colorStops = arrayOf(
                                            0.0f to Color.Transparent,
                                            0.6f to Color.Transparent,
                                            1.0f to Color.Black.copy(alpha = 0.55f)
                                        )
                                    )
                                )
                        )

                        // Floating back button (top-left, respects status bar)
                        Box(
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(12.dp)
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                                .align(Alignment.TopStart),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = onNavigateBack, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Logo — anchored bottom-start, half-overlapping the hero edge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 20.dp)
                                .offset(y = 4.dp)
                                .size(80.dp)
                                .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            if (!club?.logo.isNullOrEmpty()) {
                                AsyncImage(
                                    model = club!!.logo,
                                    contentDescription = "${club.name} logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Initials fallback
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF4B5EFC), Color(0xFF7C4DFF))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = club?.name?.take(2)?.uppercase() ?: "?",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // ── Club info card (sits below hero, has space for logo overlap) ──
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
                        ) {
                            // Club name
                            Text(
                                text = club?.name ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Stats row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClubStatChip(
                                    icon = Icons.Outlined.Group,
                                    label = "${club?.followersCount ?: 0} members"
                                )
                                if ((club?.eventNumber ?: 0) > 0) {
                                    ClubStatChip(
                                        icon = Icons.Outlined.CalendarMonth,
                                        label = "${club?.eventNumber} events"
                                    )
                                }
                                if ((club?.postsNumber ?: 0) > 0) {
                                    ClubStatChip(
                                        icon = Icons.Outlined.PostAdd,
                                        label = "${club?.postsNumber} posts"
                                    )
                                }
                            }

                            if (!club?.description.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = club!!.description!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                                )
                            }
                        }
                    }
                }
            }

            // ── Tabs ────────────────────────────────────────────────────
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }

            // ── Tab Content ─────────────────────────────────────────────
            when (selectedTab) {

                // Posts tab
                0 -> {
                    when (val postsState = state.posts) {
                        is UiState.Loading -> item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                        is UiState.Error -> item {
                            Text(
                                text = postsState.message,
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        is UiState.Success -> {
                            if (postsState.data.isEmpty()) {
                                item {
                                    EmptyTabPlaceholder("No posts yet")
                                }
                            } else {
                                items(postsState.data) { post ->
                                    ClubPostCard(post = post)
                                }
                            }
                        }
                        is UiState.Idle -> item { EmptyTabPlaceholder("No posts available") }
                    }
                }

                // Sessions tab
                1 -> {
                    item { EmptyTabPlaceholder("No sessions available") }
                }

                // Events tab
                2 -> {
                    when (val eventsState = state.events) {
                        is UiState.Loading -> item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                        is UiState.Error -> item {
                            Text(
                                text = eventsState.message,
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        is UiState.Success -> {
                            if (eventsState.data.isEmpty()) {
                                item { EmptyTabPlaceholder("No events for this club yet") }
                            } else {
                                items(eventsState.data) { event ->
                                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                        EventCard(
                                            event = event,
                                            onRegister = {
                                                viewModel.processIntent(
                                                    ClubDetailIntent.RegisterForEvent(event.eventId)
                                                )
                                            },
                                            onUnregister = {
                                                viewModel.processIntent(
                                                    ClubDetailIntent.UnregisterFromEvent(event.eventId)
                                                )
                                            },
                                            onViewDetails = {
                                                onNavigateToEventDetail(event.eventId.toString())
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        is UiState.Idle -> item { EmptyTabPlaceholder("No events available") }
                    }
                }
            }
        }
    }
}

// ── Shared helpers ───────────────────────────────────────────────────────────

@Composable
private fun ClubStatChip(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyTabPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ── Post card ────────────────────────────────────────────────────────────────

@Composable
fun ClubPostCard(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.PostAdd,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Club Post",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = post.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (post.imageUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reactions row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = post.likeCount.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = post.commentCount.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@androidx.compose.ui.tooling.preview.Preview(name = "Light Mode", showBackground = true)
@androidx.compose.ui.tooling.preview.Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode",
    showBackground = true
)
@Composable
fun ClubProfileScreenPreview() {
    MaterialExpressiveTheme {
        ClubProfileScreen(clubId = "1", onNavigateBack = {})
    }
}
