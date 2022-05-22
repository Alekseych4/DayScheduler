package com.open.day.dayscheduler.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.open.day.dayscheduler.data.entity.TasksWithUsers
import com.open.day.dayscheduler.data.entity.UsersTasksEntityMTM
import java.util.UUID

@Dao
interface UsersTasksDaoMTM {
    @Insert
    suspend fun insert(usersTasksEntityMTM: UsersTasksEntityMTM)

    @Update
    suspend fun update(usersTasksEntityMTM: UsersTasksEntityMTM)

    @Transaction
    @Query("SELECT * FROM tasks WHERE tasks.id = :taskId")
    suspend fun getTaskWithUsers(taskId: UUID): List<TasksWithUsers?>

    @Transaction
    @Query("SELECT * FROM tasks WHERE (" +
            "tasks.user_id = :userId AND tasks.start_time >= :start AND tasks.start_time < :end AND" +
            " tasks.end_time <= :end AND tasks.end_time > :start)" +
            "ORDER BY tasks.start_time ASC")
    suspend fun getTasksWithUsers(start: Long, end: Long, userId: UUID): List<TasksWithUsers>
}