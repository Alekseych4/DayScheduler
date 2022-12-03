package com.open.day.dayscheduler.network

import com.open.day.dayscheduler.model.UserModel
import com.open.day.dayscheduler.network.model.UserDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserNetworkDataSource {

    @GET("/users/by-email/{email}")
    suspend fun getByEmail(@Path("email") email: String): UserDto?

    @GET("/users/by-user-id/{userId}")
    suspend fun getById(@Path("userId") userId: String): UserDto?

    @POST("/users/create")
    suspend fun create(userModel: UserDto)

    @PUT("/users/update")
    suspend fun update(userModel: UserDto)
}