package com.example.eventapp.ui.theme.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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

    // Background gradient biar “berwarna”
    val bgBrush = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
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
                            Text(
                                "Event Manager",
                                fontWeight = FontWeight.ExtraBold
                            )
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
                            FilterItem("Dibatalkan", "cancelled", selectedFilter) {
                                selectedFilter = it
                                showFilterMenu = false
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(18.dp),
                containerColor = MaterialTheme.colorScheme.primary
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

                // Search bar modern
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
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                    )
                )

                Spacer(Modifier.height(10.dp))

                // Chip filter aktif
                AnimatedVisibility(visible = selectedFilter != "all") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
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
                                        "cancelled" -> "Dibatalkan"
                                        else -> selectedFilter
                                    }
                                )
                            },
                            leadingIcon = { Icon(Icons.Default.FilterAlt, null, Modifier.size(18.dp)) },
                            trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(18.dp)) }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                when {
                    isLoading -> ModernLoadingState()

                    filteredEvents.isEmpty() -> ModernEmptyState(searchQuery)

                    else -> LazyColumn(
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredEvents, key = { it.id }) { ev ->
                            ModernEventCard(
                                event = ev,
                                onClick = { onEventClick(ev.id) }
                            )
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
private fun ModernLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text("Memuat event…", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ModernEmptyState(search: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.EventBusy,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            if (search.isEmpty()) "Belum ada event"
            else "Event tidak ditemukan",
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
private fun ModernEventCard(
    event: Event,
    onClick: () -> Unit
) {
    val (accent, label, icon) = statusStyle(event.status)

    val pressedScale by animateFloatAsState(targetValue = 1f, label = "scale")

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Accent strip berwarna di kiri
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
                            text = event.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    StatusPill(
                        text = label,
                        color = accent,
                        icon = icon
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoMini(Icons.Default.CalendarToday, formatDate(event.date))
                    InfoMini(Icons.Default.Schedule, formatTime(event.time))
                }

                Spacer(Modifier.height(10.dp))

                if (event.capacity != null) {
                    InfoRowLine(Icons.Default.People, "${event.capacity} orang")
                }
            }
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    color: Color,
    icon: ImageVector
) {
    Surface(
        color = color.copy(alpha = 0.16f),
        contentColor = color,
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InfoMini(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun InfoRowLine(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.tertiary)
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun statusStyle(status: String): Triple<Color, String, ImageVector> {
    return when (status) {
        "upcoming" -> Triple(Color(0xFF3B82F6), "Akan Datang", Icons.Default.Schedule)
        "ongoing" -> Triple(Color(0xFF8B5CF6), "Berlangsung", Icons.Default.PlayArrow)
        "completed" -> Triple(Color(0xFF22C55E), "Selesai", Icons.Default.CheckCircle)
        "cancelled" -> Triple(Color(0xFFEF4444), "Dibatalkan", Icons.Default.Cancel)
        else -> Triple(Color(0xFF64748B), status, Icons.Default.Event)
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
