package com.open.day.dayscheduler.data.repository

import com.open.day.dayscheduler.data.converter.UserEntityToFromUserModelConverter
import com.open.day.dayscheduler.data.dao.UserDao
import com.open.day.dayscheduler.data.entity.UserEntity
import com.open.day.dayscheduler.exception.NoSuchRowException
import com.open.day.dayscheduler.model.UserModel
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun getLocalUser(): UserModel {
        val user = userDao.getLocalUser() ?: throw NoSuchRowException("No local user in DB")
        return UserEntityToFromUserModelConverter.convertUserEntity(user)
    }

    suspend fun updateUser(userModel: UserModel) {
        userModel.id?.let {
            userDao.updateUser(UserEntityToFromUserModelConverter.convertUserModel(userModel, it))
        }
    }

    suspend fun insertUser(userModel: UserModel) {
        userDao.insertUser(UserEntityToFromUserModelConverter.convertUserModel(userModel))
    }

    suspend fun getUserByEmail(email: String): UserModel {
        val userEntity = userDao.getUserByEmail(email)
        if (userEntity == null) {
            //TODO: add sync with backend
            throw NoSuchRowException("User not found")
        }

        return UserEntityToFromUserModelConverter.convertUserEntity(userEntity)
    }
}