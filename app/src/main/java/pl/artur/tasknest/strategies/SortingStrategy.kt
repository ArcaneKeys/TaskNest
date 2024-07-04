package pl.artur.tasknest.strategies

import pl.artur.tasknest.data.Task

interface SortingStrategy {
    fun sort(tasks: List<Task>): List<Task>
}
