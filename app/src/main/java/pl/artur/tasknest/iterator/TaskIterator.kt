package pl.artur.tasknest.iterator

import pl.artur.tasknest.data.Task

interface TaskIterator {
    fun hasNext(): Boolean
    fun next(): Task
}
