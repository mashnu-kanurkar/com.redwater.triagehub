package com.redwater.retrofit

import com.redwater.model.*
import retrofit2.http.*

interface OrganisationService {

    //*** Org routes ***//
    @GET("/organisation/{orgId}/org")
    suspend fun getOrg(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String): Response<Organisation?>

    @POST("/organisation/{orgId}/configure")
    suspend fun configureOrg(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Body requestBody: RequestBody<UpdateData>): Response<String>

    @POST("/organisation/{orgId}/team")
    suspend fun createTeam(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Body requestBody: RequestBody<Team>): Response<String>

    //*** Team routes ***//
    @GET("/organisation/{orgId}/team/{teamId}")
    suspend fun getTeam(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String): Response<Team?>

    @GET("/organisation/{orgId}/team/all")
    suspend fun getAllTeams(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String): Response<List<Team>?>

    @PUT("/organisation/{orgId}/team")
    suspend fun updateTeams(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Body requestBody: RequestBody<UpdateData>): Response<String>

    @POST("/organisation/{orgId}/team/delete")
    suspend fun deleteTeams(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Body requestBody: RequestBody<DeleteData>): Response<String>

    //*** Ticket prop routes ***//
    @GET("/organisation/{orgId}/{teamId}/ticketprops/{propId}")
    suspend fun getTicketProp(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Path("propId") propId: String): Response<TicketCustomProps?>

    @GET("/organisation/{orgId}/{teamId}/ticketprops/all")
    suspend fun getAllTicketProps(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String): Response<List<TicketCustomProps>?>

    @POST("/organisation/{orgId}/{teamId}/ticketprops")
    suspend fun createTicketProp(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<TicketCustomProps>): Response<String>

    @PUT("/organisation/{orgId}/{teamId}/ticketprops")
    suspend fun updateTicketProps(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<UpdateData>): Response<String>

    @POST("/organisation/{orgId}/{teamId}/ticketprops/delete")
    suspend fun deleteTicketProps(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<DeleteData>): Response<String>

    //*** user prop routes ***//
    @GET("/organisation/{orgId}/{teamId}/userprops/{propId}")
    suspend fun getUserProp(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Path("propId") propId: String): Response<UserCustomProps?>

    @GET("/organisation/{orgId}/{teamId}/userprops/all")
    suspend fun getAllUserProps(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String): Response<List<UserCustomProps>?>

    @POST("/organisation/{orgId}/{teamId}/userprops")
    suspend fun createUserProp(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<UserCustomProps>): Response<String>

    @PUT("/organisation/{orgId}/{teamId}/userprops")
    suspend fun updateUserProps(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<UpdateData>): Response<String>

    @POST("/organisation/{orgId}/{teamId}/userprops/delete")
    suspend fun deleteUserProps(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<DeleteData>): Response<String>


    //*** user status routes ***//
    @GET("/organisation/{orgId}/{teamId}/userstatus/{statusId}")
    suspend fun getUserStatus(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Path("statusId") statusId: String): Response<UserStatus?>

    @GET("/organisation/{orgId}/{teamId}/userstatus/all")
    suspend fun getAllUserStatus(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String): Response<List<UserStatus>?>

    @POST("/organisation/{orgId}/{teamId}/userstatus")
    suspend fun createUserStatus(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<UserStatus>): Response<String>

    @PUT("/organisation/{orgId}/{teamId}/userstatus")
    suspend fun updateUserStatus(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<UpdateData>): Response<String>

    @POST("/organisation/{orgId}/{teamId}/userstatus/delete")
    suspend fun deleteUserStatus(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Path("teamId") teamId: String, @Body requestBody: RequestBody<DeleteData>): Response<String>


}