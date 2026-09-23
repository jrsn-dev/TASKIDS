package com.taskids.app.data.local

import androidx.room.TypeConverter
import com.taskids.app.domain.model.RewardType
import com.taskids.app.domain.model.RoutinePeriod
import com.taskids.app.domain.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): String = value.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus =
        runCatching { TaskStatus.valueOf(value) }.getOrDefault(TaskStatus.PENDING)

    @TypeConverter
    fun fromRoutinePeriod(value: RoutinePeriod): String = value.name

    @TypeConverter
    fun toRoutinePeriod(value: String): RoutinePeriod =
        runCatching { RoutinePeriod.valueOf(value) }.getOrDefault(RoutinePeriod.ANYTIME)

    @TypeConverter
    fun fromRewardType(value: RewardType): String = value.name

    @TypeConverter
    fun toRewardType(value: String): RewardType =
        runCatching { RewardType.valueOf(value) }.getOrDefault(RewardType.CUSTOM)
}
