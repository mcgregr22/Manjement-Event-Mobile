package com.example.eventapp.Model

data class ApiResponse<T>(
    val status: Int,
    val message: String?,
    val data: T?,
    val timestamp: String?
)
