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

object TicketCustomPropsOperation {
    private val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.ORGANISATION)
    private val organisationService = RetrofitClientProvider.getService<OrganisationService>(retrofit)

    suspend fun createProp(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val requestBody = call.receive<RequestBody<TicketCustomProps>>()
            val createPropResponse = organisationService.createTicketProp(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath(), requestBody)
            when(createPropResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("ticket_prop" to createPropResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to create ticket prop data"))
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
                val propResponse = organisationService.getTicketProp(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath(), propId)
                when (propResponse) {
                    is Response.Success -> {
                        if (propResponse.data == null) {
                            call.respond(
                                status = HttpStatusCode.NotFound,
                                message = Response.Failed("Ticket prop data not available")
                            )
                        }else {
                            call.respond(
                                status = HttpStatusCode.OK,
                                message = Response.Success(mapOf("ticket_prop" to propResponse.data))
                            )
                        }
                    }
                    is Response.Failed -> {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = Response.Failed("Unable to get ticket prop data")
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
            val propResponse = organisationService.getAllTicketProps(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath())
                when (propResponse) {
                    is Response.Success -> {
                        if (propResponse.data == null) {
                            call.respond(
                                status = HttpStatusCode.NotFound,
                                message = Response.Failed("Ticket Prop data not available")
                            )
                        }else {
                            call.respond(
                                status = HttpStatusCode.OK,
                                message = Response.Success(mapOf("ticket_prop" to propResponse.data))
                            )
                        }
                    }

                    is Response.Failed -> {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = Response.Failed("Unable to get ticket prop data")
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
            val propUpdateResponse = organisationService.updateTicketProps(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath(), requestBody)
            when(propUpdateResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("ticket_prop" to propUpdateResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to update ticket prop data"))
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
            val propDeleteResponse = organisationService.deleteTicketProps(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(),pipelineContext.getTeamIdFromPath(), requestBody)
            when(propDeleteResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("ticket_prop" to propDeleteResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to delete ticket prop data"))
                }
            }
        }
    }
}