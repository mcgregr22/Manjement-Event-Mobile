package com.example.eventapp.ui.theme.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventapp.Model.Event
import com.example.eventapp.ui.theme.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    viewModel: EventViewModel = viewModel(),
    onEventClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }
    var showFilterMenu by remember { mutableStateOf(false) }

    val filteredEvents = remember(events, searchQuery, selectedFilter) {
        events.filter { event ->
            val matchSearch =
                event.title.contains(searchQuery, true) ||
                        event.location.contains(searchQuery, true)

            val matchFilter = when (selectedFilter) {
                "all" -> true
                else -> event.status == selectedFilter
            }

            matchSearch && matchFilter
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Event Manager", fontWeight = FontWeight.Bold)
                        Text(
                            "${events.size} Event",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, null)
                    }
                    IconButton(onClick = { viewModel.loadEvents() }) {
                        Icon(Icons.Default.Refresh, null)
                    }
                    IconButton(onClick = onCalendarClick) {
                        Icon(Icons.Default.CalendarMonth, "Kalender")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        FilterItem("Semua Event", "all", selectedFilter) {
                            selectedFilter = it
                            showFilterMenu = false
                        }
                        FilterItem("Akan Datang", "upcoming", selectedFilter) {
                            selectedFilter = it
                            showFilterMenu = false
                        }
                        FilterItem("Berlangsung", "ongoing", selectedFilter) {
                            selectedFilter = it
                            showFilterMenu = false
                        }
                        FilterItem("Selesai", "completed", selectedFilter) {
                            selectedFilter = it
                            showFilterMenu = false
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Event Baru") }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cari event…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                singleLine = true
            )

            AnimatedVisibility(visible = selectedFilter != "all") {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = true,
                        onClick = { selectedFilter = "all" },
                        label = {
                            Text(
                                when (selectedFilter) {
                                    "upcoming" -> "Akan Datang"
                                    "ongoing" -> "Berlangsung"
                                    "completed" -> "Selesai"
                                    else -> selectedFilter
                                }
                            )
                        },
                        trailingIcon = {
                            Icon(Icons.Default.Close, null, Modifier.size(16.dp))
                        }
                    )
                }
            }

            Box(Modifier.fillMaxSize()) {
                when {
                    isLoading -> LoadingState()
                    filteredEvents.isEmpty() -> EmptyState(searchQuery)
                    else -> LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredEvents, key = { it.id }) { ev ->
                            EventCard(ev) { onEventClick(ev.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterItem(name: String, key: String, selected: String, onClick: (String) -> Unit) {
    DropdownMenuItem(
        text = { Text(name) },
        onClick = { onClick(key) },
        leadingIcon = { if (selected == key) Icon(Icons.Default.Check, null) }
    )
}

@Composable
fun LoadingState() {
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Memuat event…")
        }
    }
}

@Composable
fun EmptyState(search: String) {
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.EventBusy, null, Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                if (search.isEmpty()) "Belum ada event" else "Event tidak ditemukan",
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text("Klik 'Event Baru' untuk menambahkan")
        }
    }
}

@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(
                    event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(event.status)
            }

            Spacer(Modifier.height(12.dp))

            InfoRow(Icons.Default.CalendarToday, formatDate(event.date))
            InfoRow(Icons.Default.Schedule, formatTime(event.time))
            InfoRow(Icons.Default.LocationOn, event.location)

            event.capacity?.let {
                InfoRow(Icons.Default.People, "$it orang")
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, label) = when (status) {
        "upcoming" -> MaterialTheme.colorScheme.primaryContainer to "Akan Datang"
        "ongoing" -> MaterialTheme.colorScheme.tertiaryContainer to "Berlangsung"
        "completed" -> MaterialTheme.colorScheme.secondaryContainer to "Selesai"
        "cancelled" -> MaterialTheme.colorScheme.errorContainer to "Dibatalkan"
        else -> MaterialTheme.colorScheme.surfaceVariant to status
    }

    Surface(color = color, shape = MaterialTheme.shapes.small) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

fun formatDate(date: String): String {
    return try {
        val (y, m, d) = date.split("-")
        val bulan = listOf(
            "", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
            "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
        )
        "$d ${bulan[m.toInt()]} $y"
    } catch (_: Exception) {
        date
    }
}

fun formatTime(time: String): String {
    return try {
        val parts = time.split(":")
        val h = parts.getOrNull(0) ?: return time
        val m = parts.getOrNull(1) ?: return time
        "$h:$m WIB"
    } catch (_: Exception) {
        time
    }
}
