package com.example.eventapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
import com.example.eventapp.Network.ApiClient
import com.example.eventapp.Model.ApiResponse
import com.example.eventapp.Model.Event
import com.example.eventapp.Model.EventRequest
import com.example.eventapp.ui.theme.EventappTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val eventsState = mutableStateOf<List<Event>>(emptyList())
    private val loadingState = mutableStateOf(false)
    private val errorState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ambil data ketika app dibuka
        loadEvents()

        setContent {
            EventappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ModernMainScreen(
                        events = eventsState.value,
                        loading = loadingState.value,
                        error = errorState.value,
                        onRefresh = { loadEvents() },
                        onCreateEvent = { request -> createEvent(request) },
                        onUpdateEvent = { id, request -> updateEvent(id, request) }, // kalau sudah buat fungsi updateEvent
                        onDeleteEvent = { event ->
                            event.id?.let { deleteEvent(it) }
                        }
                    )
                }
            }
        }
    }

    private fun loadEvents() {
        loadingState.value = true
        errorState.value = null

        ApiClient.apiService.getEvents()
            .enqueue(object : Callback<ApiResponse<List<Event>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<Event>>>,
                    response: Response<ApiResponse<List<Event>>>
                ) {
                    loadingState.value = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        val events = body?.data ?: emptyList()
                        eventsState.value = events

                        Log.d("API", "Status: ${body?.status}, msg: ${body?.message}")
                        Log.d("API", "Jumlah event: ${events.size}")
                    } else {
                        val msg = "Error ${response.code()}"
                        errorState.value = msg
                        Log.e("API", msg)
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<List<Event>>>,
                    t: Throwable
                ) {
                    loadingState.value = false
                    errorState.value = t.message ?: "Gagal konek"
                    Log.e("API", "Failure: ${t.message}")
                }
            })
    }

    private fun createEvent(request: EventRequest) {
        loadingState.value = true
        errorState.value = null

        ApiClient.apiService.createEvent(request)
            .enqueue(object : Callback<ApiResponse<Event>> {
                override fun onResponse(
                    call: Call<ApiResponse<Event>>,
                    response: Response<ApiResponse<Event>>
                ) {
                    loadingState.value = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("API", "Create OK: ${body?.message}")
                        loadEvents()   // refresh list setelah tambah
                    } else {
                        val msg = "Gagal create: ${response.code()}"
                        errorState.value = msg
                        Log.e("API", msg)
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<Event>>,
                    t: Throwable
                ) {
                    loadingState.value = false
                    errorState.value = t.message ?: "Gagal create"
                    Log.e("API", "Create failure: ${t.message}")
                }
            })
    }

    private fun deleteEvent(id: Int) {
        loadingState.value = true
        errorState.value = null

        ApiClient.apiService.deleteEvent(id)
            .enqueue(object : Callback<ApiResponse<Any>> {
                override fun onResponse(
                    call: Call<ApiResponse<Any>>,
                    response: Response<ApiResponse<Any>>
                ) {
                    loadingState.value = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("API", "Delete OK: ${body?.message}")
                        loadEvents()   // refresh list setelah hapus
                    } else {
                        val msg = "Gagal delete: ${response.code()}"
                        errorState.value = msg
                        Log.e("API", msg)
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<Any>>,
                    t: Throwable
                ) {
                    loadingState.value = false
                    errorState.value = t.message ?: "Gagal delete"
                    Log.e("API", "Delete failure: ${t.message}")
                }
            })
    }

    // ⬇️ fungsi BARU untuk UPDATE event
    private fun updateEvent(id: Int, request: EventRequest) {
        loadingState.value = true
        errorState.value = null

        ApiClient.apiService.updateEvent(id, request)
            .enqueue(object : Callback<ApiResponse<Event>> {
                override fun onResponse(
                    call: Call<ApiResponse<Event>>,
                    response: Response<ApiResponse<Event>>
                ) {
                    loadingState.value = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("API", "Update OK: ${body?.message}")
                        loadEvents()   // refresh list setelah update
                    } else {
                        val msg = "Gagal update: ${response.code()}"
                        errorState.value = msg
                        Log.e("API", msg)
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<Event>>,
                    t: Throwable
                ) {
                    loadingState.value = false
                    errorState.value = t.message ?: "Gagal update"
                    Log.e("API", "Update failure: ${t.message}")
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    events: List<Event>,
    loading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onCreateEvent: (EventRequest) -> Unit,
    onUpdateEvent: (Int, EventRequest) -> Unit, // ⬅️ baru
    onDeleteEvent: (Event) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<Event?>(null) } // ⬅️ event yg sedang di-edit

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Event Management") },
                actions = {
                    TextButton(onClick = onRefresh) {
                        Text("Refresh")
                    }
                }
            )

            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error: $error")
                }
            } else {
                EventList(
                    events = events,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    onEditEvent = { event -> editingEvent = event }, // ⬅️ baru
                    onDeleteEvent = onDeleteEvent
                )
            }
        }

        // Tombol + di pojok kanan bawah
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+")
        }

        if (showAddDialog) {
            AddEventDialog(
                onDismiss = { showAddDialog = false },
                onSave = { request ->
                    onCreateEvent(request)
                    showAddDialog = false
                }
            )
        }

        // Dialog edit event
        editingEvent?.let { event ->
            EditEventDialog(
                event = event,
                onDismiss = { editingEvent = null },
                onSave = { request ->
                    event.id?.let { id ->
                        onUpdateEvent(id, request)
                    }
                    editingEvent = null
                }
            )
        }
    }
}

