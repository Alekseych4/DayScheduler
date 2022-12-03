package com.open.day.dayscheduler.data.repository

import com.open.day.dayscheduler.data.converter.dtoModelConverters.UserDtoToUserModel
import com.open.day.dayscheduler.data.converter.modelEntityConverters.UserEntityToFromUserModel
import com.open.day.dayscheduler.data.dao.UserDao
import com.open.day.dayscheduler.exception.NetworkException
import com.open.day.dayscheduler.exception.NoSuchRowException
import com.open.day.dayscheduler.model.UserModel
import com.open.day.dayscheduler.network.UserNetworkDataSource
import retrofit2.Retrofit
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao, private val retrofit: Retrofit) {

    private val userNetworkService = retrofit.create(UserNetworkDataSource::class.java)


    suspend fun getLocalUser(): UserModel {
        val user = userDao.getLocalUser() ?: throw NoSuchRowException("No local user in DB")
        return UserEntityToFromUserModel.convertUserEntity(user)
    }

    suspend fun updateUser(userModel: UserModel) {
        userModel.id?.let {
            userDao.updateUser(UserEntityToFromUserModel.convertUserModel(userModel, it))
        }
    }

    suspend fun insertUser(userModel: UserModel) {
        userDao.insertUser(UserEntityToFromUserModel.convertUserModel(userModel))
    }

    suspend fun getUserByEmail(email: String): UserModel {
        val userEntity = userDao.getUserByEmail(email)

        if (userEntity == null) {
            val userDto = userNetworkService.getByEmail(email)

            if (userDto != null) {
                return UserDtoToUserModel.convert(userDto)
            }

            throw NetworkException()
        }

        return UserEntityToFromUserModel.convertUserEntity(userEntity)
    }
}