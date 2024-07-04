package pl.artur.tasknest.data

import androidx.room.TypeConverter
import pl.artur.tasknest.states.*
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromState(value: String): TaskState {
        return when (value) {
            "NewState" -> NewState()
            "InProgressState" -> InProgressState()
            "CompletedState" -> CompletedState()
            else -> throw IllegalArgumentException("Unknown state")
        }
    }

    @TypeConverter
    fun stateToString(state: TaskState): String {
        return state::class.simpleName ?: "UnknownState"
    }
}