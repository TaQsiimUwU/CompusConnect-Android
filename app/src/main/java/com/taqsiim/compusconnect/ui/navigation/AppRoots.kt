package com.taqsiim.compusconnect.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taqsiim.compusconnect.ui.student.BookRoomForm
import com.taqsiim.compusconnect.ui.student.ClubsScreen
import com.taqsiim.compusconnect.ui.student.EventsScreen
import com.taqsiim.compusconnect.ui.student.HomeScreen
import com.taqsiim.compusconnect.ui.student.ProfileScreen
import com.taqsiim.compusconnect.ui.clubManager.ManagerHomeScreen
import com.taqsiim.compusconnect.ui.clubManager.RequestsScreen
import com.taqsiim.compusconnect.viewmodel.ManagerViewModel
import com.taqsiim.compusconnect.viewmodel.StudentViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taqsiim.compusconnect.ui.theme.UserRole
import com.taqsiim.compusconnect.ui.clubManager.ClubAccountScreen
import com.taqsiim.compusconnect.ui.student.ReportIssueScreen
import com.taqsiim.compusconnect.ui.student.ReserveSport
import com.taqsiim.compusconnect.ui.student.ReservationsScreen
import com.taqsiim.compusconnect.ui.student.NotificationsScreen
import com.taqsiim.compusconnect.ui.student.EventDetailScreen
import com.taqsiim.compusconnect.ui.student.ClubProfileScreen

@Composable
fun StudentAppRoot(onSwitchRole: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "student/home"
    var isBottomBarVisible by remember { mutableStateOf(true) }
    var scrollToTopHome by remember { mutableStateOf(false) }

    // Create dependencies manually for now (Dependency Injection like Hilt is recommended for larger apps)
    Scaffold(
        bottomBar = {
            val shouldShowBottomBar = currentRoute != "student/book_room" &&
                 currentRoute != "student/report_issue" &&
                 currentRoute != "student/reserve_sport" &&
                 currentRoute != "student/reservations" &&
                 currentRoute != "student/notifications" &&
                 !currentRoute.startsWith("student/event/") &&
                 !currentRoute.startsWith("student/club/")

            AnimatedVisibility(
                visible = shouldShowBottomBar && isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                DynamicNavBar(
                    userRole = UserRole.Student,
                    selectedRoute = currentRoute,
                    onNavigate = { route ->
                        if (route == currentRoute && route == "student/home") {
                            scrollToTopHome = true
                        } else {
                            navController.navigate(route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo("student/home") {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "student/home",

            ) {
            composable("student/home") {
                val viewModel: StudentViewModel = viewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToEventDetail = { eventId -> navController.navigate("student/event/$eventId") },
                    onNavigateToReservations = { navController.navigate("student/reservations") },
                    onNavigateToRoomForm = { navController.navigate("student/book_room") },
                    onNavigateToSportForm = { navController.navigate("student/reserve_sport") },
                    onNavigateToReportIssue = { navController.navigate("student/report_issue") },
                    onNavigateToNotifications = { navController.navigate("student/notifications") },
                    isScrolling = { isScrolling ->
                        isBottomBarVisible = !isScrolling
                    },
                    scrollToTop = scrollToTopHome,
                    onScrollToTopComplete = { scrollToTopHome = false }
                )
            }
            composable("student/notifications") {
                NotificationsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("student/reservations") {
                ReservationsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("student/report_issue") {
                ReportIssueScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("student/book_room") {
                BookRoomForm(
                    onNavigateBack = { navController.popBackStack() },
                    onSubmit = {
                        // TODO: Handle submission
                        navController.popBackStack()
                    }
                )
            }
            composable("student/reserve_sport") {
                ReserveSport(
                    onNavigateBack = { navController.popBackStack() },
                    onSubmit = {
                        // TODO: Handle submission
                        navController.popBackStack()
                    }
                )
            }
            composable("student/events") {
                val viewModel: StudentViewModel = viewModel()
                EventsScreen(
                    viewModel = viewModel,
                    onNavigateToEventDetail = { eventId -> navController.navigate("student/event/$eventId") },
                    isScrolling = { isScrolling ->
                        isBottomBarVisible = !isScrolling
                    }
                )
            }
            composable(
                route = "student/event/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                EventDetailScreen(
                    eventId = eventId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("student/clubs") {
                val viewModel: StudentViewModel = viewModel()
                ClubsScreen(
                    viewModel = viewModel,
                    onNavigateToClubProfile = { clubId -> navController.navigate("student/club/$clubId") },
                    isScrolling = { isScrolling ->
                        isBottomBarVisible = !isScrolling
                    }
                )
            }
            composable(
                route = "student/club/{clubId}",
                arguments = listOf(navArgument("clubId") { type = NavType.StringType })
            ) { backStackEntry ->
                val clubId = backStackEntry.arguments?.getString("clubId") ?: ""
                ClubProfileScreen(
                    clubId = clubId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("student/profile") {
                ProfileScreen(
                    onSwitchToManager = onSwitchRole,
                    onLogout = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun ManagerAppRoot(onSwitchRole: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "manager/home"

    // Create dependencies manually for now
    Scaffold(
        bottomBar = {
            DynamicNavBar(
                userRole = UserRole.ClubManager,
                selectedRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("manager/home") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "manager/home",
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = 0.dp
            )
        ) {
            composable("manager/home") {
                val viewModel: ManagerViewModel = viewModel()
                ManagerHomeScreen(
                    viewModel = viewModel,
                    onCreatePost = { /* TODO */ },
                    onScheduleEvent = { /* TODO */ },
                    onScheduleSession = { /* TODO */ }
                )
            }
            composable("manager/requests") {
                val viewModel: ManagerViewModel = viewModel()
                RequestsScreen(
                    viewModel = viewModel
                )
            }
            composable("manager/attendees") {
                // TODO: Attendees Screen
            }
            composable("manager/account") {
                ClubAccountScreen(
                    onSwitchToStudent = onSwitchRole,
                    onLogout = { /* TODO */ }
                )
            }
        }
    }
}

// TODO: Add preview composables if needed
