package pl.artur.tasknest.strategies

import pl.artur.tasknest.data.Task


class CompletedStateSortingStrategy : SortingStrategy {
    override fun sort(tasks: List<Task>): List<Task> {
        return tasks.sortedBy { it.state::class.simpleName }
    }
}
