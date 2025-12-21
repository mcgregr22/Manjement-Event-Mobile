package com.example.eventapp.ui.theme.Calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


import com.example.eventapp.Model.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage(
    events: List<Event>,
    onSelectDate: (String) -> Unit,
    onBack: () -> Unit
) {
    // format tanggal
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Kalender state
    val calendar = remember { Calendar.getInstance() }
    var year by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(calendar.get(Calendar.MONTH)) } // 0..11

    val todayString = sdf.format(Calendar.getInstance().time)
    var selectedDate by remember { mutableStateOf(todayString) }

    val daysInMonth = getDaysInMonth(year, month)
    val firstDayOffset = getFirstDayOfWeek(year, month)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Month header
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (month == 0) {
                                month = 11
                                year -= 1
                            } else month--
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
                    }

                    Text(
                        text = "${monthName(month)} $year",
                        style = MaterialTheme.typography.titleLarge
                    )

                    IconButton(
                        onClick = {
                            if (month == 11) {
                                month = 0
                                year++
                            } else month++
                        }
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
                    }
                }
            }

            // Weekday labels
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                        Text(
                            text = it,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Calendar Grid
            item {
                CalendarGrid(
                    year = year,
                    month = month,
                    daysInMonth = daysInMonth,
                    firstDayOffset = firstDayOffset,
                    events = events,
                    selectedDate = selectedDate,
                    todayDate = todayString,
                    onSelect = { dateStr ->
                        selectedDate = dateStr
                        onSelectDate(dateStr)
                    }
                )
            }

            // List event
            item {
                Text(
                    text = "Events on $selectedDate:",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(events.filter { it.date == selectedDate }.sortedBy { it.time }) { ev ->
                EventItem(ev)
            }
        }
    }
}

@Composable
fun EventItem(ev: Event) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(ev.title, style = MaterialTheme.typography.titleMedium)
        Text("${ev.time} • ${ev.location}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun CalendarGrid(
    year: Int,
    month: Int,
    daysInMonth: Int,
    firstDayOffset: Int,
    events: List<Event>,
    selectedDate: String,
    todayDate: String,
    onSelect: (String) -> Unit
) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val total = firstDayOffset + daysInMonth
    val rows = (total + 6) / 7

    Column(Modifier.fillMaxWidth()) {
        repeat(rows) { row ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                (0 until 7).forEach { col ->
                    val index = row * 7 + col

                    if (index < firstDayOffset || index >= total) {
                        Box(Modifier.size(40.dp)) { /* empty cell */ }
                    } else {
                        val day = index - firstDayOffset + 1

                        val cal = Calendar.getInstance()
                        cal.set(year, month, day)
                        val dateString = sdf.format(cal.time)

                        val hasEvent = events.any { it.date == dateString }
                        val isSelected = selectedDate == dateString
                        val isToday = todayDate == dateString

                        DayCell(
                            day = day,
                            hasEvent = hasEvent,
                            isSelected = isSelected,
                            isToday = isToday,
                            onClick = { onSelect(dateString) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    hasEvent: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    hasEvent -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
            )
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

fun getDaysInMonth(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(year, month, 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun getFirstDayOfWeek(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(year, month, 1)
    return cal.get(Calendar.DAY_OF_WEEK) - 1 // shift to 0..6
}

fun monthName(m: Int): String {
    val list = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    return list[m]
}