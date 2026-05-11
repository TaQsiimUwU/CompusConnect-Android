package com.taqsiim.compusconnect.ui.student

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.taqsiim.compusconnect.data.model.User
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.ui.components.AccountActionsSection
import com.taqsiim.compusconnect.ui.components.ActionItem
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import com.taqsiim.compusconnect.ui.auth.AuthViewModel
import com.taqsiim.compusconnect.ui.auth.AuthIntent
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    canSwitchRole: Boolean = false,
    onSwitchToManager: () -> Unit,
    onLogout: () -> Unit
) {
    val authState by viewModel.state.collectAsState()
    val currentUser = authState.currentUser
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            viewModel.processIntent(AuthIntent.RefreshUser)
        }
    }
    ProfileScreenContent(
        user = currentUser,
        canSwitchRole = canSwitchRole,
        onSwitchToManager = onSwitchToManager,
        onLogout = onLogout
    )
}

@Composable
private fun ProfileScreenContent(
    user: User?,
    canSwitchRole: Boolean,
    onSwitchToManager: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold() { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileHeaderCard(user = user)
            QRCodeCard(userId = user?.userId?.toString() ?: "")
            AccountActionsSection(
                userRole = UserRole.STUDENT,
                canSwitchRole = canSwitchRole,
                onSwitch = onSwitchToManager,
                onLogout = onLogout
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileHeaderCard(user: User?) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = user?.pictureUrl,
                                contentDescription = user?.firstName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "${user?.firstName ?: ""} ${user?.lastName ?: ""}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "${user?.faculty ?: "Unknown"} • ${user?.level ?: "Student"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "ID: ${user?.userId ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                ContactInfoRow(icon = Icons.Default.Email, text = user?.email ?: "")
                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

@Composable
fun ContactInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
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
fun QRCodeCard(userId: String) {
    val normalizedUserId = userId.ifBlank { "CS2023-1234" }
    val qrBitmap = remember(normalizedUserId) {
        generateQrCodeBitmap(
            data = "student_id:$normalizedUserId",
            size = 512
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My QR Code",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "Student QR Code",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "QR unavailable",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Student ID: $normalizedUserId",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Show this code for attendance and facility access",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun generateQrCodeBitmap(data: String, size: Int): Bitmap? {
    return runCatching {
        val bitMatrix = QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
        Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
                }
            }
        }
    }.getOrNull()
}

@Composable
fun ActionsCard(
    onSwitchToManager: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            ActionItem(
                icon = Icons.Default.Refresh,
                title = "Switch to Club Manager",
                subtitle = "Manage your club activities",
                onClick = onSwitchToManager
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            ActionItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Log Out",
                titleColor = MaterialTheme.colorScheme.error,
                iconTint = MaterialTheme.colorScheme.error,
                onClick = onLogout
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val mockUser = User(
        userId = 1234,
        role = UserRole.STUDENT,
        userName = "noura.ahmed",
        email = "noura.ahmed@university.edu",
        firstName = "Noura",
        lastName = "Ahmed",
        faculty = "Engineering",
        major = "Computer Science",
        level = "3",
        phone = "+20 10 1234 5678"
    )

    CampusAppTheme {
        ProfileScreenContent(
            user = mockUser,
            canSwitchRole = false,
            onSwitchToManager = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true , uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenDarkPreview() {
    val mockUser = User(
        userId = 1234,
        role = UserRole.STUDENT,
        userName = "noura.ahmed",
        email = "noura.ahmed@university.edu",
        firstName = "Noura",
        lastName = "Ahmed",
        faculty = "Engineering",
        major = "Computer Science",
        level = "3",
        phone = "+20 10 1234 5678"
    )

    CampusAppTheme(darkTheme = true) {
        ProfileScreenContent(
            user = mockUser,
            canSwitchRole = false,
            onSwitchToManager = {},
            onLogout = {}
        )
    }
}
