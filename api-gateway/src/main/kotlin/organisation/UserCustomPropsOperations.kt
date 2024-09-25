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

object UserCustomPropsOperations{

    private val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.ORGANISATION)
    private val organisationService = RetrofitClientProvider.getService<OrganisationService>(retrofit)

    suspend fun createProp(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val requestBody = call.receive<RequestBody<UserCustomProps>>()
            val createPropResponse = organisationService.createUserProp(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath(), requestBody)
            when(createPropResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("user_prop" to createPropResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to create user prop data"))
                }
            }
        }
    }

    suspend fun getProp(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else {
            val propId = call.parameters.get("propId").toString()
            if (propId.isEmpty()) {
                call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("propId is mandatory"))
            } else {
                val propResponse = organisationService.getUserProp(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath(), propId)
                when (propResponse) {
                    is Response.Success -> {
                        if (propResponse.data == null) {
                            call.respond(
                                status = HttpStatusCode.NotFound,
                                message = Response.Failed("User prop data not available")
                            )
                        }else {
                            call.respond(
                                status = HttpStatusCode.OK,
                                message = Response.Success(mapOf("user_prop" to propResponse.data))
                            )
                        }
                    }
                    is Response.Failed -> {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = Response.Failed("Unable to get user prop data")
                        )
                    }
                }
            }
        }
    }

    suspend fun getAllProps(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else {
            val propListResponse = organisationService.getAllUserProps(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath())
            when (propListResponse) {
                is Response.Success -> {
                    if (propListResponse.data == null) {
                        call.respond(
                            status = HttpStatusCode.NotFound,
                            message = Response.Failed("User Prop data not available")
                        )
                    }else {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = Response.Success(mapOf("user_prop" to propListResponse.data))
                        )
                    }
                }

                is Response.Failed -> {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = Response.Failed("Unable to get user prop data")
                    )
                }
            }
        }
    }

    suspend fun updateProps(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val requestBody = call.receive<RequestBody<UpdateData>>()
            val propUpdateResponse = organisationService.updateUserProps(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath(), requestBody)
            when(propUpdateResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("user_prop" to propUpdateResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to update user prop data"))
                }
            }
        }
    }

    suspend fun deleteProps(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val requestBody = call.receive<RequestBody<DeleteData>>()
            val propDeleteResponse = organisationService.deleteUserProps(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(),pipelineContext.getTeamIdFromPath(), requestBody)
            when(propDeleteResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("user_prop" to propDeleteResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to delete user prop data"))
                }
            }
        }
    }
}