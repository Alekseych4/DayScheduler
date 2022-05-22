package com.open.day.dayscheduler.data.converter

import com.open.day.dayscheduler.data.entity.UserEntity
import com.open.day.dayscheduler.model.UserModel
import java.util.UUID

class UserEntityToFromUserModelConverter {
    companion object {
        fun convertUserEntity(entity: UserEntity): UserModel {
            return UserModel(
                entity.id,
                entity.name,
                entity.surname,
                entity.isLocalUser,
                entity.isSignedIn,
                entity.email,
                entity.idInService,
                entity.profileImageUri
            )
        }

        fun convertUserModel(userModel: UserModel, userId: UUID? = null): UserEntity {
            return if (userId != null) {
                UserEntity(
                    userModel.name,
                    userModel.surname,
                    userModel.isLocalUser,
                    userId,
                    userModel.isSignedIn,
                    userModel.idInService,
                    userModel.email,
                    userModel.profileImageUri
                )
            } else {
                UserEntity(
                    userModel.name,
                    userModel.surname,
                    userModel.isLocalUser,
                    isSignedIn = userModel.isSignedIn,
                    idInService = userModel.idInService,
                    email = userModel.email,
                    profileImageUri = userModel.profileImageUri
                )
            }
        }
    }
}