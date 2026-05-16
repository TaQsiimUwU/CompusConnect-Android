@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.taqsiim.compusconnect.ui.clubManager

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.Club
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.mvi.UiState
import com.taqsiim.compusconnect.ui.clubManager.account.ClubAccountViewModel
import com.taqsiim.compusconnect.ui.components.AccountActionsSection
import com.taqsiim.compusconnect.ui.auth.AuthViewModel

@Composable
fun ClubAccountScreen(
    canSwitchRole: Boolean = false,
    onSwitchToStudent: () -> Unit,
    onLogout: () -> Unit,
    clubAccountViewModel: ClubAccountViewModel = hiltViewModel()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsState()
    val clubState by clubAccountViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        when (val state = clubState.club) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                val club = state.data
                ClubHeaderCard(club = club)
                AboutClubCard(description = club.description ?: "No description available.")
            }
            is UiState.Error -> {
                // Fallback to user info
                ClubHeaderCard(club = null, userName = authState.currentUser?.let { "${it.firstName} ${it.lastName}" } ?: "Club Manager")
                AboutClubCard(description = "Unable to load club information.")
            }
            is UiState.Idle -> {
                ClubHeaderCard(club = null, userName = authState.currentUser?.let { "${it.firstName} ${it.lastName}" } ?: "Club Manager")
                AboutClubCard(description = "")
            }
        }

        AccountActionsSection(
            userRole = UserRole.CLUB_MANAGER,
            canSwitchRole = canSwitchRole,
            onSwitch = onSwitchToStudent,
            onLogout = onLogout
        )
    }
}

@Composable
fun ClubHeaderCard(club: Club? = null, userName: String = "") {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!club?.logo.isNullOrEmpty()) {
                        AsyncImage(
                            model = club?.logo,
                            contentDescription = "${club?.name} logo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (club?.name ?: userName).take(1).uppercase(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = club?.name ?: userName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        if (club != null) {
                            Text(
                                text = "${club.followersCount} Followers · ${club.members} Members",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "${club.eventNumber} Events · ${club.sessionsNumber} Sessions",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                if (club != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    if (club.email.isNotEmpty()) {
                        ClubContactRow(icon = Icons.Default.Email, text = club.email)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (club.clubAdminName.isNotEmpty()) {
                        ClubContactRow(icon = Icons.Default.Person, text = "Manager: ${club.clubAdminName}")
                    }
                }
            }
        }
    }
}

@Composable
fun ClubContactRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun AboutClubCard(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "About the Club",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClubAccountScreenPreview() {
    MaterialExpressiveTheme {
        ClubAccountScreen(
            onSwitchToStudent = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ClubAccountScreenDarkPreview() {
    MaterialExpressiveTheme {
        ClubAccountScreen(
            onSwitchToStudent = {},
            onLogout = {}
        )
    }
}
