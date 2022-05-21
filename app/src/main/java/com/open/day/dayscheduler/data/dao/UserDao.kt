package com.open.day.dayscheduler.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.open.day.dayscheduler.data.entity.UserEntity
import java.util.UUID

@Dao
abstract class UserDao {
    @Insert
    abstract suspend fun insertUser(user: UserEntity)

    @Update
    abstract suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    abstract suspend fun deleteById(userId: UUID)

    @Query("SELECT * FROM users WHERE is_local_user = 1 LIMIT 1")
    abstract suspend fun getLocalUserIfExists(): UserEntity?

    @Transaction
    open suspend fun getLocalUser(): UserEntity? {
        val entity = getLocalUserIfExists()
        if (entity == null)
            insertUser(UserEntity(null, null, true))
        return getLocalUserIfExists()
    }
}