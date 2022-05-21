package com.open.day.dayscheduler.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.open.day.dayscheduler.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: UUID)

    //TODO: this query is probably wrong
    @Query("SELECT * FROM tasks " +
            "WHERE (user_id = :userId AND start_time >= :start AND start_time < :end AND end_time <= :end AND end_time > :start)" +
            "ORDER BY start_time ASC")
    fun getByTimeRange(start: Long, end: Long, userId: UUID): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks " +
            "WHERE (user_id = :userId AND start_time >= :start AND start_time < :end AND end_time <= :end AND end_time > :start)" +
            "ORDER BY start_time ASC")
    suspend fun getByTimeRangeSuspendable(start: Long, end: Long, userId: UUID): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getById(taskId: UUID): TaskEntity?
}