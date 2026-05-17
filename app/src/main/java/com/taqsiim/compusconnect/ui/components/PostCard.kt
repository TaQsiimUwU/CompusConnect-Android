@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.taqsiim.compusconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.Post
import androidx.compose.ui.tooling.preview.Preview
import com.taqsiim.compusconnect.data.model.Club

/**
 * Post Card for Latest Updates
 * Features edge-to-edge layout with an integrated, toggleable bottom divider.
 */
@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit,
    onViewDetails: (Post) -> Unit,
    onEventClick: (Int) -> Unit = {},
    onCommentClick: () -> Unit = {},
    showDivider: Boolean = true,
    managerActions: (@Composable () -> Unit)? = null
) {
    // Outer column handles the background, click, and edge-to-edge elements
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onViewDetails(post) }
    ) {
        // Inner column handles the padded content
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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

            // Post Content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

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


            // Post Image
            if (!post.imageUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Like button
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

                // Comment button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable(onClick = onCommentClick)
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
        }

        // Manager-only Edit/Delete actions (optional)
        managerActions?.invoke()

        // Edge-to-edge Horizontal Divider
        if (showDivider && managerActions == null) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "Post Card - Light")
@Composable
fun PostCardPreviewLight() {
    MaterialExpressiveTheme {
        PostCard(
            post = Post(
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
            onLike = {},
            onViewDetails = { _ -> },
            onEventClick = {},
            onCommentClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Post Card - Dark",
)
@Composable
fun PostCardPreviewDark() {
    MaterialExpressiveTheme {
        PostCard(
            post = Post(
                postId = 2,
                clubId = 102,
                eventId = 201,
                content = "Hackathon this weekend! Don't miss out on the prizes.",
                imageUrl = null,
                createdAt = "2023-10-05T14:30:00Z",
                likeCount = 128,
                commentCount = 23,
                isLiked = true
            ),
            onLike = {},
            onViewDetails = { _ -> },
            onEventClick = {},
            onCommentClick = {},
            showDivider = false // Previewing without the divider
        )
    }
}