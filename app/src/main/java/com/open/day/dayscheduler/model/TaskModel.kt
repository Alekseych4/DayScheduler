package com.open.day.dayscheduler.model

import java.util.UUID

private const val EMPTY_STRING = ""

data class TaskModel(
    var title: String,
    val tag: Tag?,
    var startTime: Long,
    var isReminder: Boolean,
    var isAnchor: Boolean,
    var endTime: Long?,
    var description: String?,
    val addedUserIds: MutableSet<UserModel>,
    var isTaskLocal: Boolean,
    val id: UUID = UUID.randomUUID()
) {
    constructor(startTime: Long, addedUserIds: MutableSet<UserModel>, isTaskLocal: Boolean) : this(
        EMPTY_STRING,
        null,
        startTime,
        false,
        false,
        null,
        EMPTY_STRING,
        addedUserIds,
        isTaskLocal
    )
}
