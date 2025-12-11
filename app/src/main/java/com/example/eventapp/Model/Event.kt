package com.example.eventapp.Model

data class Event(
    val id: Int? = null,
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String? = null,
    val capacity: Int? = null,
    val status: String,
    val updated_at: String? = null
)
