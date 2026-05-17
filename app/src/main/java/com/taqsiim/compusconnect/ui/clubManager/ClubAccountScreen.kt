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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.taqsiim.compusconnect.data.model.UpdateClubRequest
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.mvi.UiState
import com.taqsiim.compusconnect.ui.auth.AuthViewModel
import com.taqsiim.compusconnect.ui.clubManager.account.ClubAccountEffect
import com.taqsiim.compusconnect.ui.clubManager.account.ClubAccountIntent
import com.taqsiim.compusconnect.ui.clubManager.account.ClubAccountViewModel
import com.taqsiim.compusconnect.ui.components.AccountActionsSection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    val snackbarHostState = remember { SnackbarHostState() }
    var showEditSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Handle one-shot effects
    LaunchedEffect(clubAccountViewModel.effect) {
        clubAccountViewModel.effect.collect { effect ->
            when (effect) {
                is ClubAccountEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is ClubAccountEffect.UpdateSuccess -> {
                    scope.launch { sheetState.hide() }
                    showEditSheet = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                is UiState.Success -> {
                    val club = state.data
                    ClubHeaderCard(club = club, onEditClick = { showEditSheet = true })
                    AboutClubCard(description = club.description ?: "No description available.")
                }
                is UiState.Error -> {
                    val fallback = authState.currentUser?.let { "${it.firstName} ${it.lastName}" } ?: "Club Manager"
                    ClubHeaderCard(club = null, userName = fallback, onEditClick = null)
                    AboutClubCard(description = "Unable to load club information.")
                }
                is UiState.Idle -> {
                    val fallback = authState.currentUser?.let { "${it.firstName} ${it.lastName}" } ?: "Club Manager"
                    ClubHeaderCard(club = null, userName = fallback, onEditClick = null)
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // ── Edit Bottom Sheet (name + description only) ──────────────────────────
    if (showEditSheet) {
        val currentClub = (clubState.club as? UiState.Success)?.data

        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            sheetState = sheetState
        ) {
            EditClubForm(
                currentClub = currentClub,
                isUpdating = clubState.isUpdating,
                onSubmit = { name, description ->
                    val clubId = currentClub?.id ?: return@EditClubForm
                    // Keep existing logo/cover unchanged; only update name & description
                    clubAccountViewModel.processIntent(
                        ClubAccountIntent.UpdateClub(
                            clubId = clubId,
                            request = UpdateClubRequest(
                                name = name,
                                description = description,
                                logo = currentClub.logo ?: "",
                                cover = currentClub.cover ?: ""
                            )
                        )
                    )
                },
                onDismiss = { showEditSheet = false }
            )
        }
    }
}

// ── Edit form ────────────────────────────────────────────────────────────────

@Composable
private fun EditClubForm(
    currentClub: Club?,
    isUpdating: Boolean,
    onSubmit: (name: String, description: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentClub?.name ?: "") }
    var description by remember { mutableStateOf(currentClub?.description ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    CircleShape
                )
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Edit Club Info",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Club Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Cancel")
            }

            Button(
                onClick = { onSubmit(name, description) },
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank() && !isUpdating
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        }
    }
}

// ── Club header card ─────────────────────────────────────────────────────────

@Composable
fun ClubHeaderCard(
    club: Club? = null,
    userName: String = "",
    onEditClick: (() -> Unit)? = null
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Logo
                    if (!club?.logo.isNullOrEmpty()) {
                        AsyncImage(
                            model = club?.logo,
                            contentDescription = "${club?.name} logo",
                            modifier = Modifier.size(64.dp).clip(CircleShape),
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

                    Column(modifier = Modifier.weight(1f)) {
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

                    // Edit pencil button
                    if (onEditClick != null) {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit club info",
                                tint = MaterialTheme.colorScheme.onPrimary
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

// ── Helpers ──────────────────────────────────────────────────────────────────

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

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun ClubAccountScreenPreview() {
    MaterialExpressiveTheme {
        ClubAccountScreen(onSwitchToStudent = {}, onLogout = {})
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ClubAccountScreenDarkPreview() {
    MaterialExpressiveTheme {
        ClubAccountScreen(onSwitchToStudent = {}, onLogout = {})
    }
}
