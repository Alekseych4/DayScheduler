package com.open.day.dayscheduler.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: UUID,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "end_time") val endTime: Long?,
    @ColumnInfo(name = "is_reminder") val isReminder: Boolean,
    @ColumnInfo(name = "is_anchor") val isAnchor: Boolean,
    @ColumnInfo(name = "tag") val tag: String?,
    @ColumnInfo(name = "user_id") val userId: UUID,
    @ColumnInfo(name = "is_task_local") val isTaskLocal: Boolean = true
)
