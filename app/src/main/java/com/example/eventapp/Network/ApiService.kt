package com.example.eventapp.Network



import com.example.eventapp.Model.ApiResponse
import com.example.eventapp.Model.Event
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // GET semua event
    @GET("event-api-php.php")
    fun getEvents(
        @Query("status") status: String? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null
    ): Call<ApiResponse<List<Event>>>
}