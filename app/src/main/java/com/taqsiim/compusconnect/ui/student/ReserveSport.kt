@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.taqsiim.compusconnect.ui.student

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.taqsiim.compusconnect.data.model.Facility
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReserveSport(
    onNavigateBack: () -> Unit,
    facilities: List<Facility>,
    isSubmitting: Boolean,
    onSubmit: (facilityId: Int, startTime: String, endTime: String, teamIds: List<Int>) -> Unit
) {
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var teamIdsInput by remember { mutableStateOf("") }
    var selectedFacility by remember { mutableStateOf<Facility?>(null) }
    var isFacilityDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val parsedTeamIds = teamIdsInput.split(",")
        .mapNotNull { raw ->
            raw.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()?.takeIf { it >= 0 }
        }
    val isFormValid = selectedFacility != null &&
        date.isNotBlank() &&
        startTime.isNotBlank() &&
        endTime.isNotBlank()

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        date = sdf.format(Date(millis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showStartTimePicker) {
        SportTimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = { hour, minute ->
                startTime = String.format(Locale.US, "%02d:%02d", hour, minute)
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        SportTimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onConfirm = { hour, minute ->
                endTime = String.format(Locale.US, "%02d:%02d", hour, minute)
                showEndTimePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Sports Facilities",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Reserve a sports facility",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Form Fields
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Facility Selection
                    ExposedDropdownMenuBox(
                        expanded = isFacilityDropdownExpanded,
                        onExpandedChange = { isFacilityDropdownExpanded = !isFacilityDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedFacility?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Facility") },
                            placeholder = { Text("Select a facility") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFacilityDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            enabled = !isSubmitting
                        )
                        ExposedDropdownMenu(
                            expanded = isFacilityDropdownExpanded,
                            onDismissRequest = { isFacilityDropdownExpanded = false }
                        ) {
                            facilities.forEach { facility ->
                                DropdownMenuItem(
                                    text = { Text(facility.name) },
                                    onClick = {
                                        selectedFacility = facility
                                        isFacilityDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = date,
                        onValueChange = { },
                        label = { Text("Date") },
                        placeholder = { Text("yyyy-mm-dd") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        readOnly = true,
                        enabled = false
                    )



                    // Time Inputs

                        // --- Start Time Field ---

                            OutlinedTextField(
                                value = startTime,
                                onValueChange = { },
                                label = { Text("Start Time") },
                                placeholder = { Text("HH:MM") },
                                leadingIcon = {
                                    Icon(Icons.Default.AccessTime, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true, // Prevents keyboard from popping up
                                enabled = false
                            )


                        // --- End Time Field ---

                            OutlinedTextField(
                                value = endTime,
                                onValueChange = { },
                                label = { Text("End Time") },
                                placeholder = { Text("HH:MM") },
                                leadingIcon = {
                                    Icon(Icons.Default.AccessTime, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true
                            )



                    // Team IDs
                    OutlinedTextField(
                        value = teamIdsInput,
                        onValueChange = { teamIdsInput = it },
                        label = { Text("Team IDs") },
                        leadingIcon = {
                            Icon(Icons.Default.Group, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = { Text("Optional: enter IDs separated by commas (e.g. 0,7,10)") },
                        isError = false
                    )

                    Button(
                        onClick = {
                            selectedFacility?.let {
                                val startIso = "${date}T${startTime}:00Z"
                                val endIso = "${date}T${endTime}:00Z"
                                onSubmit(it.facilityId, startIso, endIso, parsedTeamIds)
                            }
                        },
                        enabled = !isSubmitting && isFormValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reserve Facility")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SportTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select time",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                TimePicker(state = timePickerState)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        onConfirm(timePickerState.hour, timePickerState.minute)
                    }) { Text("OK") }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ReserveSportPreview() {
    MaterialExpressiveTheme {
        ReserveSport(
            onNavigateBack = {},
            facilities = emptyList(),
            isSubmitting = false,
            onSubmit = { _, _, _, _ -> }
        )
    }
}
