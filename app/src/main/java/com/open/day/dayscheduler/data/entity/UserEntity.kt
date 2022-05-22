package com.open.day.dayscheduler.data.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "surname") val surname: String?,
    //FIXME: probably should change to non-null
    @ColumnInfo(name = "is_local_user") val isLocalUser: Boolean?,
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "is_signed_in") val isSignedIn: Boolean = false,
    @ColumnInfo(name = "id_in_service") val idInService: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "profile_image_uri") val profileImageUri: String? = null
)
