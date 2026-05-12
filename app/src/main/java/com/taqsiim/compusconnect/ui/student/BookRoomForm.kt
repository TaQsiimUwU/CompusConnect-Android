package com.taqsiim.compusconnect.ui.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRoomForm(
    onNavigateBack: () -> Unit,
    onSubmit: (date: String, startTime: String, endTime: String, purpose: String, numberOfPeople: String) -> Unit
) {
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var numberOfPeople by remember { mutableStateOf("1") }
    var purpose by remember { mutableStateOf("Studying") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val purposeOptions = listOf("Studying", "Meeting", "Project", "Presentation")

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
        RoomTimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = { hour, minute ->
                startTime = String.format(Locale.US, "%02d:%02d", hour, minute)
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        RoomTimePickerDialog(
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
                            text = "Study Rooms",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Find and reserve your study space",
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
            // How it works
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "How it works",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HowItWorksItem("Submit your reservation request")
                    HowItWorksItem("System automatically assigns best available room")
                    HowItWorksItem("Confirmation sent via email and notification")
                    HowItWorksItem("Room details will include amenities and location")
                }
            }
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

                    // Time Inputs
                    OutlinedTextField(
                        value = date,
                        onValueChange = { },
                        label = { Text("Date") },
                        placeholder = { Text("yyyy-mm-dd") },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        },
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { },
                            label = { Text("Start Time") },
                            placeholder = { Text("HH:MM") },
                            leadingIcon = {
                                Icon(Icons.Default.AccessTime, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showStartTimePicker = true }) {
                                    Icon(Icons.Default.AccessTime, contentDescription = "Select Start Time")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showStartTimePicker = true },
                            readOnly = true,
                            enabled = false
                        )

                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { },
                            label = { Text("End Time") },
                            placeholder = { Text("HH:MM") },
                            leadingIcon = {
                                Icon(Icons.Default.AccessTime, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showEndTimePicker = true }) {
                                    Icon(Icons.Default.AccessTime, contentDescription = "Select End Time")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showEndTimePicker = true },
                            readOnly = true,
                            enabled = false
                        )
                    }

                    // Number of People
                    OutlinedTextField(
                        value = numberOfPeople,
                        onValueChange = { numberOfPeople = it },
                        label = { Text("Number of People") },
                        leadingIcon = {
                            Icon(Icons.Default.Group, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = { Text("Maximum 8 people per room") }
                    )

                    // Purpose Radio Buttons
                    Column {
                        Text(
                            text = "Purpose",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        purposeOptions.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { purpose = option }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = (purpose == option),
                                    onClick = { purpose = option }
                                )
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { onSubmit(date, startTime, endTime, purpose, numberOfPeople) },
                        enabled = date.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reserve Room")
                    }
                }
            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomTimePickerDialog(
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

@Composable
private fun HowItWorksItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "• ",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

