package com.example.eventapp.Model

data class Event(
    val id: Int,
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String?,
    val capacity: Int?,
    val status: String
)