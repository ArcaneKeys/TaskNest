package pl.artur.tasknest.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.artur.tasknest.states.TaskState
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var description: String,
    var priority: Int,
    var category: String,
    var dueDate: Date,
    var state: TaskState
) {
    init {
        require(category.isNotBlank()) { "Category cannot be empty" }
    }

    fun getDetails(): String {
        return "Task(id=$id, title='$title', description='$description', priority=$priority, category='$category', dueDate=$dueDate, state=${state::class.simpleName})"
    }
}
