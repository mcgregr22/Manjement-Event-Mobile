package com.example.eventapp.ui.theme.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun AddEventScreen(
    navController: NavController,
    viewModel: EventViewModel
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

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

    // Dialogs (dibuat di dalam composable -> aman)
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
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
                time = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, minute)
                timeError = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    val bgBrush = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgBrush)
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text("Tambah Event Baru", fontWeight = FontWeight.ExtraBold)
                            Text(
                                "Isi data event dengan lengkap",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgBrush)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Field bertanda * wajib diisi untuk menyimpan event.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            ElevatedCard(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; titleError = false },
                        label = { Text("Judul Event *") },
                        leadingIcon = { Icon(Icons.Default.Event, null) },
                        isError = titleError,
                        supportingText = if (titleError) { { Text("Judul wajib diisi") } } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        label = { Text("Tanggal *") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.EditCalendar, contentDescription = "Pilih tanggal")
                            }
                        },
                        isError = dateError,
                        supportingText = if (dateError) {
                            { Text("Tanggal wajib diisi") }
                        } else if (date.isNotEmpty()) {
                            { Text("Dipilih: ${add_formatDateDisplay(date)}", color = MaterialTheme.colorScheme.primary) }
                        } else null,
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = time,
                        onValueChange = {},
                        label = { Text("Waktu *") },
                        leadingIcon = { Icon(Icons.Default.Schedule, null) },
                        trailingIcon = {
                            IconButton(onClick = { timePickerDialog.show() }) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Pilih waktu")
                            }
                        },
                        isError = timeError,
                        supportingText = if (timeError) {
                            { Text("Waktu wajib diisi") }
                        } else if (time.isNotEmpty()) {
                            { Text("Dipilih: ${add_formatTimeDisplay(time)}", color = MaterialTheme.colorScheme.primary) }
                        } else null,
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it; locationError = false },
                        label = { Text("Lokasi *") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        isError = locationError,
                        supportingText = if (locationError) { { Text("Lokasi wajib diisi") } } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= 500) description = it },
                        label = { Text("Deskripsi (opsional)") },
                        leadingIcon = { Icon(Icons.Default.Description, null) },
                        supportingText = { Text("${description.length}/500 karakter") },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
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
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = add_getStatusLabel(status),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status Event *") },
                            leadingIcon = {
                                Icon(
                                    when (status) {
                                        "upcoming" -> Icons.Default.Schedule
                                        "ongoing" -> Icons.Default.PlayArrow
                                        "completed" -> Icons.Default.CheckCircle
                                        "cancelled" -> Icons.Default.Cancel
                                        else -> Icons.Default.Event
                                    },
                                    null
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(add_getStatusLabel(option)) },
                                    onClick = {
                                        status = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    titleError = title.isBlank()
                    dateError = date.isBlank()
                    timeError = time.isBlank()
                    locationError = location.isBlank()

                    if (titleError || dateError || timeError || locationError) {
                        Toast.makeText(context, "⚠️ Lengkapi semua field wajib (*)", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val capacity = capacityText.toIntOrNull()
                    val newEvent = Event(
                        id = 0,
                        title = title.trim(),
                        date = date.trim(),
                        time = time.trim(),
                        location = location.trim(),
                        description = if (description.isBlank()) null else description.trim(),
                        capacity = capacity,
                        status = status
                    )

                    viewModel.createEvent(newEvent) { success, msg ->
                        if (success) {
                            Toast.makeText(context, "✅ Event berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "❌ Gagal: ${msg ?: "Unknown"}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(10.dp))
                Text(if (isLoading) "Menyimpan..." else "Simpan Event", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Close, null)
                Spacer(Modifier.width(10.dp))
                Text("Batal")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/** Helper PRIVATE + nama unik supaya tidak tabrakan dengan file lain */
private fun add_getStatusLabel(status: String): String = when (status) {
    "upcoming" -> "Akan Datang"
    "ongoing" -> "Berlangsung"
    "completed" -> "Selesai"
    "cancelled" -> "Dibatalkan"
    else -> status
}

private fun add_formatDateDisplay(date: String): String {
    return try {
        val parts = date.split("-")
        if (parts.size == 3) {
            val months = listOf(
                "", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
            )
            "${parts[2]} ${months[parts[1].toInt()]} ${parts[0]}"
        } else date
    } catch (e: Exception) {
        date
    }
}

private fun add_formatTimeDisplay(time: String): String {
    return try {
        val parts = time.split(":")
        if (parts.size >= 2) "${parts[0]}:${parts[1]} WIB" else time
    } catch (e: Exception) {
        time
    }
}
