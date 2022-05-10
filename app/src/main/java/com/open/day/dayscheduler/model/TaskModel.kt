package com.open.day.dayscheduler.model

import java.util.*

data class TaskModel(val id: UUID, var title: String, val tags: List<String>, var startTime: Long,
                     var isReminder: Boolean, var endTime: Long, var description: String)
