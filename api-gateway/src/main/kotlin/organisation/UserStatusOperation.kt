package com.redwater.organisation

import com.redwater.model.*
import com.redwater.plugins.getOrgIdFromPath
import com.redwater.plugins.getTeamIdFromPath
import com.redwater.retrofit.OrganisationService
import com.redwater.retrofit.RetrofitClientProvider
import com.redwater.utils.ServiceKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

object UserStatusOperation {

    private val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.ORGANISATION)
    private val organisationService = RetrofitClientProvider.getService<OrganisationService>(retrofit)

    suspend fun createStatus(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val requestBody = call.receive<RequestBody<UserStatus>>()
            val createStatusResponse = organisationService.createUserStatus(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath(), requestBody)
            when(createStatusResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("user_status" to createStatusResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to create user status data"))
                }
            }
        }
    }

    suspend fun getStatus(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else {
            val statusId = call.parameters.get("statusId").toString()
            if (statusId.isEmpty()) {
                call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("statusId is mandatory"))
            } else {
                val statusResponse = organisationService.getUserStatus(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath(), statusId)
                when (statusResponse) {
                    is Response.Success -> {
                        if (statusResponse.data == null) {
                            call.respond(
                                status = HttpStatusCode.NotFound,
                                message = Response.Failed("User status data not available")
                            )
                        }else {
                            call.respond(
                                status = HttpStatusCode.OK,
                                message = Response.Success(mapOf("user_status" to statusResponse.data))
                            )
                        }
                    }
                    is Response.Failed -> {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = Response.Failed("Unable to get user status data")
                        )
                    }
                }
            }
        }
    }

    suspend fun getAllStatuses(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else {
            val statusListResponse = organisationService.getAllUserStatus(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath())
            when (statusListResponse) {
                is Response.Success -> {
                    if (statusListResponse.data == null) {
                        call.respond(
                            status = HttpStatusCode.NotFound,
                            message = Response.Failed("User status data not available")
                        )
                    }else {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = Response.Success(mapOf("user_status" to statusListResponse.data))
                        )
                    }
                }

                is Response.Failed -> {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = Response.Failed("Unable to get user status data")
                    )
                }
            }
        }
    }

    suspend fun updateStatuses(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val requestBody = call.receive<RequestBody<UpdateData>>()
            val statusUpdateResponse = organisationService.updateUserStatus(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath(), requestBody)
            when(statusUpdateResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("user_status" to statusUpdateResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to update user status data"))
                }
            }
        }
    }

    suspend fun deleteStatuses(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val requestBody = call.receive<RequestBody<DeleteData>>()
            val statusDeleteResponse = organisationService.deleteUserStatus(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(),pipelineContext.getTeamIdFromPath(), requestBody)
            when(statusDeleteResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("user_status" to statusDeleteResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to delete user status data"))
                }
            }
        }
    }
}