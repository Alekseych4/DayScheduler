package com.open.day.dayscheduler.data.repository

import com.open.day.dayscheduler.data.dao.UserDao
import com.open.day.dayscheduler.data.entity.UserEntity
import com.open.day.dayscheduler.exception.NoSuchRowException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {
    suspend fun initLocalUser() {
        withContext(Dispatchers.IO) {
            if (userDao.getLocalUser() == null) {
                userDao.insertUser(UserEntity(null, null, true))
            }
        }
    }

    suspend fun getLocalUser(): UserEntity {
        return withContext(Dispatchers.IO) {
            return@withContext userDao.getLocalUser()
        } ?: throw NoSuchRowException("No local user in DB")
    }
}