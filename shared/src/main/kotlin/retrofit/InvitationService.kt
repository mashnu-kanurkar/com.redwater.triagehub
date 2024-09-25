package com.redwater.retrofit

import com.redwater.model.*
import retrofit2.http.*

interface InvitationService {
    @POST("/notification/{orgId}/invitation")
    suspend fun sendInvitation(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Body requestBody: RequestBody<InvitationRequest>): Response<String>

    @POST("/notification/{orgId}/activate")
    suspend fun activate(@HeaderMap headerMap: Map<String, String>, @Path("orgId") orgId: String, @Query("token") token: String): Response<Invitation>
}