package pl.artur.tasknest.iterator

import pl.artur.tasknest.data.Task

class ConcreteTaskIterator(private val tasks: List<Task>) : TaskIterator {
    private var position: Int = 0

    override fun hasNext(): Boolean {
        return position < tasks.size
    }

    override fun next(): Task {
        return tasks[position++]
    }
}
