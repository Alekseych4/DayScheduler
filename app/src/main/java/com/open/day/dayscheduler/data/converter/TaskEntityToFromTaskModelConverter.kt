package com.open.day.dayscheduler.data.converter

import com.open.day.dayscheduler.data.entity.TaskEntity
import com.open.day.dayscheduler.data.entity.TaskWithUsers
import com.open.day.dayscheduler.data.entity.UserEntity
import com.open.day.dayscheduler.model.Tag
import com.open.day.dayscheduler.model.TaskModel
import java.util.stream.Collectors

class TaskEntityToFromTaskModelConverter {
    companion object {
        fun convertTaskWithUsersToTaskModel(taskWithUsers: TaskWithUsers): TaskModel {
            return TaskModel(
                taskWithUsers.taskEntity.id,
                taskWithUsers.taskEntity.title,
                if (taskWithUsers.taskEntity.tag == null) null else Tag.valueOf(taskWithUsers.taskEntity.tag),
                taskWithUsers.taskEntity.startTime,
                taskWithUsers.taskEntity.isReminder,
                taskWithUsers.taskEntity.isAnchor,
                taskWithUsers.taskEntity.endTime,
                taskWithUsers.taskEntity.description,
                taskWithUsers.userEntities.stream()
                    .map { UserEntityToFromUserModelConverter.convertUserEntity(it) }
                    .collect(Collectors.toSet()),
                taskWithUsers.taskEntity.isTaskLocal
            )
        }

        fun convertTaskEntityToTaskModel(taskEntity: TaskEntity, userEntities: List<UserEntity> = listOf()): TaskModel {
            return TaskModel(
                taskEntity.id,
                taskEntity.title,
                if (taskEntity.tag == null) null else Tag.valueOf(taskEntity.tag),
                taskEntity.startTime,
                taskEntity.isReminder,
                taskEntity.isAnchor,
                taskEntity.endTime,
                taskEntity.description,
                userEntities.stream()
                    .map { UserEntityToFromUserModelConverter.convertUserEntity(it) }
                    .collect(Collectors.toSet()),
                taskEntity.isTaskLocal
            )
        }
    }
}