package com.example.eventapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eventapp.Network.ApiClient
import com.example.eventapp.Model.ApiResponse
import com.example.eventapp.Model.Event
import com.example.eventapp.ui.theme.EventappTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    private val eventsState = mutableStateOf<List<Event>>(emptyList())
    private val loadingState = mutableStateOf(false)
    private val errorState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadEvents()   // panggil API

        setContent {
            EventappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EventScreen(
                        events = eventsState.value,
                        loading = loadingState.value,
                        error = errorState.value
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
}

@Composable
fun EventScreen(
    events: List<Event>,
    loading: Boolean,
    error: String?
) {
    when {
        loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: $error")
            }
        }

        events.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Belum ada event")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
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
                            if (!event.description.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = event.description!!)
                            }
                        }
                    }
                }
            }
        }
    }
}
