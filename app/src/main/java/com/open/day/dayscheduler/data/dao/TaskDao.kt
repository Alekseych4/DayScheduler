package com.open.day.dayscheduler.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.open.day.dayscheduler.data.AppDatabase
import com.open.day.dayscheduler.data.entity.TaskEntity
import com.open.day.dayscheduler.data.entity.TasksWithUsers
import com.open.day.dayscheduler.data.entity.UsersTasksEntityMTM
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class TaskDao(private val database: AppDatabase) {
    private val usersTasksDaoMTM: UsersTasksDaoMTM = database.usersTasksDaoMTM()

    @Insert
    abstract suspend fun insertTask(task: TaskEntity)

    @Update
    abstract suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    abstract suspend fun deleteById(taskId: UUID)

    //TODO: this query is probably wrong
    @Query("SELECT * FROM tasks " +
            "WHERE (user_id = :userId AND start_time >= :start AND start_time < :end AND end_time <= :end AND end_time > :start)" +
            "ORDER BY start_time ASC")
    abstract fun getByTimeRange(start: Long, end: Long, userId: UUID): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks " +
            "WHERE (user_id = :userId AND start_time >= :start AND start_time < :end AND end_time <= :end AND end_time > :start)" +
            "ORDER BY start_time ASC")
    abstract suspend fun getByTimeRangeSuspendable(start: Long, end: Long, userId: UUID): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    abstract suspend fun getById(taskId: UUID): TaskEntity?

    @Transaction
    open suspend fun getTaskWithUsersById(taskId: UUID): TasksWithUsers? {
        return usersTasksDaoMTM.getTaskWithUsers(taskId)[0]
    }

    @Transaction
    @Query("SELECT * FROM tasks")
    abstract suspend fun getTasksWithUsers(): List<TasksWithUsers>

    @Transaction
    open suspend fun insertTaskMTM(task: TaskEntity, userIds: Set<UUID>) {
        insertTask(task)
        userIds.forEach {
            usersTasksDaoMTM.insert(UsersTasksEntityMTM(it, task.id))
        }
    }

    @Transaction
    open suspend fun updateTaskMTM(task: TaskEntity, userIds: Set<UUID>) {
        updateTask(task)
        userIds.forEach {
            usersTasksDaoMTM.update(UsersTasksEntityMTM(it, task.id))
        }
    }
}