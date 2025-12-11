package com.example.eventapp.Network



import com.example.eventapp.Model.ApiResponse
import com.example.eventapp.Model.Event
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.eventapp.Model.EventRequest
import retrofit2.http.*

interface ApiService {


        // GET semua event
        @GET("event-api-php.php")
        fun getEvents(
            @Query("status") status: String? = null,
            @Query("date_from") dateFrom: String? = null,
            @Query("date_to") dateTo: String? = null
        ): Call<ApiResponse<List<Event>>>

        // POST - tambah event
        @POST("event-api-php.php")
        fun createEvent(
            @Body request: EventRequest
        ): Call<ApiResponse<Event>>

        // DELETE - hapus event
        @DELETE("event-api-php.php")
        fun deleteEvent(
            @Query("id") id: Int
        ): Call<ApiResponse<Any>>
    }
