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
    @Query("SELECT * FROM tasks WHERE start_time >= :start AND end_time <= :end")
    fun getByTimeRange(start: Long, end: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getById(taskId: UUID): Flow<TaskEntity?>
}