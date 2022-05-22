package com.open.day.dayscheduler.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "surname") val surname: String?,
    @ColumnInfo(name = "is_local_user") val isLocalUser: Boolean?,
    @PrimaryKey val id: UUID = UUID.randomUUID()
    )
