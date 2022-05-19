package com.open.day.dayscheduler.data.repository

import android.icu.util.DateInterval
import com.open.day.dayscheduler.data.dao.TaskDao
import com.open.day.dayscheduler.data.entity.TaskEntity
import com.open.day.dayscheduler.model.Tag
import com.open.day.dayscheduler.model.TaskModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

        if (taskModel.id == null) {
            TaskEntity(
                taskModel.title, taskModel.description, taskModel.startTime, taskModel.endTime,
                taskModel.isReminder, taskModel.isAnchor, taskModel.tag?.name, user.id
            )
                .also { taskDao.insertTask(it) }
        } else {
            TaskEntity(
                taskModel.title, taskModel.description, taskModel.startTime, taskModel.endTime,
                taskModel.isReminder, taskModel.isAnchor, taskModel.tag?.name, user.id, taskModel.id
            )
                .also { taskDao.updateTask(it) }
        }
    }

    suspend fun deleteTaskById(id: UUID) {
        taskDao.deleteById(id)
    }

    suspend fun getTasks(interval: DateInterval): Flow<List<TaskModel>> {
        val user = userRepository.getLocalUser()

        return taskDao.getByTimeRange(interval.fromDate, interval.toDate)
            .map {
                it.map { entity -> TaskModel(
                    entity.id, entity.title,
                    if (entity.tag == null) null else Tag.valueOf(entity.tag),
                    entity.startTime, entity.isReminder,
                    entity.isAnchor, entity.endTime, entity.description
                ) }
            }
    }

    suspend fun getTasksList(interval: DateInterval): List<TaskModel> {
        val user = userRepository.getLocalUser()

        return taskDao.getByTimeRangeSuspendable(interval.fromDate, interval.toDate)
            .map {
                TaskModel(
                    it.id, it.title,
                    if (it.tag == null) null else Tag.valueOf(it.tag),
                    it.startTime, it.isReminder,
                    it.isAnchor, it.endTime, it.description
                )
            }
    }

    suspend fun getTaskById(id: UUID): TaskModel? {
        val entity = taskDao.getById(id)
        return if (entity == null) {
            null
        } else {
            TaskModel(entity.id, entity.title,
                if (entity.tag == null) null else Tag.valueOf(entity.tag),
                entity.startTime, entity.isReminder,
                entity.isAnchor, entity.endTime, entity.description)
        }
    }
}