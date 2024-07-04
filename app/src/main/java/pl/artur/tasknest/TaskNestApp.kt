package pl.artur.tasknest

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.artur.tasknest.manager.TaskManager
import pl.artur.tasknest.ui.screens.HomeScreen
import pl.artur.tasknest.ui.screens.TaskDetailsScreen
import pl.artur.tasknest.ui.SnackbarManager

@Composable
fun TaskNestApp(snackbarManager: SnackbarManager, taskManager: TaskManager) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController, snackbarManager = snackbarManager, taskManager = taskManager)
        }
        composable(
            "task_details/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            val task = taskManager.getSortedTasks().find { it.id == taskId }
            if (task != null) {
                TaskDetailsScreen(navController = navController, snackbarManager = snackbarManager, task = task, taskManager = taskManager)
            }
        }
    }
}