package pl.artur.tasknest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.room.Room
import pl.artur.tasknest.data.AppDatabase
import pl.artur.tasknest.data.TaskList
import pl.artur.tasknest.manager.TaskManager
import pl.artur.tasknest.ui.SnackbarHost
import pl.artur.tasknest.ui.rememberSnackbarManager
import pl.artur.tasknest.ui.theme.TaskNestTheme

class MainActivity : ComponentActivity() {
    private lateinit var taskManager: TaskManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)

        val taskDao = db.taskDao()
        val taskList = TaskList()
        taskManager = TaskManager(taskList, taskDao)

        setContent {
            TaskNestTheme {
                val coroutineScope = rememberCoroutineScope()
                val snackbarManager = rememberSnackbarManager(coroutineScope)
                Surface(color = MaterialTheme.colorScheme.background) {
                    SnackbarHost(snackbarManager = snackbarManager)
                    TaskNestApp(snackbarManager, taskManager)
                }
            }
        }
    }
}
