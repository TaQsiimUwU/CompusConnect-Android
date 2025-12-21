package com.taqsiim.compusconnect.ui.clubManager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.ui.components.CreatePostDialog
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import android.content.res.Configuration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.taqsiim.compusconnect.ui.theme.UserRole
import com.taqsiim.compusconnect.viewmodel.ManagerViewModel
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem

// TODO: Implement ManagerHomeScreen composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ManagerHomeScreen(
    viewModel: ManagerViewModel,
    onCreatePost: () -> Unit,
    onScheduleEvent: () -> Unit,
    onScheduleSession: () -> Unit
) {
    // Mock Data
    val posts = remember {
        listOf(
            Post(
                postId = 1,
                clubId = 1,
                eventId = 101,
                content = "Excited to announce our AI workshop! Registration is now open. Don't miss this opportunity to learn from industry experts! 🚀",
                imageUrl = null,
                createdAt = "2 hours ago",
                likeCount = 45,
                commentCount = 12,
                isLiked = false
            ),
            Post(
                postId = 2,
                clubId = 1,
                eventId = null,
                content = "Thank you to everyone who attended last week's coding session! Your participation and enthusiasm made it a huge success. See you at the next one! 💻",
                imageUrl = null,
                createdAt = "1 day ago",
                likeCount = 32,
                commentCount = 5,
                isLiked = true
            )
        )
    }

    var isFabExpanded by remember { mutableStateOf(false) }
    val fabRotation by animateFloatAsState(targetValue = if (isFabExpanded) 45f else 0f, label = "fabRotation")
    var showCreatePostDialog by remember { mutableStateOf(false) }

    if (showCreatePostDialog) {
        CreatePostDialog(
            onDismissRequest = { showCreatePostDialog = false },
            onPublish = { content, imageUri, linkType, shareToSocials ->
                // TODO: Handle post creation
                showCreatePostDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent Announcements") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButtonMenu(
                expanded = isFabExpanded,
                button = {
                    FloatingActionButton(
                        onClick = { isFabExpanded = !isFabExpanded },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Expand Menu",
                            modifier = Modifier.rotate(fabRotation)
                        )
                    }
                },
                modifier = Modifier.padding(bottom = 80.dp),
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButtonMenuItem(
                    onClick = {
                        isFabExpanded = false
                        onScheduleEvent()
                    },
                    icon = { Icon(Icons.Default.Event, contentDescription = null) },
                    text = { Text("Schedule Event/Session") }
                )
                FloatingActionButtonMenuItem(
                    onClick = {
                        isFabExpanded = false
                        showCreatePostDialog = true
                    },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    text = { Text("Create Post") }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
        ) {
            items(posts) { post ->
                AnnouncementCard(post = post)
            }
        }
    }
}

// TODO: Implement AnnouncementCard composable
@Composable
fun AnnouncementCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color(0xFFE91E63) // Mock Club Color
                    ) {
                        // Logo
                    }
                    Column {
                        Text(
                            text = "Tech Club", // Mock Club Name
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = post.createdAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = if (post.eventId != null) Color(0xFFE3F2FD) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (post.eventId != null) "event" else "general",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (post.eventId != null) Color(0xFF1976D2) else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium
            )

            if (post.eventId != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE3F2FD),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBDEFB))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFF1565C0)
                        )
                        Text(
                            text = "Linked to: AI & Machine Learning Workshop", // Mock Event Title
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1565C0),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = post.likeCount.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = post.commentCount.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { /* TODO */ }) {
                    Text("View Comments", color = MaterialTheme.colorScheme.onSurface)
                }
                Row {
                    TextButton(onClick = { /* TODO */ }) {
                        Text("Edit", color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { /* TODO */ }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
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
fun ManagerHomeScreenPreview() {
    CampusAppTheme(userRole = UserRole.ClubManager) {
        ManagerHomeScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onCreatePost = {},
            onScheduleEvent = {},
            onScheduleSession = {}
        )
    }
}
