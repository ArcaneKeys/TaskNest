package pl.artur.tasknest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pl.artur.tasknest.data.Task
import pl.artur.tasknest.manager.TaskManager
import pl.artur.tasknest.states.CompletedState
import pl.artur.tasknest.states.InProgressState
import pl.artur.tasknest.ui.SnackbarManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    navController: NavHostController,
    snackbarManager: SnackbarManager,
    task: Task,
    taskManager: TaskManager
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var category by remember { mutableStateOf(task.category) }
    var dueDate by remember { mutableStateOf(task.dueDate) }
    val priorityOptions = listOf("1 - Very High", "2 - High", "3 - Medium", "4 - Low", "5 - Very Low")
    var priorityExpanded by remember { mutableStateOf(false) }
    val categories by remember { mutableStateOf(taskManager.getCategories()) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Task Details", style = MaterialTheme.typography.titleLarge)
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(text = "Title") }
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(text = "Description") }
        )
        ExposedDropdownMenuBox(
            expanded = priorityExpanded,
            onExpandedChange = { priorityExpanded = !priorityExpanded }
        ) {
            TextField(
                readOnly = true,
                value = priorityOptions[priority - 1],
                onValueChange = { },
                label = { Text("Priority") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = priorityExpanded,
                onDismissRequest = { priorityExpanded = false }
            ) {
                priorityOptions.forEachIndexed { index, label ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            priority = index + 1
                            priorityExpanded = false
                        }
                    )
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            TextField(
                value = category,
                onValueChange = { category = it },
                label = { Text(text = "Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            category = label
                            categoryExpanded = false
                        }
                    )
                }
            }
        }
        DatePicker(
            selectedDate = dueDate,
            onDateChange = { newDate -> dueDate = newDate }
        )

        Button(onClick = {
            task.title = title
            task.description = description
            task.priority = priority
            task.category = category
            task.dueDate = dueDate
            taskManager.updateTask(task)
            snackbarManager.showMessage("Task updated")
        }) {
            Text(text = "Save")
        }

        Button(onClick = {
            task.state = InProgressState()
            taskManager.updateTask(task)
            snackbarManager.showMessage("Task is now in progress")
        }) {
            Text(text = "Mark In Progress")
        }

        Button(onClick = {
            task.state = CompletedState()
            taskManager.updateTask(task)
            snackbarManager.showMessage("Task is now completed")
        }) {
            Text(text = "Mark Completed")
        }

        Button(onClick = {
            navController.navigateUp()
        }) {
            Text(text = "Back")
        }
    }
}
