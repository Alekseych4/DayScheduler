package com.open.day.dayscheduler.data.converter.dtoModelConverters

import com.open.day.dayscheduler.model.Tag
import com.open.day.dayscheduler.model.TaskModel
import com.open.day.dayscheduler.network.model.TaskDto
import java.util.stream.Collectors

class TaskDtoToTaskModel {
    companion object {
        fun convert(taskDto: TaskDto):TaskModel {
            return TaskModel(
                taskDto.id,
                taskDto.title,
                taskDto.tag?.let { Tag.valueOf(it) },
                taskDto.startTime,
                taskDto.isReminder,
                taskDto.isAnchor,
                taskDto.endTime,
                taskDto.description,
                taskDto.userDtoList.stream()
                    .map { UserDtoToUserModel.convert(it) }
                    .collect(Collectors.toList())
                    .toMutableList(),
                false
            )
        }
    }
}