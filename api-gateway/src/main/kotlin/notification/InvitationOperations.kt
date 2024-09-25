package com.redwater.notification

import com.redwater.model.InvitationRequest
import com.redwater.model.InvitationStatus
import com.redwater.model.RequestBody
import com.redwater.model.Response
import com.redwater.plugins.getOrgIdFromPath
import com.redwater.retrofit.InvitationService
import com.redwater.retrofit.RetrofitClientProvider
import com.redwater.utils.ServiceKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

object InvitationOperations{

    private val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.NOTIFICATION)
    private val invitationService = RetrofitClientProvider.getService<InvitationService>(retrofit)

    suspend fun invite(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val invitationRequest = call.receive<RequestBody<InvitationRequest>>()
        if (retrofit == null || invitationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val invitationResponse = invitationService.sendInvitation(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()), pipelineContext.getOrgIdFromPath(), invitationRequest )
            when(invitationResponse){
                is Response.Success ->{
                    val invitationToken = invitationResponse.data
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("token" to invitationToken)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Failed to send invitation"))
                }
            }
        }
    }

    suspend fun validateInvitation(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val token = call.request.queryParameters.get("token")

        if (retrofit == null || invitationService == null || token.isNullOrEmpty()){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val invitationResponse = invitationService.activate(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()), pipelineContext.getOrgIdFromPath(), token)
            when(invitationResponse){
                is Response.Success ->{
                    val invitation = invitationResponse.data
                    if (invitation.validTill < System.currentTimeMillis() ||
                        invitation.invitationStatus.ordinal > InvitationStatus.ACCEPTED.ordinal){
                        call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Invitation is expired or not valid"))
                    }
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("invitation" to invitation)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Failed to send invitation"))
                }
            }
        }

    }
}