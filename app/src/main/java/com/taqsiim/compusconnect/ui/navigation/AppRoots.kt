package com.taqsiim.compusconnect.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.ui.clubManager.AttendeesScreen
import com.taqsiim.compusconnect.ui.clubManager.ClubAccountScreen
import com.taqsiim.compusconnect.ui.clubManager.ManagerHomeScreen
import com.taqsiim.compusconnect.ui.clubManager.RequestsScreen
import com.taqsiim.compusconnect.ui.clubManager.ScheduleEventScreen
import com.taqsiim.compusconnect.ui.student.BookRoomForm
import com.taqsiim.compusconnect.ui.student.ClubProfileScreen
import com.taqsiim.compusconnect.ui.student.ClubsScreen
import com.taqsiim.compusconnect.ui.student.EventDetailScreen
import com.taqsiim.compusconnect.ui.student.EventsScreen
import com.taqsiim.compusconnect.ui.student.HomeScreen
import com.taqsiim.compusconnect.ui.student.NotificationsScreen
import com.taqsiim.compusconnect.ui.student.PostDetailScreen
import com.taqsiim.compusconnect.ui.student.ProfileScreen
import com.taqsiim.compusconnect.ui.student.ReportIssueScreen
import com.taqsiim.compusconnect.ui.student.ReservationsScreen
import com.taqsiim.compusconnect.ui.student.ReserveSport
import com.taqsiim.compusconnect.ui.auth.AuthViewModel
import com.taqsiim.compusconnect.ui.student.home.HomeViewModel
import com.taqsiim.compusconnect.ui.student.events.EventsViewModel
import com.taqsiim.compusconnect.ui.student.events.EventDetailViewModel
import com.taqsiim.compusconnect.ui.student.clubs.ClubsViewModel
import com.taqsiim.compusconnect.ui.student.clubs.ClubDetailViewModel
import com.taqsiim.compusconnect.ui.student.posts.PostDetailViewModel
import com.taqsiim.compusconnect.ui.student.reservations.ReservationsViewModel
import com.taqsiim.compusconnect.ui.clubManager.home.ManagerHomeViewModel
import com.taqsiim.compusconnect.ui.clubManager.home.ManagerHomeIntent
import com.taqsiim.compusconnect.ui.clubManager.requests.RequestsViewModel
import com.taqsiim.compusconnect.ui.clubManager.attendees.AttendeesViewModel
import com.taqsiim.compusconnect.ui.clubManager.schedule.ScheduleEventViewModel
import com.taqsiim.compusconnect.mvi.UiState as MviUiState

@Composable
fun StudentAppRoot(
    canSwitchRole: Boolean,
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "student/home"
    var isBottomBarVisible by remember { mutableStateOf(true) }
    var scrollToTopHome by remember { mutableStateOf(false) }

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
                    userRole = UserRole.STUDENT,
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
            // modifier = Modifier.padding(innerPadding)
            ) {
            composable("student/home") {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToEventDetail = { eventId -> navController.navigate("student/event/$eventId") },
                    onNavigateToPostDetail = { postId -> navController.navigate("student/post/$postId") },
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
                val viewModel: EventsViewModel = hiltViewModel()
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
            composable(
                route = "student/post/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                PostDetailScreen(
                    postId = postId,
                    onNavigateToEventDetail = { eventId -> navController.navigate("student/event/$eventId") },
                    onNavigateBack = { navController.popBackStack()}
                )
            }
            composable("student/clubs") {
                val viewModel: ClubsViewModel = hiltViewModel()
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
                    viewModel = authViewModel,
                    canSwitchRole = canSwitchRole,
                    onSwitchToManager = onSwitchRole,
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun ManagerAppRoot(
    canSwitchRole: Boolean,
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "manager/home"

    // Create dependencies manually for now
    Scaffold(
        bottomBar = {
            if (!currentRoute.startsWith("manager/schedule_event") &&
                !currentRoute.startsWith("manager/post/") &&
                !currentRoute.startsWith("manager/event/")
            ) {
                DynamicNavBar(
                    userRole = UserRole.CLUB_MANAGER,
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "manager/home",
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = if (currentRoute.startsWith("manager/schedule_event") ||
                    currentRoute.startsWith("manager/post/") ||
                    currentRoute.startsWith("manager/event/")
                ) 0.dp else innerPadding.calculateBottomPadding()
            )
        ) {
            composable("manager/home") {
                val viewModel: ManagerHomeViewModel = hiltViewModel()
                ManagerHomeScreen(
                    viewModel = viewModel,
                    onScheduleEvent = { navController.navigate("manager/schedule_event?type=event") },
                    onScheduleSession = { navController.navigate("manager/schedule_event?type=session") },
                    onOpenPostDetail = { postId -> navController.navigate("manager/post/$postId") }
                )
            }
            composable(
                route = "manager/schedule_event?type={type}",
                arguments = listOf(
                    navArgument("type") {
                        type = NavType.StringType
                        defaultValue = "event"
                    }
                )
            ) { backStackEntry ->
                val scheduleType = backStackEntry.arguments?.getString("type") ?: "event"
                val scheduleViewModel: ScheduleEventViewModel = hiltViewModel()
                ScheduleEventScreen(
                    viewModel = scheduleViewModel,
                    initialType = scheduleType,
                    onBackClick = { navController.popBackStack() },
                    onEventCreated = {
                        navController.popBackStack()
                    }
                )
            }
            composable("manager/requests") {
                val viewModel: RequestsViewModel = hiltViewModel()
                RequestsScreen(
                    viewModel = viewModel
                )
            }
            composable(
                route = "manager/post/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                PostDetailScreen(
                    postId = postId,
                    onNavigateToEventDetail = { eventId -> navController.navigate("manager/event/$eventId") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "manager/event/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                EventDetailScreen(
                    eventId = eventId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("manager/attendees") {
                // Get the ManagerHomeViewModel's events to pass to attendees
                val managerHomeVM: ManagerHomeViewModel = hiltViewModel()
                val homeState by managerHomeVM.state.collectAsState()
                val events = when (val s = homeState.events) {
                    is com.taqsiim.compusconnect.mvi.UiState.Success -> s.data
                    else -> emptyList()
                }
                AttendeesScreen(
                    onScanQrCode = { /* TODO: Implement QR Scanner */ },
                    events = events
                )
            }
            composable("manager/account") {
                ClubAccountScreen(
                    canSwitchRole = canSwitchRole,
                    onSwitchToStudent = onSwitchRole,
                    onLogout = onLogout
                )
            }
        }
    }
}

// TODO: Add preview composables if needed
