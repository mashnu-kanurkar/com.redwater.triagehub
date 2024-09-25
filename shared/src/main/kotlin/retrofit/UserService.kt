package com.redwater.retrofit

import com.redwater.model.*
import retrofit2.http.*

interface UserService {

    @POST("/auth/basic/login")
    suspend fun login(@HeaderMap headerMap: Map<String, String>, @Body loginRequest: LoginRequest): Response<User>

    @POST("/auth/basic/signup")
    suspend fun signUp(@HeaderMap headerMap: Map<String, String>, @Body signUpRequest: SignUpRequest): Response<User>

    @GET("/user/{orgId}/user")
    suspend fun getUserById(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String): Response<User>

    @GET("/user/{orgId}/users")
    suspend fun getUserListByOrgId(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String): Response<List<User>>

    @PUT("/user/{orgId}/update")
    suspend fun updateByOrgId(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Body requestBody: RequestBody<UpdateData>): Response<String>

    @POST("/user/{orgId}/delete")
    suspend fun deleteByOrgId(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Body requestBody: RequestBody<UpdateData>): Response<String>

    @GET("/user/{orgId}/{teamId}/user")
    suspend fun getUserByTeamId(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String): Response<User>

    @GET("/user/{orgId}/{teamId}/users")
    suspend fun getUserListByTeamId(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String): Response<List<User>>

    @PUT("/user/{orgId}/{teamId}/update")
    suspend fun updateByTeamId(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<UpdateData>): Response<String>

    @POST("/user/{orgId}/{teamId}/delete")
    suspend fun deleteByTeamId(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<UpdateData>): Response<String>

    @POST("/user/{orgId}/{teamId}/add")
    suspend fun addByTeamId(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<UpdateData>): Response<String>

}