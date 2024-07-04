package pl.artur.tasknest.states

import pl.artur.tasknest.data.Task

class CompletedState : TaskState {
    override fun setState(task: Task, showMessage: (String) -> Unit) {
        task.state = this
        showMessage("Task ${task.title} is now in Completed state")
    }
}
