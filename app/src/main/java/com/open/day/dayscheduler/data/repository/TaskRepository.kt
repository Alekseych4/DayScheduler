package com.open.day.dayscheduler.data.repository

import android.icu.util.DateInterval
import com.open.day.dayscheduler.data.converter.TaskEntityToFromTaskModelConverter.Companion.convertTaskEntityToTaskModel
import com.open.day.dayscheduler.data.converter.TaskEntityToFromTaskModelConverter.Companion.convertTaskWithUsersToTaskModel
import com.open.day.dayscheduler.data.dao.TaskDao
import com.open.day.dayscheduler.data.dao.UsersTasksDaoMTM
import com.open.day.dayscheduler.data.entity.TaskEntity
import com.open.day.dayscheduler.data.entity.TaskWithUsers
import com.open.day.dayscheduler.model.Tag
import com.open.day.dayscheduler.model.TaskModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import com.open.day.dayscheduler.model.UserModel
import java.util.stream.Collectors

@Singleton
class TaskRepository @Inject constructor (
    private val taskDao: TaskDao,
    private val userRepository: UserRepository,
    private val daoMTM: UsersTasksDaoMTM
) {

    suspend fun saveTask(taskModel: TaskModel) {
        val user = userRepository.getLocalUser()

        if (!user.isSignedIn || taskModel.isTaskLocal) {
            saveLocalTask(taskModel, user.id!!)
            return
        }

        if (taskModel.id == null) {
            insertSharedTask(taskModel, user!!)
        } else {
            updateSharedTask(taskModel, user!!)
        }
    }

    private suspend fun saveLocalTask(taskModel: TaskModel, localUserId: UUID) {
        if (taskModel.id == null) {
            TaskEntity(
                taskModel.title, taskModel.description, taskModel.startTime, taskModel.endTime,
                taskModel.isReminder, taskModel.isAnchor, taskModel.tag?.name, localUserId, true
            )
                .also { taskDao.insertTask(it) }
        } else {
            TaskEntity(
                taskModel.title, taskModel.description, taskModel.startTime, taskModel.endTime,
                taskModel.isReminder, taskModel.isAnchor, taskModel.tag?.name, localUserId, true, taskModel.id
            )
                .also { taskDao.updateTask(it) }
        }
    }

    private suspend fun insertSharedTask(taskModel: TaskModel, localUser: UserModel) {
        val taskEntity = TaskEntity(
            taskModel.title, taskModel.description, taskModel.startTime, taskModel.endTime,
            taskModel.isReminder, taskModel.isAnchor, taskModel.tag?.name, localUser.id!!, taskModel.isTaskLocal
        )

        taskModel.addedUserIds.add(localUser)

        val userIds: MutableSet<UUID> = mutableSetOf()
        taskModel.addedUserIds.forEach{ user ->
            user.id?.let { userIds.add(it) }
        }

        taskDao.insertTaskMTM(taskEntity, userIds)
    }

    private suspend fun updateSharedTask(taskModel: TaskModel, localUser: UserModel) {
        if (taskModel.id != null) {
            val taskEntity = TaskEntity(
                taskModel.title,
                taskModel.description,
                taskModel.startTime,
                taskModel.endTime,
                taskModel.isReminder,
                taskModel.isAnchor,
                taskModel.tag?.name,
                localUser.id!!,
                taskModel.isTaskLocal,
                taskModel.id
            )

            taskModel.addedUserIds.add(localUser)

            val userIds: MutableSet<UUID> = mutableSetOf()
            taskModel.addedUserIds.forEach{ user ->
                    user.id?.let { userIds.add(it) }
            }

            taskDao.updateTaskMTM(taskEntity, userIds)
        }
    }

    suspend fun deleteTaskById(id: UUID) {
        taskDao.deleteById(id)
    }

    suspend fun getTasksList(interval: DateInterval): List<TaskModel> {
        val localUser = userRepository.getLocalUser()

        return if (localUser.isSignedIn) {
            //FIXME: possibly only non local tasks will be returned
            val tasksWithUsers = daoMTM.getTasksWithUsers(interval.fromDate, interval.toDate, localUser.id!!)
            tasksWithUsers.stream()
                .map {
                    convertTaskWithUsersToTaskModel(TaskWithUsers(it.taskEntity, it.userEntities))
                }
                .collect(Collectors.toList())

        } else {
            taskDao.getByTimeRangeSuspendable(interval.fromDate, interval.toDate, localUser.id!!)
                .map {
                    convertTaskEntityToTaskModel(it)
                }
        }
    }

    suspend fun getTaskById(id: UUID): TaskModel? {
        val localUser = userRepository.getLocalUser()

        if (localUser.isSignedIn) {
            //FIXME: possibly only non local tasks will be returned
            val taskWithUsers = taskDao.getTaskWithUsersById(id)

            if (taskWithUsers != null)
                return convertTaskWithUsersToTaskModel(TaskWithUsers(taskWithUsers.taskEntity, taskWithUsers.userEntities))

        } else {
            val entity = taskDao.getById(id)

            if (entity != null) {
                return convertTaskEntityToTaskModel(entity)
            }
        }

        return null
    }
}