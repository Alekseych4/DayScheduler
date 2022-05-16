package com.open.day.dayscheduler.data.repository

import android.icu.util.DateInterval
import com.open.day.dayscheduler.data.AppDatabase
import com.open.day.dayscheduler.data.dao.TaskDao
import com.open.day.dayscheduler.data.dao.UserDao
import com.open.day.dayscheduler.data.entity.TaskEntity
import com.open.day.dayscheduler.model.TaskModel
import dagger.Component
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor (
    private val taskDao: TaskDao,
    private val userRepository: UserRepository
) {

    suspend fun saveTask(taskModel: TaskModel) {
        val user = userRepository.getLocalUser()

        TaskEntity(
            taskModel.title, taskModel.description, taskModel.startTime, taskModel.endTime,
            taskModel.isReminder, taskModel.isAnchor, taskModel.tag, user.id
        )
            .also { taskDao.insertTask(it) }
    }

    suspend fun deleteTaskById(id: UUID) {
        taskDao.deleteById(id)
    }

    suspend fun getTasks(interval: DateInterval): Flow<List<TaskModel>> {
        val user = userRepository.getLocalUser()

        return taskDao.getByTimeRange(interval.fromDate, interval.toDate)
            .map {
                it.map { entity -> TaskModel(
                    entity.id, entity.title, entity.tag, entity.startTime, entity.isReminder,
                    entity.isAnchor, entity.endTime, entity.description
                ) }
            }
    }

    suspend fun getTaskById(id: UUID): TaskModel? {
        val entity = taskDao.getById(id)
        return if (entity == null) {
            null
        } else {
            TaskModel(entity.id, entity.title, entity.tag, entity.startTime, entity.isReminder,
                entity.isAnchor, entity.endTime, entity.description)
        }
    }
}