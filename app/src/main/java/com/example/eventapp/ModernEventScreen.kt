package com.example.eventapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import com.example.eventapp.Model.Event
import com.example.eventapp.Model.EventRequest
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernMainScreen(
    events: List<Event>,
    loading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onCreateEvent: (EventRequest) -> Unit,
    onUpdateEvent: (Int, EventRequest) -> Unit,
    onDeleteEvent: (Event) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<Event?>(null) }
    var search by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Semua") }

    val filteredEvents = remember(events, search, selectedStatus) {
        events
            .filter { event ->
                val matchSearch = search.isBlank() ||
                        event.title.contains(search, ignoreCase = true) ||
                        event.location.contains(search, ignoreCase = true)
                val matchStatus = selectedStatus == "Semua" ||
                        event.status.equals(selectedStatus, ignoreCase = true)
                matchSearch && matchStatus
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text("Event Management", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${events.size} event",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Tambah") },
                text = { Text("Event Baru") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // Search
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Cari event...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Filter status
            StatusFilterRow(
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it }
            )

            Spacer(Modifier.height(16.dp))

            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: $error")
                    }
                }

                filteredEvents.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada event yang cocok")
                    }
                }

                else -> {
                    EventListModern(
                        events = filteredEvents,
                        onEditEvent = { editingEvent = it },
                        onDeleteEvent = onDeleteEvent
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddEventDialogModern(
            onDismiss = { showAddDialog = false },
            onSave = { request ->
                onCreateEvent(request)
                showAddDialog = false
            }
        )
    }

    editingEvent?.let { event ->
        EditEventDialogModern(
            event = event,
            onDismiss = { editingEvent = null },
            onSave = { request ->
                event.id?.let { id -> onUpdateEvent(id, request) }
                editingEvent = null
            }
        )
    }
}

@Composable
private fun StatusFilterRow(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    val statusOptions = listOf("Semua", "upcoming", "ongoing", "completed", "cancelled")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statusOptions.forEach { status ->
            val label = if (status == "Semua") {
                "Semua"
            } else {
                status.replaceFirstChar { it.uppercase() }   // Upcoming / Ongoing / Completed / Cancelled
            }

            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusSelected(status) },
                modifier = Modifier
                    .weight(1f)          // ➜ semua chip dibagi rata lebarnya
                    .height(34.dp),
                label = {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            )
        }
    }
}


@Composable
fun EventListModern(
    events: List<Event>,
    onEditEvent: (Event) -> Unit,
    onDeleteEvent: (Event) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { event ->
            EventCardModern(
                event = event,
                onEditEvent = onEditEvent,
                onDeleteEvent = onDeleteEvent
            )
        }
    }
}

@Composable
fun EventCardModern(
    event: Event,
    onEditEvent: (Event) -> Unit,
    onDeleteEvent: (Event) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${event.date} • ${event.time}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusPill(status = event.status)
            }

            if (!event.description.isNullOrEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = event.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (event.id != null) {
                    TextButton(onClick = { onEditEvent(event) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Edit")
                    }
                    TextButton(onClick = { onDeleteEvent(event) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus"
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Hapus")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    val color = when (status.lowercase()) {
        "upcoming" -> MaterialTheme.colorScheme.primaryContainer
        "ongoing" -> MaterialTheme.colorScheme.tertiaryContainer
        "completed" -> MaterialTheme.colorScheme.secondaryContainer
        "cancelled" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(50),
        tonalElevation = 2.dp
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/* ======================== DIALOG TAMBAH & EDIT MODERN ======================== */

@Composable
fun AddEventDialogModern(
    onDismiss: () -> Unit,
    onSave: (EventRequest) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var capacityText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("upcoming") }

    fun openDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, y, m, d ->
                val mm = (m + 1).toString().padStart(2, '0')
                val dd = d.toString().padStart(2, '0')
                date = "$y-$mm-$dd"
            },
            year, month, day
        ).show()
    }

    fun openTimePicker() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, h, m ->
                val hh = h.toString().padStart(2, '0')
                val mm = m.toString().padStart(2, '0')
                time = "$hh:$mm"
            },
            hour, minute, true
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Event") },
        text = {
            EventFormContent(
                title = title,
                onTitleChange = { title = it },
                date = date,
                onDateClick = { openDatePicker() },
                time = time,
                onTimeClick = { openTimePicker() },
                location = location,
                onLocationChange = { location = it },
                description = description,
                onDescriptionChange = { description = it },
                capacityText = capacityText,
                onCapacityChange = { capacityText = it },
                status = status,
                onStatusChange = { status = it }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank() && date.isNotBlank() && time.isNotBlank()
                    && location.isNotBlank() && status.isNotBlank()
                ) {
                    val capacity = capacityText.toIntOrNull()
                    val request = EventRequest(
                        title = title,
                        date = date,
                        time = time,
                        location = location,
                        description = if (description.isBlank()) null else description,
                        capacity = capacity,
                        status = status
                    )
                    onSave(request)
                }
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun EditEventDialogModern(
    event: Event,
    onDismiss: () -> Unit,
    onSave: (EventRequest) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var title by remember { mutableStateOf(event.title) }
    var date by remember { mutableStateOf(event.date) }
    var time by remember { mutableStateOf(event.time) }
    var location by remember { mutableStateOf(event.location) }
    var description by remember { mutableStateOf(event.description ?: "") }
    var capacityText by remember { mutableStateOf(event.capacity?.toString() ?: "") }
    var status by remember { mutableStateOf(event.status) }

    fun openDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, y, m, d ->
                val mm = (m + 1).toString().padStart(2, '0')
                val dd = d.toString().padStart(2, '0')
                date = "$y-$mm-$dd"
            },
            year, month, day
        ).show()
    }

    fun openTimePicker() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, h, m ->
                val hh = h.toString().padStart(2, '0')
                val mm = m.toString().padStart(2, '0')
                time = "$hh:$mm"
            },
            hour, minute, true
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Event") },
        text = {
            EventFormContent(
                title = title,
                onTitleChange = { title = it },
                date = date,
                onDateClick = { openDatePicker() },
                time = time,
                onTimeClick = { openTimePicker() },
                location = location,
                onLocationChange = { location = it },
                description = description,
                onDescriptionChange = { description = it },
                capacityText = capacityText,
                onCapacityChange = { capacityText = it },
                status = status,
                onStatusChange = { status = it }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank() && date.isNotBlank() && time.isNotBlank()
                    && location.isNotBlank() && status.isNotBlank()
                ) {
                    val capacity = capacityText.toIntOrNull()
                    val request = EventRequest(
                        title = title,
                        date = date,
                        time = time,
                        location = location,
                        description = if (description.isBlank()) null else description,
                        capacity = capacity,
                        status = status
                    )
                    onSave(request)
                }
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun EventFormContent(
    title: String,
    onTitleChange: (String) -> Unit,
    date: String,
    onDateClick: () -> Unit,
    time: String,
    onTimeClick: () -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    capacityText: String,
    onCapacityChange: (String) -> Unit,
    status: String,
    onStatusChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Judul") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text("Tanggal (YYYY-MM-DD)") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDateClick() },
            readOnly = true
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = time,
            onValueChange = {},
            label = { Text("Jam (HH:MM)") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTimeClick() },
            readOnly = true
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Lokasi") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Deskripsi (opsional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = capacityText,
            onValueChange = onCapacityChange,
            label = { Text("Kapasitas (opsional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = status,
            onValueChange = onStatusChange,
            label = { Text("Status (upcoming/ongoing/completed/cancelled)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
