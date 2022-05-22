package com.open.day.dayscheduler.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import java.util.UUID

@Entity(tableName = "user_task_mtm", primaryKeys = ["user_id", "task_id"])
data class UsersTasksEntityMTM(
    @ColumnInfo(name = "user_id") val userId: UUID,
    @ColumnInfo(name = "task_id") val taskId: UUID
)

data class TaskWithUsers(
    @Embedded val taskEntity: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
    )
    val userEntities: List<UserEntity>
)

data class UserWithTasks(
    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
    )
    val taskEntities: List<TaskEntity>
)

data class TasksWithUsers(
    @Embedded val taskEntity: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(value = UsersTasksEntityMTM::class, parentColumn = "user_id", entityColumn = "task_id")
    )
    val userEntities: List<UserEntity>
)

data class UsersWithTasks(
    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(value = UsersTasksEntityMTM::class, parentColumn = "user_id", entityColumn = "task_id")
    )
    val taskEntities: List<TaskEntity>
)