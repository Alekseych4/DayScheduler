package com.open.day.dayscheduler.network.model

import java.util.UUID

data class TaskDto(
    val id: UUID,
    val userId: UUID,
    val title: String,
    val startTime: Long,
    val endTime: Long?,
    val isReminder: Boolean,
    val isAnchor: Boolean,
    val tag: String?,
    val description: String?,
    val userDtoList: List<UserDto>
)