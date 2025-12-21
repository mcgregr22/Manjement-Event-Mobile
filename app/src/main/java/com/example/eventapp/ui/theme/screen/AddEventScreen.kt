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
import java.util.*

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

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            dateError = false
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            time = String.format("%02d:%02d:00", hourOfDay, minute)
            timeError = false
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // Fungsi lokal untuk menghindari konflik dengan fungsi lain di project
    fun getEventStatusLabel(status: String): String {
        return when(status) {
            "upcoming" -> "Akan Datang"
            "ongoing" -> "Berlangsung"
            "completed" -> "Selesai"
            "cancelled" -> "Dibatalkan"
            else -> status
        }
    }

    fun formatEventDateDisplay(date: String): String {
        return try {
            val parts = date.split("-")
            if (parts.size == 3) {
                val months = listOf("", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                    "Juli", "Agustus", "September", "Oktober", "November", "Desember")
                "${parts[2]} ${months[parts[1].toInt()]} ${parts[0]}"
            } else date
        } catch (e: Exception) {
            date
        }
    }

    fun formatEventTimeDisplay(time: String): String {
        return try {
            val parts = time.split(":")
            if (parts.size >= 2) {
                "${parts[0]}:${parts[1]} WIB"
            } else time
        } catch (e: Exception) {
            time
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Event Baru", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Isi semua field yang bertanda bintang (*) untuk membuat event baru",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                label = { Text("Judul Event *") },
                placeholder = { Text("Contoh: Workshop Android Development") },
                leadingIcon = { Icon(Icons.Default.Info, null) },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("Judul wajib diisi") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Date Field with Picker
            OutlinedTextField(
                value = date,
                onValueChange = { },
                label = { Text("Tanggal *") },
                placeholder = { Text("Pilih tanggal") },
                leadingIcon = { Icon(Icons.Default.EditCalendar, null) },
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.EditCalendar, "Pilih Tanggal")
                    }
                },
                isError = dateError,
                supportingText = if (dateError) {
                    { Text("Tanggal wajib diisi") }
                } else if (date.isNotEmpty()) {
                    { Text("Format: ${formatEventDateDisplay(date)}", color = MaterialTheme.colorScheme.primary) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Time Field with Picker
            OutlinedTextField(
                value = time,
                onValueChange = { },
                label = { Text("Waktu *") },
                placeholder = { Text("Pilih waktu") },
                leadingIcon = { Icon(Icons.Default.Schedule, null) },
                trailingIcon = {
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(Icons.Default.Schedule, "Pilih Waktu")
                    }
                },
                isError = timeError,
                supportingText = if (timeError) {
                    { Text("Waktu wajib diisi") }
                } else if (time.isNotEmpty()) {
                    { Text("Format: ${formatEventTimeDisplay(time)}", color = MaterialTheme.colorScheme.primary) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Location Field
            OutlinedTextField(
                value = location,
                onValueChange = {
                    location = it
                    locationError = false
                },
                label = { Text("Lokasi *") },
                placeholder = { Text("Contoh: Gedung A Lantai 3") },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                isError = locationError,
                supportingText = if (locationError) {
                    { Text("Lokasi wajib diisi") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi (opsional)") },
                placeholder = { Text("Tambahkan deskripsi event...") },
                leadingIcon = { Icon(Icons.Default.Description, null) },
                supportingText = {
                    Text("${description.length}/500 karakter")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Capacity Field
            OutlinedTextField(
                value = capacityText,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        capacityText = it
                    }
                },
                label = { Text("Kapasitas Peserta (opsional)") },
                placeholder = { Text("50") },
                leadingIcon = { Icon(Icons.Default.Group, null) },
                supportingText = {
                    if (capacityText.isNotEmpty()) {
                        Text("Maksimal ${capacityText} orang", color = MaterialTheme.colorScheme.primary)
                    } else null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Status Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = getEventStatusLabel(status),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status Event *") },
                    leadingIcon = {
                        Icon(
                            when(status) {
                                "upcoming" -> Icons.Default.Schedule
                                "ongoing" -> Icons.Default.PlayArrow
                                "completed" -> Icons.Default.CheckCircle
                                "cancelled" -> Icons.Default.Cancel
                                else -> Icons.Default.Info
                            },
                            null
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = when(status) {
                            "upcoming" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                            "ongoing" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f)
                            "completed" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                            "cancelled" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ),
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
                            text = { Text(getEventStatusLabel(option)) },
                            onClick = {
                                status = option
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    when(option) {
                                        "upcoming" -> Icons.Default.Schedule
                                        "ongoing" -> Icons.Default.PlayArrow
                                        "completed" -> Icons.Default.CheckCircle
                                        "cancelled" -> Icons.Default.Cancel
                                        else -> Icons.Default.Info
                                    },
                                    null
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    // Validation
                    titleError = title.isBlank()
                    dateError = date.isBlank()
                    timeError = time.isBlank()
                    locationError = location.isBlank()

                    if (titleError || dateError || timeError || locationError) {
                        Toast.makeText(context, "⚠️ Mohon lengkapi semua field yang wajib", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(context, "❌ Gagal: ${msg ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Menyimpan...", style = MaterialTheme.typography.titleMedium)
                } else {
                    Icon(Icons.Default.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Event", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}