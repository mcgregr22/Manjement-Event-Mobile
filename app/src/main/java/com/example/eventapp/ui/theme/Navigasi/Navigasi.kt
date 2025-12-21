package com.example.eventapp.ui.theme.Navigasi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eventapp.ui.theme.Calendar.CalendarPage
import com.example.eventapp.ui.theme.screen.AddEventScreen
import com.example.eventapp.ui.theme.screen.EditEventScreen
import com.example.eventapp.ui.theme.screen.EventDetailScreen
import com.example.eventapp.ui.theme.screen.EventListScreen
import com.example.eventapp.ui.theme.viewmodel.EventViewModel

@Composable
fun Navigasi(viewModel: EventViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "event_list"
    ) {
        composable("event_list") {
            EventListScreen(
                viewModel = viewModel,
                onEventClick = { eventId ->
                    navController.navigate("event_detail/$eventId")
                },
                onAddClick = {
                    navController.navigate("add_event")
                },
                onCalendarClick = {
                    navController.navigate("calendar")
                }
            )
        }

        composable("add_event") {
            AddEventScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(
            route = "edit_event/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditEventScreen(
                navController = navController,
                eventId = id,
                viewModel = viewModel
            )
        }

        composable(
            route = "event_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EventDetailScreen(
                eventId = id,
                viewModel = viewModel,
                onEdit = { navController.navigate("edit_event/$it") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("calendar") {
            CalendarPage(
                events = viewModel.events.collectAsState().value,
                onSelectDate = { dateStr ->
                    viewModel.setSelectedDate(dateStr)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
