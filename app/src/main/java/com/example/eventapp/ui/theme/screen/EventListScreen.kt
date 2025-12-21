package com.example.eventapp.ui.theme.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventapp.Model.Event
import com.example.eventapp.ui.theme.viewmodel.EventViewModel

private val XmasRed = Color(0xFFB91C1C)
private val XmasGreen = Color(0xFF15803D)
private val XmasGold = Color(0xFFF59E0B)
private val Snow = Color(0xFFF8FAFC)

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

    LaunchedEffect(Unit) { viewModel.loadEvents() }

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
                            Text("🎄 Event Manager", fontWeight = FontWeight.ExtraBold)
                            Text(
                                "${events.size} Event",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                        IconButton(onClick = { viewModel.loadEvents() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                        IconButton(onClick = onCalendarClick) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Kalender")
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            XmasFilterItem("Semua", "all", selectedFilter) {
                                selectedFilter = it; showFilterMenu = false
                            }
                            XmasFilterItem("🎄 Akan Datang", "upcoming", selectedFilter) {
                                selectedFilter = it; showFilterMenu = false
                            }
                            XmasFilterItem("✨ Berlangsung", "ongoing", selectedFilter) {
                                selectedFilter = it; showFilterMenu = false
                            }
                            XmasFilterItem("❄️ Selesai", "completed", selectedFilter) {
                                selectedFilter = it; showFilterMenu = false
                            }
                            XmasFilterItem("🧨 Dibatalkan", "cancelled", selectedFilter) {
                                selectedFilter = it; showFilterMenu = false
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(18.dp),
                containerColor = XmasRed
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Event Baru", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgBrush)
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari event…") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = XmasGreen,
                        unfocusedBorderColor = XmasRed.copy(alpha = 0.35f),
                        focusedLeadingIconColor = XmasGreen,
                        unfocusedLeadingIconColor = XmasRed,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    )
                )

                Spacer(Modifier.height(10.dp))

                AnimatedVisibility(visible = selectedFilter != "all") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = true,
                            onClick = { selectedFilter = "all" },
                            label = { Text(XmasStatusLabel(selectedFilter)) },
                            leadingIcon = { Icon(Icons.Default.FilterAlt, null, Modifier.size(18.dp)) },
                            trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(18.dp)) }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                when {
                    isLoading -> XmasLoading()
                    filteredEvents.isEmpty() -> XmasEmpty(searchQuery)
                    else -> LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredEvents, key = { it.id }) { ev ->
                            XmasEventCard(ev) { onEventClick(ev.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun XmasFilterItem(name: String, key: String, selected: String, onClick: (String) -> Unit) {
    DropdownMenuItem(
        text = { Text(name) },
        onClick = { onClick(key) },
        leadingIcon = { if (selected == key) Icon(Icons.Default.Check, null) }
    )
}

@Composable
private fun XmasLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = XmasGreen)
        Spacer(Modifier.height(12.dp))
        Text("Memuat event…", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun XmasEmpty(search: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.EventBusy, null, Modifier.size(72.dp), tint = XmasRed)
        Spacer(Modifier.height(12.dp))
        Text(
            if (search.isEmpty()) "Belum ada event 🎁" else "Event tidak ditemukan",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Klik “Event Baru” untuk menambahkan",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun XmasEventCard(event: Event, onClick: () -> Unit) {
    val accent = XmasStatusColor(event.status)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            event.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            event.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        color = accent.copy(alpha = 0.16f),
                        contentColor = accent,
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = XmasStatusLabel(event.status),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    XmasInfoMini(Icons.Default.CalendarToday, XmasFormatDate(event.date))
                    XmasInfoMini(Icons.Default.Schedule, XmasFormatTime(event.time))
                }

                Spacer(Modifier.height(10.dp))

                event.capacity?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, null, Modifier.size(18.dp), tint = XmasGold)
                        Spacer(Modifier.width(6.dp))
                        Text("$it orang", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun XmasInfoMini(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(XmasGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(16.dp), tint = XmasGreen)
        }
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

// helper PRIVATE + nama unik biar tidak bentrok dengan file lain
private fun XmasStatusColor(status: String): Color = when (status) {
    "upcoming" -> XmasGreen
    "ongoing" -> XmasGold
    "completed" -> Color(0xFF0EA5E9)
    "cancelled" -> XmasRed
    else -> Color(0xFF64748B)
}

private fun XmasStatusLabel(status: String): String = when (status) {
    "upcoming" -> "🎄 Akan Datang"
    "ongoing" -> "✨ Berlangsung"
    "completed" -> "❄️ Selesai"
    "cancelled" -> "🧨 Dibatalkan"
    else -> status
}

private fun XmasFormatDate(date: String): String {
    return try {
        val (y, m, d) = date.split("-")
        val bulan = listOf("", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
        "$d ${bulan[m.toInt()]} $y"
    } catch (_: Exception) {
        date
    }
}

private fun XmasFormatTime(time: String): String {
    return try {
        val parts = time.split(":")
        val h = parts.getOrNull(0) ?: return time
        val m = parts.getOrNull(1) ?: return time
        "$h:$m WIB"
    } catch (_: Exception) {
        time
    }
}
