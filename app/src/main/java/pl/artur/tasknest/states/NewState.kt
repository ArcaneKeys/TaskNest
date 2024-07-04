package pl.artur.tasknest.states

import pl.artur.tasknest.data.Task

class NewState : TaskState {
    override fun setState(task: Task, showMessage: (String) -> Unit) {
        task.state = this
        showMessage("Task ${task.title} is now in New state")
    }
}
