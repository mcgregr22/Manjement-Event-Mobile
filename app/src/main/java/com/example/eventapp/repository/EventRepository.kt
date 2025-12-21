package com.example.eventapp.repository
import com.example.eventapp.Network.ApiClient
import com.example.eventapp.Model.Event

class EventRepository {
    private val api = ApiClient.instance

    suspend fun getAllEvents() = api.getAllEvents()

    suspend fun getEventById(id: Int) = api.getEventById(id)

    suspend fun getEventsByDate(date: String) = api.getEventsByDate(date)

    suspend fun getEventsByStatus(status: String) = api.getEventsByStatus(status)

    suspend fun getEventsByDateRange(from: String, to: String) = api.getEventsByDateRange(from, to)

    suspend fun getStatistics() = api.getStatistics()

    suspend fun createEvent(event: Event) = api.createEvent(event)

    suspend fun updateEvent(id: Int, event: Event) = api.updateEvent(id, event)

    suspend fun deleteEvent(id: Int) = api.deleteEvent(id)
}


