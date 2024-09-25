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

object TeamOperations {

    private val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.ORGANISATION)
    private val organisationService = RetrofitClientProvider.getService<OrganisationService>(retrofit)

    suspend fun createTeam(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val requestBody = call.receive<RequestBody<Team>>()
            val createTeamResponse = organisationService.createTeam(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), requestBody)
            when(createTeamResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("team" to createTeamResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to create team data"))
                }
            }
        }
    }

    suspend fun getTeam(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val teamResponse = organisationService.getTeam(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), pipelineContext.getTeamIdFromPath())
            when(teamResponse){
                is Response.Success ->{
                    if (teamResponse.data == null){
                        call.respond(status = HttpStatusCode.NotFound, message = Response.Failed("Team data not available"))
                    }else {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = Response.Success(mapOf("team" to teamResponse.data))
                        )
                    }
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to get team data"))
                }
            }
        }
    }

    suspend fun getAllTeams(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val teamListResponse = organisationService.getAllTeams(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath())
            when(teamListResponse){
                is Response.Success ->{
                    if (teamListResponse.data == null){
                        call.respond(status = HttpStatusCode.NotFound, message = Response.Failed("Team data not available"))
                    }else {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = Response.Success(mapOf("teams" to teamListResponse.data))
                        )
                    }
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to get team data"))
                }
            }
        }
    }

    suspend fun updateTeams(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val requestBody = call.receive<RequestBody<UpdateData>>()
            val teamUpdateResponse = organisationService.updateTeams(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), requestBody)
            when(teamUpdateResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("team" to teamUpdateResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to update team data"))
                }
            }
        }
    }

    suspend fun deleteTeams(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val requestBody = call.receive<RequestBody<DeleteData>>()
            val teamDeleteResponse = organisationService.deleteTeams(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath(), requestBody)
            when(teamDeleteResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("team" to teamDeleteResponse.data)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to delete team data"))
                }
            }
        }
    }
}