package com.open.day.dayscheduler.data.converter.dtoModelConverters

import com.open.day.dayscheduler.model.UserModel
import com.open.day.dayscheduler.network.model.UserDto

class UserModelToUserDto {
    companion object {
        fun convert(userModel: UserModel): UserDto {
            return UserDto(
                userModel.id!!,
                userModel.name!!,
                userModel.surname!!,
                userModel.email!!,
                userModel.idInService!!,
                userModel.profileImageUri!!
            )
        }
    }
}