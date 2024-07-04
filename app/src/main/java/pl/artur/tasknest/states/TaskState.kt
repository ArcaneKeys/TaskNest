package pl.artur.tasknest.states

import pl.artur.tasknest.data.Task

interface TaskState {
    fun setState(task: Task, showMessage: (String) -> Unit)
}
