package pl.artur.tasknest.data

import pl.artur.tasknest.iterator.ConcreteTaskIterator
import pl.artur.tasknest.iterator.TaskIterator

class TaskList {
    private val tasks: MutableList<Task> = mutableListOf()

    fun addTask(task: Task) {
        tasks.add(task)
    }

    fun removeTask(task: Task) {
        tasks.remove(task)
    }

    fun getTaskIterator(): TaskIterator {
        return ConcreteTaskIterator(tasks)
    }
}
