package com.arekalov.todoistreminder.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.arekalov.todoistreminder.presentation.settings.SettingsScreen
import com.arekalov.todoistreminder.presentation.tasks.TasksScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "tasks"
    ) {
        composable("tasks") {
            TasksScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}

