package pl.artur.tasknest.strategies

import pl.artur.tasknest.data.Task

class DateSortingStrategy : SortingStrategy {
    override fun sort(tasks: List<Task>): List<Task> {
        return tasks.sortedBy { it.dueDate }
    }
}