@Composable
fun EventList(
    events: List<Event>,
    modifier: Modifier = Modifier,
    onEditEvent: (Event) -> Unit,     // ⬅️ baru
    onDeleteEvent: (Event) -> Unit
) {
    if (events.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Belum ada event")
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(16.dp)
        ) {
            items(events) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = "${event.date} ${event.time}")
                        Text(text = event.location)
                        Text(text = "Status: ${event.status}")
                        if (!event.description.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = event.description.orEmpty())
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (event.id != null) {
                                TextButton(onClick = { onEditEvent(event) }) {
                                    Text("Edit")
                                }
                                TextButton(onClick = { onDeleteEvent(event) }) {
                                    Text("Hapus")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onSave: (EventRequest) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }      // YYYY-MM-DD (di-set dari DatePicker)
    var time by remember { mutableStateOf("") }      // HH:MM (di-set dari TimePicker)
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
                date = "$y-$mm-$dd"   // format sesuai validasi PHP
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
                time = "$hh:$mm"      // format HH:MM
            },
            hour, minute, true
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Event") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                // Tanggal: klik untuk buka kalender
                OutlinedTextField(
                    value = date,
                    onValueChange = { },
                    label = { Text("Tanggal (YYYY-MM-DD)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openDatePicker() },
                    readOnly = true
                )
                Spacer(Modifier.height(8.dp))

                // Jam: klik untuk buka time picker
                OutlinedTextField(
                    value = time,
                    onValueChange = { },
                    label = { Text("Jam (HH:MM)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openTimePicker() },
                    readOnly = true
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi (opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = capacityText,
                    onValueChange = { capacityText = it },
                    label = { Text("Kapasitas (opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status (upcoming/ongoing/completed/cancelled)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // cek field wajib terisi
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

// ⬇️ Dialog BARU untuk EDIT event
@Composable
fun EditEventDialog(
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
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = date,
                    onValueChange = { },
                    label = { Text("Tanggal (YYYY-MM-DD)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openDatePicker() },
                    readOnly = true
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = time,
                    onValueChange = { },
                    label = { Text("Jam (HH:MM)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openTimePicker() },
                    readOnly = true
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi (opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = capacityText,
                    onValueChange = { capacityText = it },
                    label = { Text("Kapasitas (opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status (upcoming/ongoing/completed/cancelled)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
