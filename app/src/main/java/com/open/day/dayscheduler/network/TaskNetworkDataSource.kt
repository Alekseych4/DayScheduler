package com.open.day.dayscheduler.network

import com.open.day.dayscheduler.network.model.TaskDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface TaskNetworkDataSource {

    @GET("/tasks/by-task-id/{taskId}")
    suspend fun getById(@Path("taskId") taskId: UUID): TaskDto

    @GET("/tasks/by-user-id/{userId}")
    suspend fun getByUserId(@Path("userId") userId: UUID): List<TaskDto>

    @POST("/tasks/create")
    suspend fun create(taskModel: TaskDto)

    @PUT("/tasks/update")
    suspend fun update(taskModel: TaskDto)

    @DELETE("/tasks/delete-by-task-id/{taskId}")
    suspend fun deleteById(taskId: UUID)
}