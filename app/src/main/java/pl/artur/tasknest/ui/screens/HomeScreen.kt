package pl.artur.tasknest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import pl.artur.tasknest.data.Task
import pl.artur.tasknest.manager.TaskManager
import pl.artur.tasknest.states.NewState
import pl.artur.tasknest.strategies.CompletedStateSortingStrategy
import pl.artur.tasknest.strategies.DateSortingStrategy
import pl.artur.tasknest.strategies.NameSortingStrategy
import pl.artur.tasknest.strategies.PrioritySortingStrategy
import pl.artur.tasknest.ui.SnackbarHost
import pl.artur.tasknest.ui.SnackbarManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, snackbarManager: SnackbarManager, taskManager: TaskManager) {
    var tasks by remember { mutableStateOf(taskManager.getSortedTasks()) }
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val categories by remember { mutableStateOf(taskManager.getCategories()) }
    val updatedTasks = rememberUpdatedState(taskManager.getSortedTasks())

    LaunchedEffect(updatedTasks) {
        snapshotFlow { updatedTasks.value }
            .collect { updatedTasks ->
                tasks = updatedTasks
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Task List") },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                taskManager.setSortingStrategy(NameSortingStrategy())
                                tasks = taskManager.getSortedTasks()
                                expanded = false
                            },
                            text = { Text("Sort by Name") }
                        )
                        DropdownMenuItem(
                            onClick = {
                                taskManager.setSortingStrategy(DateSortingStrategy())
                                tasks = taskManager.getSortedTasks()
                                expanded = false
                            },
                            text = { Text("Sort by Date") }
                        )
                        DropdownMenuItem(
                            onClick = {
                                taskManager.setSortingStrategy(PrioritySortingStrategy())
                                tasks = taskManager.getSortedTasks()
                                expanded = false
                            },
                            text = { Text("Sort by Priority") }
                        )
                        DropdownMenuItem(
                            onClick = {
                                taskManager.setSortingStrategy(CompletedStateSortingStrategy())
                                tasks = taskManager.getSortedTasks()
                                expanded = false
                            },
                            text = { Text("Sort by Completed State") }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+")
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarManager = snackbarManager)
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            categories.forEach { category ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                items(tasks.filter { it.category == category }) { task ->
                    TaskItem(task = task, onClick = {
                        navController.navigate("task_details/${task.id}")
                    })
                }
            }
        }

        if (showDialog) {
            NewTaskDialog(
                onDismiss = { showDialog = false },
                onSave = { title, description, priority, category, dueDate ->
                    val newTask = Task(
                        id = 0,
                        title = title,
                        description = description,
                        priority = priority,
                        category = category,
                        dueDate = dueDate,
                        state = NewState()
                    )
                    coroutineScope.launch {
                        taskManager.addTask(newTask)
                        tasks = taskManager.getSortedTasks()
                        snackbarManager.showMessage("New task added")
                        showDialog = false
                    }
                },
                categories = categories
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, onClick: () -> Unit) {
    val priorityOptions = listOf("1 - Very High", "2 - High", "3 - Medium", "4 - Low", "5 - Very Low")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Priority: ${priorityOptions[task.priority - 1]}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Category: ${task.category}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Due: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(task.dueDate)}", style = MaterialTheme.typography.bodySmall)
            Text(text = "State: ${task.state::class.simpleName}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskDialog(onDismiss: () -> Unit, onSave: (String, String, Int, String, Date) -> Unit, categories: List<String>) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(1) }
    var category by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(Date()) }
    val priorityOptions = listOf("1 - Very High", "2 - High", "3 - Medium", "4 - Low", "5 - Very Low")
    var priorityExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showCategoryError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "New Task") },
        text = {
            Column {
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
                                    showCategoryError = false
                                }
                            )
                        }
                    }
                }
                if (showCategoryError) {
                    Text(text = "Category cannot be empty", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(8.dp))
                DatePicker(
                    selectedDate = dueDate,
                    onDateChange = { newDate -> dueDate = newDate }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (category.isNotBlank()) {
                    onSave(title, description, priority, category, dueDate)
                } else {
                    showCategoryError = true
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DatePicker(selectedDate: Date, onDateChange: (Date) -> Unit) {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    var dateText by remember { mutableStateOf(dateFormatter.format(selectedDate)) }

    TextField(
        value = dateText,
        onValueChange = {
            dateText = it
            try {
                val parsedDate = dateFormatter.parse(it)
                if (parsedDate != null) {
                    onDateChange(parsedDate)
                }
            } catch (e: ParseException) {
                // Błąd analizy daty, ignoruj błąd
            }
        },
        label = { Text("Due Date") }
    )
}
