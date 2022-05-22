package com.open.day.dayscheduler.model

import java.util.UUID

data class UserModel(
    val id: UUID?,
    var name: String?,
    var surname: String?,
    val isLocalUser: Boolean?,
    var isSignedIn: Boolean = false,
    var email: String? = null,
    val idInService: String? = null,
    val profileImageUri: String? = null
)
