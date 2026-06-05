package com.example.data.local

import androidx.room.TypeConverter
import com.example.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): TaskStatus {
        return try {
            TaskStatus.valueOf(value)
        } catch (e: Exception) {
            TaskStatus.PENDING
        }
    }
}
