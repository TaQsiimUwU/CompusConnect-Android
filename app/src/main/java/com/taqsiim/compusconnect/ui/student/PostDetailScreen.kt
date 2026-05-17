package com.taqsiim.compusconnect.ui.student

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.Comment
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.ui.student.posts.PostDetailViewModel
import com.taqsiim.compusconnect.ui.student.posts.PostDetailIntent
import com.taqsiim.compusconnect.ui.student.posts.PostDetailEffect
import com.taqsiim.compusconnect.mvi.UiState
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onEventClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel(),

    ) {

    val detailState by viewModel.state.collectAsState()
    val postState = detailState.post
    val commentsState = detailState.comments
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        postId.toIntOrNull()?.let { id ->
            viewModel.processIntent(PostDetailIntent.LoadPost(id))
            viewModel.processIntent(PostDetailIntent.LoadComments(id))
        }
    }

    // Collect one-shot effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PostDetailEffect.CommentAdded -> { /* comment submitted successfully */ }
                is PostDetailEffect.ShowSnackbar -> { /* TODO: show snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // Comment input box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(), // <--- Add this modifier right here
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        placeholder = { Text("Add a comment...") },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                val pid = postId.toIntOrNull()
                                if (commentText.isNotBlank() && pid != null && !detailState.isSubmitting) {
                                    viewModel.processIntent(PostDetailIntent.AddComment(pid, commentText))
                                    commentText = ""
                                }
                            }
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    IconButton(
                        onClick = {
                            val pid = postId.toIntOrNull()
                            if (commentText.isNotBlank() && pid != null && !detailState.isSubmitting) {
                                viewModel.processIntent(PostDetailIntent.AddComment(pid, commentText))
                                commentText = ""
                            }
                        },
                        enabled = commentText.isNotBlank() && !detailState.isSubmitting
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send comment",
                            tint = if (commentText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when (postState) {
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = postState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            postId.toIntOrNull()?.let { viewModel.processIntent(PostDetailIntent.LoadPost(it)) }
                        }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is UiState.Success -> {
                val post = postState.data
                PostDetailContent(
                    post = post,
                    commentsState = commentsState,
                    paddingValues = paddingValues,
                    onEventClick = onEventClick,
                    onLike = {
                        if (post.isLiked) {
                            viewModel.processIntent(PostDetailIntent.UnlikePost(post.postId))
                        } else {
                            viewModel.processIntent(PostDetailIntent.LikePost(post.postId))
                        }
                    }
                )
            }
            is UiState.Idle -> { }
        }
    }
}

@Composable
private fun PostDetailContent(
    post: Post,
    commentsState: UiState<List<Comment>>,
    paddingValues: PaddingValues,
    onEventClick: (Int) -> Unit,
    onLike: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Post content
        item {
            // Club Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = post.clubName ?: "Club ${post.clubId}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = post.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = post.content, style = MaterialTheme.typography.bodyLarge)

            // Event button
            if (post.eventId != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onEventClick(post.eventId) }),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.inversePrimary,
                    border = androidx.compose.foundation.BorderStroke(1.dp,MaterialTheme.colorScheme.primary )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Linked to Event #${post.eventId}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (!post.imageUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Like and comment row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.noRippleClickable { onLike() }
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(text = "${post.likeCount} likes", style = MaterialTheme.typography.bodyMedium)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(text = "${post.commentCount} comments", style = MaterialTheme.typography.bodyMedium)
                }
            }


        }

        item {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Comments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        when (val state = commentsState) {
            is UiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    item {
                        Text(
                            text = "No comments yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(state.data) { comment ->
                        CommentItem(comment = comment)
                    }
                }
            }

            is UiState.Idle -> { }
        }
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (comment.studentImageUrl.isNotEmpty()) {
            AsyncImage(
                model = comment.studentImageUrl,
                contentDescription = comment.studentName,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = comment.studentName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = comment.content, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = comment.createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Modifier extension for clickable without ripple effect (for like button).
 */
@Composable
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier {
    return this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            onClick = onClick
        )
    )
}

@Preview(showBackground = true, name = "PostDetail - Light")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, name = "PostDetail - Dark")
@Composable
private fun PostDetailPreview() {
    val samplePost = Post(
        postId = 1,
        clubId = 12,
        eventId = 1,
        content = "This is a sample post used for previews. It demonstrates content, optional images and actions.",
        imageUrl = null,
        createdAt = "2026-05-16",
        likeCount = 42,
        commentCount = 2,
        isLiked = false
    )

    val sampleComments = listOf(
        Comment(
            studentName = "Alice",
            studentImageUrl = "",
            content = "Nice post!",
            createdAt = "2026-05-15"
        ),
        Comment(
            studentName = "Bob",
            studentImageUrl = "",
            content = "Looking forward to this.",
            createdAt = "2026-05-15"
        )
    )

    PostDetailContent(
        post = samplePost,
        commentsState = UiState.Success(sampleComments),
        paddingValues = PaddingValues(0.dp),
        onEventClick = {},
        onLike = {}
    )
}
