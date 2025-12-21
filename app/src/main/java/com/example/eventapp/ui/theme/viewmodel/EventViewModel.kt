package com.example.eventapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapp.Model.Event
import com.example.eventapp.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EventViewModel : ViewModel() {

    private val repository = EventRepository()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // ➕ Selected Date for Calendar
    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate

    fun setSelectedDate(date: String?) {
        _selectedDate.value = date
    }

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.getAllEvents()
                _events.value = res.data ?: emptyList()
                _message.value = null
            } catch (e: Exception) {
                _message.value = "Gagal memuat event: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun findEventLocal(id: Int): Event? {
        return _events.value.find { it.id == id }
    }

    fun getEventById(id: Int, onResult: (Event?) -> Unit) {
        viewModelScope.launch {
            try {
                val res = repository.getEventById(id)
                onResult(res.data)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun createEvent(event: Event, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.createEvent(event)
                _message.value = res.message
                loadEvents()
                onDone(true, res.message)
            } catch (e: Exception) {
                onDone(false, e.localizedMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEvent(id: Int, event: Event, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.updateEvent(id, event)
                _message.value = res.message
                loadEvents()
                onDone(true, res.message)
            } catch (e: Exception) {
                onDone(false, e.localizedMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEvent(id: Int, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val res = repository.deleteEvent(id)
                _message.value = res.message
                loadEvents()
                onDone(true, res.message)
            } catch (e: Exception) {
                onDone(false, e.localizedMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==========================
    //      Calendar Helpers
    // ==========================

    private fun parseDateToCalendar(dateString: String?): Calendar? {
        if (dateString.isNullOrBlank()) return null
        return try {
            val d = dateFormatter.parse(dateString)
            val cal = Calendar.getInstance()
            cal.time = d!!
            cal
        } catch (e: Exception) {
            null
        }
    }

    fun getEventsForDate(dateString: String): List<Event> {
        return _events.value.filter { it.date == dateString }
            .sortedBy { it.time }
    }

    fun hasEvent(dateString: String): Boolean {
        return _events.value.any { it.date == dateString }
    }

    fun eventCount(dateString: String): Int {
        return _events.value.count { it.date == dateString }
    }

    fun todayString(): String {
        return dateFormatter.format(Date())
    }

    fun toDateString(year: Int, monthZeroBased: Int, day: Int): String {
        val cal = Calendar.getInstance()
        cal.set(year, monthZeroBased, day, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return dateFormatter.format(cal.time)
    }
}

