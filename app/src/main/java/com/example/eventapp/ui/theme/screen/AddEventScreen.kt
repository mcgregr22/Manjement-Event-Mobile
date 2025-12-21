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

private val XmasRed = Color(0xFFB91C1C)
private val XmasGreen = Color(0xFF15803D)
private val XmasGold = Color(0xFFF59E0B)
private val Snow = Color(0xFFF8FAFC)

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
            XmasGreen.copy(alpha = 0.18f),
            XmasRed.copy(alpha = 0.10f),
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Snow.copy(alpha = 0.75f), Color.Transparent)
                            )
                        )
                )

                TopAppBar(
                    title = {
                        Column {
                            Text("🎁 Tambah Event Baru", fontWeight = FontWeight.ExtraBold)
                            Text(
                                "Biar makin meriah di kalender ✨",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, "Kembali")
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
            // Banner natal
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = XmasGold.copy(alpha = 0.16f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = XmasGold)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Field bertanda * wajib diisi ya 🎄",
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
                        leadingIcon = { Icon(Icons.Default.Event, null, tint = XmasRed) },
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
                        leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = XmasGreen) },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.EditCalendar, contentDescription = "Pilih tanggal", tint = XmasGreen)
                            }
                        },
                        isError = dateError,
                        supportingText = if (dateError) {
                            { Text("Tanggal wajib diisi") }
                        } else if (date.isNotEmpty()) {
                            { Text("Dipilih: ${AddXmasFormatDateDisplay(date)}", color = XmasGreen) }
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
                        leadingIcon = { Icon(Icons.Default.Schedule, null, tint = XmasGold) },
                        trailingIcon = {
                            IconButton(onClick = { timePickerDialog.show() }) {
                                Icon(Icons.Default.AccessTime, contentDescription = "Pilih waktu", tint = XmasGold)
                            }
                        },
                        isError = timeError,
                        supportingText = if (timeError) {
                            { Text("Waktu wajib diisi") }
                        } else if (time.isNotEmpty()) {
                            { Text("Dipilih: ${AddXmasFormatTimeDisplay(time)}", color = XmasGold) }
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
                        leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = XmasRed) },
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
                        leadingIcon = { Icon(Icons.Default.Description, null, tint = XmasGreen) },
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
                        leadingIcon = { Icon(Icons.Default.People, null, tint = XmasGold) },
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
                            value = AddXmasStatusLabel(status),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status Event *") },
                            leadingIcon = {
                                Icon(Icons.Default.Star, null, tint = AddXmasStatusColor(status))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(AddXmasStatusLabel(option)) },
                                    onClick = {
                                        status = option
                                        expanded = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Star, null, tint = AddXmasStatusColor(option))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = XmasRed)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
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

/** helpers PRIVATE + nama unik -> tidak bentrok file lain */
private fun AddXmasStatusColor(status: String): Color = when (status) {
    "upcoming" -> XmasGreen
    "ongoing" -> XmasGold
    "completed" -> Color(0xFF0EA5E9)
    "cancelled" -> XmasRed
    else -> Color(0xFF64748B)
}

private fun AddXmasStatusLabel(status: String): String = when (status) {
    "upcoming" -> "🎄 Akan Datang"
    "ongoing" -> "✨ Berlangsung"
    "completed" -> "❄️ Selesai"
    "cancelled" -> "🧨 Dibatalkan"
    else -> status
}

private fun AddXmasFormatDateDisplay(date: String): String {
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

private fun AddXmasFormatTimeDisplay(time: String): String {
    return try {
        val parts = time.split(":")
        if (parts.size >= 2) "${parts[0]}:${parts[1]} WIB" else time
    } catch (_: Exception) {
        time
    }
}
