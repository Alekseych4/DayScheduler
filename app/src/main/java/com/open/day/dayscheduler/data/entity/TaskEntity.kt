package com.open.day.dayscheduler.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.open.day.dayscheduler.model.Tag
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "end_time") val endTime: Long?,
    @ColumnInfo(name = "is_reminder") val isReminder: Boolean,
    @ColumnInfo(name = "is_anchor") val isAnchor: Boolean,
    @ColumnInfo(name = "tag") val tag: String?,
    @ColumnInfo(name = "user_id") val userId: UUID,
    @PrimaryKey val id: UUID = UUID.randomUUID()
)
