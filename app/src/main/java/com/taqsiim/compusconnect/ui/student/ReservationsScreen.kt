package com.taqsiim.compusconnect.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taqsiim.compusconnect.data.model.Reservation
import com.taqsiim.compusconnect.data.model.ReservationType
import com.taqsiim.compusconnect.ui.components.ReservationCard
import com.taqsiim.compusconnect.ui.theme.CampusAppTheme
import com.taqsiim.compusconnect.ui.student.reservations.ReservationsViewModel
import com.taqsiim.compusconnect.ui.student.reservations.ReservationsIntent
import com.taqsiim.compusconnect.mvi.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReservationsViewModel = hiltViewModel()
) {
    val filters = ReservationFilter.entries.toTypedArray()
    val pagerState = rememberPagerState(pageCount = { filters.size })
    val coroutineScope = rememberCoroutineScope()
    val reservationsScreenState by viewModel.state.collectAsState()
    val reservationsState = reservationsScreenState.reservations

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Reservations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp
            ) {
                filters.forEachIndexed { index, filter ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = filter.name.replace("_", " ").lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reservations List Pager
            when (val state = reservationsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is UiState.Success -> {
                    val allReservations = state.data
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val currentFilter = filters[page]
                        val filteredReservations = when (currentFilter) {
                            ReservationFilter.ALL -> allReservations
                            ReservationFilter.EVENT -> allReservations.filter { it.type == ReservationType.EVENT }
                            ReservationFilter.SPORT -> allReservations.filter { it.type == ReservationType.SPORT }
                            ReservationFilter.STUDY_ROOM -> allReservations.filter { it.type == ReservationType.STUDY_ROOM }
                            ReservationFilter.SESSION -> allReservations.filter { it.type == ReservationType.SESSION }
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredReservations) { reservation ->
                                ReservationCard(
                                    reservation = reservation,
                                    modifier = Modifier.fillMaxWidth(),
                                    onCancelClick = {
                                        viewModel.processIntent(ReservationsIntent.CancelReservation(reservation.reservationId))
                                    }
                                )
                            }

                            if (filteredReservations.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No reservations found",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is UiState.Idle -> { }
            }
        }
    }
}

enum class ReservationFilter {
    ALL, EVENT, SPORT, STUDY_ROOM, SESSION
}

@Preview(showBackground = true)
@Composable
fun ReservationsScreenPreview() {
    CampusAppTheme {
        ReservationsScreen(onNavigateBack = {})
    }
}
