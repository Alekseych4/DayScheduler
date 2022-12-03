package com.open.day.dayscheduler.data.converter.dtoModelConverters

import com.open.day.dayscheduler.model.UserModel
import com.open.day.dayscheduler.network.model.UserDto

class UserDtoToUserModel {
    companion object{
        fun convert(userDto: UserDto): UserModel {
            return UserModel(
                userDto.id,
                userDto.name,
                userDto.surname,
                null,
                true,
                userDto.email,
                userDto.idInService,
                userDto.profileImageUrl
            )
        }
    }
}