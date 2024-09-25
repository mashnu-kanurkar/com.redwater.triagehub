package com.redwater.retrofit

import com.redwater.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InternalUserService {

    @POST("/auth/basic/internal/login")
    fun login(@HeaderMap headerMap: Map<String, String>, @Body loginRequest: LoginRequest): Call<InternalUser>

    @GET("/user/internal/{userId}")
    suspend fun getInternalUser(@HeaderMap headerMap: Map<String, String>, @Path("userId") userId: String): Call<InternalUser?>

    @GET("/user/internal/all")
    suspend fun getAllInternalUsers(@HeaderMap headerMap: Map<String, String>, ): Call<List<InternalUser>?>

    @POST("/user/internal/create")
    suspend fun createInternalUser(@HeaderMap headerMap: Map<String, String>, @Body requestBody: RequestBody<InternalUser>):Call<String>

    @PUT("/user/internal/update")
    suspend fun updateInternalUser(@HeaderMap headerMap: Map<String, String>, @Body requestBody: RequestBody<UpdateData>): Call<String>

    @POST("/user/internal/delete")
    suspend fun deleteInternalUser(@HeaderMap headerMap: Map<String, String>, @Body requestBody: RequestBody<DeleteData>): Call<String>
}