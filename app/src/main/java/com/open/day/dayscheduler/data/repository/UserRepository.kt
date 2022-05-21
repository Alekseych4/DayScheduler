package com.open.day.dayscheduler.data.repository

import com.open.day.dayscheduler.data.dao.UserDao
import com.open.day.dayscheduler.data.entity.UserEntity
import com.open.day.dayscheduler.exception.NoSuchRowException
import com.open.day.dayscheduler.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.lang.Exception
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun getLocalUser(): UserModel {
        val user = userDao.getLocalUser() ?: throw NoSuchRowException("No local user in DB")
        return UserModel(user.id, user.name, user.surname, user.isLocalUser)
    }

    suspend fun updateUser(userModel: UserModel) {
        userModel.id?.let {
            userDao.updateUser(UserEntity(userModel.name, userModel.surname, userModel.isLocalUser, userModel.id))
        }
    }

    suspend fun insertUser(userModel: UserModel) {
        userModel.id?.let {
            userDao.insertUser(UserEntity(userModel.name, userModel.surname, userModel.isLocalUser, userModel.id))
        }
    }
}