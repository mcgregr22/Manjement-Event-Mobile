package com.example.eventapp.Network

import com.example.eventapp.Model.ApiResponse
import com.example.eventapp.Model.Event
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.*

interface ApiService {


        // GET all events
        @GET("api.php")
        suspend fun getAllEvents(): ApiResponse<List<Event>>

        // GET by ID
        @GET("api.php")
        suspend fun getEventById(@Query("id") id: Int): ApiResponse<Event>

        // GET by date
        @GET("api.php")
        suspend fun getEventsByDate(@Query("date") date: String): ApiResponse<List<Event>>

        // GET by status
        @GET("api.php")
        suspend fun getEventsByStatus(@Query("status") status: String): ApiResponse<List<Event>>

        // GET date range
        @GET("api.php")
        suspend fun getEventsByDateRange(
            @Query("date_from") from: String,
            @Query("date_to") to: String
        ): ApiResponse<List<Event>>

        // GET statistics
        @GET("api.php")
        suspend fun getStatistics(@Query("stats") stats: Int = 1): ApiResponse<Map<String, String>>

        // POST create event
        @POST("api.php")
        suspend fun createEvent(@Body event: Event): ApiResponse<Event>

        // PUT update event (id via query param)
        @PUT("api.php")
        suspend fun updateEvent(@Query("id") id: Int, @Body event: Event): ApiResponse<Event>

        // DELETE event
        @DELETE("api.php")
        suspend fun deleteEvent(@Query("id") id: Int): ApiResponse<String>
    }
