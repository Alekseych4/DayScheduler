package com.open.day.dayscheduler.data.converter.dtoModelConverters

import com.open.day.dayscheduler.model.TaskModel
import com.open.day.dayscheduler.model.UserModel
import com.open.day.dayscheduler.network.model.TaskDto
import java.util.stream.Collectors

class TaskModelToTaskDto {
    companion object {
        fun convert(taskModel: TaskModel, localUser: UserModel): TaskDto {
            return TaskDto(
                taskModel.id!!,
                localUser.id!!,
                taskModel.title,
                taskModel.startTime,
                taskModel.endTime,
                taskModel.isReminder,
                taskModel.isAnchor,
                taskModel.tag?.toString(),
                taskModel.description,
                taskModel.addedUserIds.stream()
                    .map { UserModelToUserDto.convert(it) }
                    .collect(Collectors.toList())
            )
        }
    }
}