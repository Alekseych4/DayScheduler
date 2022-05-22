package com.open.day.dayscheduler.model

import java.util.UUID

data class TaskModel(
    val id: UUID?,
    var title: String,
    val tag: Tag?,
    var startTime: Long,
    var isReminder: Boolean,
    var isAnchor: Boolean,
    var endTime: Long?,
    var description: String?,
    val addedUserIds: MutableSet<UserModel>,
    var isTaskLocal: Boolean
)
