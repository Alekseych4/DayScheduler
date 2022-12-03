package com.open.day.dayscheduler.network.model

import java.util.UUID

data class UserDto(
    val id: UUID,
    val name: String,
    val surname: String,
    val email: String,
    val idInService: String,
    val profileImageUrl: String
)
