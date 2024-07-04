package pl.artur.tasknest.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.artur.tasknest.data.Task
import pl.artur.tasknest.data.TaskDao
import pl.artur.tasknest.data.TaskList
import pl.artur.tasknest.iterator.TaskIterator
import pl.artur.tasknest.strategies.PrioritySortingStrategy
import pl.artur.tasknest.strategies.SortingStrategy

class TaskManager(private val taskList: TaskList, private val taskDao: TaskDao) {
    private var sortingStrategy: SortingStrategy = PrioritySortingStrategy()
    private val categories = mutableSetOf<String>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val tasks = taskDao.getAllTasks()
            tasks.forEach { task ->
                taskList.addTask(task)
                categories.add(task.category)
            }
        }
    }

    fun addTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskList.addTask(task)
            taskDao.insertTask(task)
            categories.add(task.category)
        }
    }

    fun removeTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskList.removeTask(task)
            taskDao.deleteTask(task)
        }
    }

    fun setSortingStrategy(strategy: SortingStrategy) {
        this.sortingStrategy = strategy
    }

    fun getSortedTasks(): List<Task> {
        val tasks = taskList.getTaskIterator().toList()
        return sortingStrategy.sort(tasks)
    }

    private fun TaskIterator.toList(): List<Task> {
        val taskList = mutableListOf<Task>()
        while (this.hasNext()) {
            taskList.add(this.next())
        }
        return taskList
    }

    fun updateTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.updateTask(task)
        }
    }

    fun getCategories(): List<String> {
        return categories.toList()
    }
}
