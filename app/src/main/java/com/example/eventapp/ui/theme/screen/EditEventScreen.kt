package com.example.eventapp.ui.theme.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eventapp.Model.Event
import com.example.eventapp.ui.theme.viewmodel.EventViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController: NavController,
    eventId: Int,
    viewModel: EventViewModel
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    var loaded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var capacityText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("upcoming") }

    var titleError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }
    var timeError by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }

    val statusOptions = listOf("upcoming", "ongoing", "completed", "cancelled")
    var expanded by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                date = String.format(
                    Locale.getDefault(),
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    dayOfMonth
                )
                dateError = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                time = String.format(
                    Locale.getDefault(),
                    "%02d:%02d:00",
                    hourOfDay,
                    minute
                )
                timeError = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    LaunchedEffect(eventId) {
        if (!loaded) {
            viewModel.getEventById(eventId) { event ->
                event?.let {
                    title = it.title
                    date = it.date
                    time = it.time
                    location = it.location
                    description = it.description ?: ""
                    capacityText = it.capacity?.toString() ?: ""
                    status = it.status
                }
                loaded = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Event", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false },
                label = { Text("Judul Event *") },
                leadingIcon = { Icon(Icons.Default.Event, null) },
                isError = titleError,
                supportingText = if (titleError) ({ Text("Judul wajib diisi") }) else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = date,
                onValueChange = {},
                label = { Text("Tanggal *") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.EditCalendar, contentDescription = "Pilih Tanggal")
                    }
                },
                isError = dateError,
                supportingText = if (dateError) {
                    ({ Text("Tanggal wajib diisi") })
                } else if (date.isNotEmpty()) {
                    ({ Text("Format: ${formatDateDisplay(date)}") })
                } else null,
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = time,
                onValueChange = {},
                label = { Text("Waktu *") },
                leadingIcon = { Icon(Icons.Default.Schedule, null) },
                trailingIcon = {
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Pilih Waktu")
                    }
                },
                isError = timeError,
                supportingText = if (timeError) {
                    ({ Text("Waktu wajib diisi") })
                } else if (time.isNotEmpty()) {
                    ({ Text("Format: ${formatTimeDisplay(time)}") })
                } else null,
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it; locationError = false },
                label = { Text("Lokasi *") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                isError = locationError,
                supportingText = if (locationError) ({ Text("Lokasi wajib diisi") }) else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi (opsional)") },
                leadingIcon = { Icon(Icons.Default.Description, null) },
                supportingText = { Text("${description.length}/500 karakter") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = capacityText,
                onValueChange = {
                    if (it.isEmpty() || it.all { ch -> ch.isDigit() }) capacityText = it
                },
                label = { Text("Kapasitas Peserta (opsional)") },
                leadingIcon = { Icon(Icons.Default.People, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = getStatusLabel(status),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status Event *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(getStatusLabel(option)) },
                            onClick = {
                                status = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    titleError = title.isBlank()
                    dateError = date.isBlank()
                    timeError = time.isBlank()
                    locationError = location.isBlank()

                    if (titleError || dateError || timeError || locationError) {
                        Toast.makeText(context, "⚠️ Mohon lengkapi semua field yang wajib", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val capacity = capacityText.toIntOrNull()
                    val updatedEvent = Event(
                        id = eventId,
                        title = title.trim(),
                        date = date.trim(),
                        time = time.trim(),
                        location = location.trim(),
                        description = if (description.isBlank()) null else description.trim(),
                        capacity = capacity,
                        status = status
                    )

                    viewModel.updateEvent(eventId, updatedEvent) { success, msg ->
                        if (success) {
                            Toast.makeText(context, "✅ Event berhasil diupdate!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "❌ Gagal: $msg", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                }
                Icon(Icons.Default.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text(if (isLoading) "Menyimpan..." else "Update Event")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Hapus Event?", fontWeight = FontWeight.Bold) },
            text = { Text("Event \"$title\" akan dihapus permanen.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteEvent(eventId) { success, msg ->
                            if (success) {
                                Toast.makeText(context, "✅ Event berhasil dihapus", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "❌ Gagal menghapus: $msg", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}

/** Helpers dibuat PRIVATE biar gak bentrok file lain (conflicting overloads hilang) */
private fun getStatusLabel(status: String): String = when (status) {
    "upcoming" -> "Akan Datang"
    "ongoing" -> "Berlangsung"
    "completed" -> "Selesai"
    "cancelled" -> "Dibatalkan"
    else -> status
}

private fun formatDateDisplay(date: String): String {
    return try {
        val parts = date.split("-")
        if (parts.size == 3) {
            val months = listOf(
                "", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
            )
            "${parts[2]} ${months[parts[1].toInt()]} ${parts[0]}"
        } else date
    } catch (_: Exception) {
        date
    }
}

private fun formatTimeDisplay(time: String): String {
    return try {
        val parts = time.split(":")
        if (parts.size >= 2) "${parts[0]}:${parts[1]} WIB" else time
    } catch (_: Exception) {
        time
    }
}
