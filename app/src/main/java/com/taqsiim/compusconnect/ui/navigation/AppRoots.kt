package com.taqsiim.compusconnect.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.taqsiim.compusconnect.data.model.ReserveFacilityRequest
import com.taqsiim.compusconnect.data.model.ReserveRoomRequest
import com.taqsiim.compusconnect.data.model.UserRole
import com.taqsiim.compusconnect.ui.auth.AuthViewModel
import com.taqsiim.compusconnect.ui.clubManager.AttendeesScreen
import com.taqsiim.compusconnect.ui.clubManager.ClubAccountScreen
import com.taqsiim.compusconnect.ui.clubManager.ManagerHomeScreen
import com.taqsiim.compusconnect.ui.clubManager.RequestsScreen
import com.taqsiim.compusconnect.ui.clubManager.ScheduleEventScreen
import com.taqsiim.compusconnect.ui.clubManager.attendees.AttendeesViewModel
import com.taqsiim.compusconnect.ui.clubManager.account.ClubAccountIntent
import com.taqsiim.compusconnect.ui.clubManager.account.ClubAccountViewModel
import com.taqsiim.compusconnect.ui.clubManager.home.ManagerHomeViewModel
import com.taqsiim.compusconnect.ui.clubManager.requests.RequestsViewModel
import com.taqsiim.compusconnect.ui.clubManager.schedule.ScheduleEventViewModel
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
import com.taqsiim.compusconnect.ui.student.clubs.ClubsViewModel
import com.taqsiim.compusconnect.ui.student.events.EventsViewModel
import com.taqsiim.compusconnect.ui.student.facilities.FacilityBookingEffect
import com.taqsiim.compusconnect.ui.student.facilities.FacilityBookingIntent
import com.taqsiim.compusconnect.ui.student.facilities.FacilityBookingViewModel
import com.taqsiim.compusconnect.ui.student.home.HomeViewModel
import com.taqsiim.compusconnect.ui.student.rooms.RoomBookingEffect
import com.taqsiim.compusconnect.ui.student.rooms.RoomBookingIntent
import com.taqsiim.compusconnect.ui.student.rooms.RoomBookingViewModel

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
                 currentRoute != "student/post/{postId}" &&
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
                    isScrolling = { isScrolling ->
                        isBottomBarVisible = !isScrolling
                    },
                    scrollToTop = scrollToTopHome,
                    onScrollToTopComplete = { scrollToTopHome = false }
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
                val viewModel: RoomBookingViewModel = hiltViewModel()
                val roomBookingState by viewModel.state.collectAsState()
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.state.collectAsState()
                
                val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
                
                LaunchedEffect(viewModel.effect) {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is RoomBookingEffect.BookingSuccess -> {
                                kotlinx.coroutines.delay(1000)
                                navController.popBackStack()
                            }
                            is RoomBookingEffect.ShowSnackbar -> {
                                snackbarHostState.showSnackbar(effect.message)
                            }
                        }
                    }
                }
                
                BookRoomForm(
                    onNavigateBack = { navController.popBackStack() },
                    isSubmitting = roomBookingState.isSubmitting,
                    snackbarHostState = snackbarHostState,
                    onSubmit = { date, startTime, endTime, purpose, _ ->
                        val userId = authState.currentUser?.userId ?: 0
                        val startIso = if (startTime.contains("T")) startTime else "${date}T${startTime}:00Z"
                        val endIso = if (endTime.contains("T")) endTime else "${date}T${endTime}:00Z"
                        val request = ReserveRoomRequest(
                            startTime = startIso,
                            endTime = endIso,
                            purpose = purpose,
                            stdIds = if (userId > 0) listOf(userId) else listOf()
                        )
                        viewModel.processIntent(RoomBookingIntent.ReserveRoom(request))
                    }
                )
            }
            composable("student/reserve_sport") {
                val viewModel: FacilityBookingViewModel = hiltViewModel()
                val facilityState by viewModel.state.collectAsState()
                val authState by authViewModel.state.collectAsState()

                val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

                LaunchedEffect(viewModel.effect) {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is FacilityBookingEffect.BookingSuccess -> {
                                kotlinx.coroutines.delay(1000)
                                navController.popBackStack()
                            }
                            is FacilityBookingEffect.ShowSnackbar -> {
                                snackbarHostState.showSnackbar(effect.message)
                            }
                        }
                    }
                }

                ReserveSport(
                    onNavigateBack = { navController.popBackStack() },
                    facilities = (facilityState.facilities as? com.taqsiim.compusconnect.mvi.UiState.Success)?.data
                        ?: emptyList(),
                    isSubmitting = facilityState.isSubmitting,
                    snackbarHostState = snackbarHostState,
                    onSubmit = { facilityId, startIso, endIso ->
                        val userId = authState.currentUser?.userId ?: 0
                        val request = ReserveFacilityRequest(
                            facilityId = facilityId,
                            startTime = startIso,
                            endTime = endIso,
                            teamIds = if (userId > 0) listOf(userId) else listOf()
                        )
                        viewModel.processIntent(
                            FacilityBookingIntent.ReserveFacility(
                                facilityId = facilityId,
                                request = request
                            )
                        )
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
                    onEventClick = { eventId -> navController.navigate("student/event/$eventId") },
                    onNavigateBack = { navController.popBackStack() }
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
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEventDetail = { eventId -> navController.navigate("student/event/$eventId") },
                    onNavigateToPostDetail = { postId -> navController.navigate("student/post/$postId") }
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
    val clubAccountViewModel: ClubAccountViewModel = hiltViewModel()
    val attendeesViewModel: AttendeesViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        clubAccountViewModel.processIntent(ClubAccountIntent.LoadClubInfo)
    }

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
                    onOpenPostDetail = { postId -> navController.navigate("manager/post/$postId") },
                    onNavigateToEventDetail = { eventId -> navController.navigate("manager/event/$eventId") }
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
                    onEventClick = { eventId -> navController.navigate("manager/event/$eventId") },
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
                AttendeesScreen(viewModel = attendeesViewModel)
            }
            composable("manager/account") {
                ClubAccountScreen(
                    canSwitchRole = canSwitchRole,
                    onSwitchToStudent = onSwitchRole,
                    onLogout = onLogout,
                    clubAccountViewModel = clubAccountViewModel
                )
            }
        }
    }
}

// TODO: Add preview composables if needed
