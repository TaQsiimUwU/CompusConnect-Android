package com.taqsiim.compusconnect.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import com.taqsiim.compusconnect.ui.theme.UserRole

/**
 * Dynamic Navigation Bar that changes content based on user role
 *
 * @param userRole Current user role (Student or ClubManager)
 * @param selectedRoute Currently selected route
 * @param onNavigate Callback for navigation with route
 * @param modifier Optional modifier
 */
private data class NavBarItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun DynamicNavBar(
    userRole: UserRole,
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animate visibility when switching roles
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(100)) + slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300)
        ),
        exit = fadeOut(animationSpec = tween(100)) + slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(100)
        )
    ) {
        when (userRole) {
            UserRole.Student -> StudentNavBar(
                selectedRoute = selectedRoute,
                onNavigate = onNavigate,
                modifier = modifier
            )
            UserRole.ClubManager -> ManagerNavBar(
                selectedRoute = selectedRoute,
                onNavigate = onNavigate,
                modifier = modifier
            )
        }
    }
}

/**
 * Student Navigation Bar
 * Contains: Home, Events, Clubs, Profile
 */
@Composable
private fun StudentNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val studentNavItems = listOf(
        NavBarItem(
            route = "student/home",
            icon = Icons.Default.Home,
            label = "Home"
        ),
        NavBarItem(
            route = "student/events",
            icon = Icons.Default.Event,
            label = "Events"
        ),
        NavBarItem(
            route = "student/clubs",
            icon = Icons.Default.Group,
            label = "Clubs"
        ),
        NavBarItem(
            route = "student/profile",
            icon = Icons.Default.Person,
            label = "Profile"
        )
    )

    NavBarContent(
        items = studentNavItems,
        selectedRoute = selectedRoute,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

/**
 * Club Manager Navigation Bar
 * Contains: Home, Requests, Attendees, Account
 */
@Composable
private fun ManagerNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val managerNavItems = listOf(
        NavBarItem(
            route = "manager/home",
            icon = Icons.Default.Home,
            label = "Home"
        ),
        NavBarItem(
            route = "manager/requests",
            icon = Icons.Default.Assignment,
            label = "Requests"
        ),
        NavBarItem(
            route = "manager/attendees",
            icon = Icons.Default.Group,
            label = "Attendees"
        ),
        NavBarItem(
            route = "manager/account",
            icon = Icons.Default.AccountCircle,
            label = "Account"
        )
    )

    NavBarContent(
        items = managerNavItems,
        selectedRoute = selectedRoute,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

/**
 * Reusable Navigation Bar Content
 * Updated with Floating + Translucent styling
 */
@Composable
private fun NavBarContent(
    items: List<NavBarItem>,
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .padding(start = 28.dp, end = 28.dp, bottom = 16.dp, top = 8.dp)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(32.dp)),

        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        items.forEach { item ->
            val isSelected = selectedRoute == item.route || selectedRoute.startsWith(item.route)

            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = {
                    Text(
                        text = item.label,
                        fontSize = if (isSelected) 14.sp else 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Student Navigation Bar - Light")
@Composable
fun StudentNavBarPreviewLight() {
    CampusAppTheme(userRole = UserRole.Student, darkTheme = false) {
        DynamicNavBar(
            userRole = UserRole.Student,
            selectedRoute = "student/home",
            onNavigate = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Student Navigation Bar - Dark")
@Composable
fun StudentNavBarPreviewDark() {
    CampusAppTheme(userRole = UserRole.Student, darkTheme = true) {
        DynamicNavBar(
            userRole = UserRole.Student,
            selectedRoute = "student/home",
            onNavigate = {}
        )
    }
}

@Preview(showBackground = true, name = "Manager Navigation Bar - Light")
@Composable
fun ManagerNavBarPreviewLight() {
    CampusAppTheme(userRole = UserRole.ClubManager, darkTheme = false) {
        DynamicNavBar(
            userRole = UserRole.ClubManager,
            selectedRoute = "manager/home",
            onNavigate = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Manager Navigation Bar - Dark")
@Composable
fun ManagerNavBarPreviewDark() {
    CampusAppTheme(userRole = UserRole.ClubManager, darkTheme = true) {
        DynamicNavBar(
            userRole = UserRole.ClubManager,
            selectedRoute = "manager/home",
            onNavigate = {}
        )
    }
}