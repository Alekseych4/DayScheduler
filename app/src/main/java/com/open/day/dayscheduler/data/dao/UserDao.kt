package com.open.day.dayscheduler.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.open.day.dayscheduler.data.entity.UserEntity
import java.util.UUID

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteById(userId: UUID)

    @Query("SELECT * FROM users WHERE is_local_user = 1 LIMIT 1")
    suspend fun getLocalUser(): UserEntity?
}