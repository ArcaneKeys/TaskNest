package pl.artur.tasknest.data

import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Insert
    fun insertTask(task: Task): Long

    @Delete
    fun deleteTask(task: Task)

    @Update
    fun updateTask(task: Task)
}