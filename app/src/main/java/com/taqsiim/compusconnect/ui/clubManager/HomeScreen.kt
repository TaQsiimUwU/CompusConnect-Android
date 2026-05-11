package com.taqsiim.compusconnect.ui.clubManager

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.mvi.UiState
import com.taqsiim.compusconnect.ui.clubManager.home.ManagerHomeEffect
import com.taqsiim.compusconnect.ui.clubManager.home.ManagerHomeIntent
import com.taqsiim.compusconnect.ui.clubManager.home.ManagerHomeViewModel
import com.taqsiim.compusconnect.ui.components.CreatePostDialog
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ManagerHomeScreen(
    viewModel: ManagerHomeViewModel,
    onScheduleEvent: () -> Unit,
    onScheduleSession: () -> Unit,
    onOpenPostDetail: (String) -> Unit
) {
    val managerState by viewModel.state.collectAsState()
    val postsState = managerState.posts
    val isRefreshing = managerState.isRefreshing
    val pullRefreshState = rememberPullToRefreshState()

    var isFabExpanded by remember { mutableStateOf(false) }
    val fabRotation by animateFloatAsState(targetValue = if (isFabExpanded) 45f else 0f, label = "fabRotation")
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }
    var showEditDialogForPost by remember { mutableStateOf<Post?>(null) }
    var editedPostContent by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ManagerHomeEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is ManagerHomeEffect.PostCreated -> { /* handled by ShowSnackbar */ }
            }
        }
    }

    if (showCreatePostDialog) {
        CreatePostDialog(
            onDismissRequest = { showCreatePostDialog = false },
            onPublish = { content, imageUri, linkType, _ ->
                viewModel.processIntent(
                    ManagerHomeIntent.CreatePost(
                        content = content,
                        eventId = null, // TODO: link to event via linkType + event selector
                        imageUrl = imageUri
                    )
                )
                showCreatePostDialog = false
            }
        )
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { postId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete this post? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.processIntent(ManagerHomeIntent.DeletePost(postId))
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
            }
        )
    }

    showEditDialogForPost?.let { post ->
        AlertDialog(
            onDismissRequest = {
                showEditDialogForPost = null
                editedPostContent = ""
            },
            title = { Text("Edit Post") },
            text = {
                OutlinedTextField(
                    value = editedPostContent,
                    onValueChange = { editedPostContent = it },
                    placeholder = { Text("Update post content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.processIntent(
                            ManagerHomeIntent.UpdatePost(
                                postId = post.postId,
                                content = editedPostContent
                            )
                        )
                        showEditDialogForPost = null
                        editedPostContent = ""
                    },
                    enabled = editedPostContent.isNotBlank() && editedPostContent.trim() != post.content
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialogForPost = null
                        editedPostContent = ""
                    }
                ) {
                    Text("Cancel")
                }
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.processIntent(ManagerHomeIntent.Refresh) },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                when (val state = postsState) {
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
                    items(state.data) { post ->
                        AnnouncementCard(
                            post = post,
                            onOpenPost = {
                                onOpenPostDetail(post.postId.toString())
                            },
                            onEdit = {
                                showEditDialogForPost = post
                                editedPostContent = post.content
                            },
                            onLike = {
                                if (post.isLiked) {
                                    viewModel.processIntent(ManagerHomeIntent.UnlikePost(post.postId))
                                } else {
                                    viewModel.processIntent(ManagerHomeIntent.LikePost(post.postId))
                                    }
                                },
                                onComment = {
                                    onOpenPostDetail(post.postId.toString())
                                },
                                onDelete = { showDeleteDialog = post.postId }
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
fun AnnouncementCard(
    post: Post,
    onOpenPost: () -> Unit = {},
    onEdit: () -> Unit = {},
    onLike: () -> Unit = {},
    onComment: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onOpenPost
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
                    // Club Logo
                    if (!post.clubLogoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = post.clubLogoUrl,
                            contentDescription = "${post.clubName.orEmpty()} logo",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (post.clubName ?: "C").take(1),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Column {
                        Text(
                            text = post.clubName ?: "Club ${post.clubId}",
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
                            text = "Linked to Event #${post.eventId}",
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
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable(onClick = onLike)
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
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
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable(onClick = onComment)
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
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
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
    CampusAppTheme(userRole = UserRole.CLUB_MANAGER) {
        ManagerHomeScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onScheduleEvent = {},
            onScheduleSession = {},
            onOpenPostDetail = {}
        )
    }
}
