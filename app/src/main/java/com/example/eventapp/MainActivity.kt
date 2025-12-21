package com.example.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventapp.ui.theme.EventappTheme
import com.example.eventapp.ui.theme.Navigasi.Navigasi
import com.example.eventapp.ui.theme.viewmodel.EventViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventappTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val vm: EventViewModel = viewModel()
                    Navigasi(viewModel = vm)
                }
            }
        }
    }
}
