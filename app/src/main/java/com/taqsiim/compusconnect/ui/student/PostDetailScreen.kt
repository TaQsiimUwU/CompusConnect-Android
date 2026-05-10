package com.taqsiim.compusconnect.ui.student

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.Comment
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.viewmodel.StudentViewModel
import com.taqsiim.compusconnect.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: StudentViewModel,
    shouldFocusComment: Boolean = false
) {

    val postsState by viewModel.postsState.collectAsState()
    val commentsState by viewModel.postCommentsState.collectAsState()
    var commentText by remember { mutableStateOf("") }
    var isSubmittingComment by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        postId.toIntOrNull()?.let { viewModel.loadPostComments(it) }
    }

    val post: Post? = when (val state = postsState) {
        is com.taqsiim.compusconnect.viewmodel.UiState.Success -> state.data.firstOrNull { it.postId.toString() == postId }
        else -> null
    }

    Scaffold(
        bottomBar = {
            // Comment input box
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank() && post != null && !isSubmittingComment) {
                                isSubmittingComment = true
                                viewModel.addComment(post.postId, commentText)
                                commentText = ""
                                isSubmittingComment = false
                            }
                        },
                        enabled = commentText.isNotBlank() && !isSubmittingComment
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send comment",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                if (post == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Loading post...", style = MaterialTheme.typography.bodyMedium)
                    }
                    return@item
                }
            }

            item {
                post?.let {
                    Text(text = "Club ${it.clubId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = it.createdAt, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = it.content, style = MaterialTheme.typography.bodyLarge)

                    if (!it.imageUrl.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AsyncImage(
                            model = it.imageUrl,
                            contentDescription = "Post image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Text(text = "${it.likeCount} likes", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "${it.commentCount} comments", style = MaterialTheme.typography.bodyMedium)
                    }

                    if (it.eventId != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { onNavigateToEventDetail(it.eventId.toString()) }) {
                            Text(text = "View Event Details")
                        }
                    }
                }
            }

            item {
                HorizontalDivider()
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
            }
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

//@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "PostDetail - Light")
//@androidx.compose.ui.tooling.preview.Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "PostDetail - Dark")
//@Composable
//fun PostDetailPreview() {
//    CampusAppTheme {
//        PostDetailScreen(
//            postId = "1",
//            onNavigateToEventDetail = {},
//            onNavigateBack = {},
//            viewModel = androidx.lifecycle.viewmodel.compose.viewModel()
//        )
//    }
//}
