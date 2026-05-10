package com.taqsiim.compusconnect.ui.student

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.Post
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.taqsiim.compusconnect.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: StudentViewModel
) {

    val postsState by viewModel.postsState.collectAsState()

    val post: Post? = when (val state = postsState) {
        is com.taqsiim.compusconnect.viewmodel.UiState.Success -> state.data.firstOrNull { it.postId.toString() == postId }
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Post") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            if (post == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Loading post...", style = MaterialTheme.typography.bodyMedium)
                }
                return@Column
            }

            Text(text = "Club ${post.clubId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.createdAt, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = post.content, style = MaterialTheme.typography.bodyLarge)

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

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Text(text = "${post.likeCount} likes", style = MaterialTheme.typography.bodyMedium)
                Text(text = "${post.commentCount} comments", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (post.eventId != null) {
                Button(onClick = { onNavigateToEventDetail(post.eventId.toString()) }) {
                    Text(text = "View Event Details")
                }
            }
        }
    }
}
//
//@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "PostDetail - Light")
//@androidx.compose.ui.tooling.preview.Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "PostDetail - Dark")
//@Composable
//fun PostDetailPreview() {
//    CampusAppTheme {
//        PostDetailScreen(postId = "1", onNavigateToEventDetail = {}, onNavigateBack = {})
//    }
//}
