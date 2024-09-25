package com.redwater.organisation

import com.redwater.model.RequestBody
import com.redwater.model.Response
import com.redwater.model.UpdateData
import com.redwater.plugins.getOrgIdFromPath
import com.redwater.retrofit.OrganisationService
import com.redwater.retrofit.RetrofitClientProvider
import com.redwater.utils.ServiceKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

object OrganisationOperations{

    private val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.ORGANISATION)
    private val organisationService = RetrofitClientProvider.getService<OrganisationService>(retrofit)

    suspend fun getOrg(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val organisationResponse = organisationService.getOrg(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),pipelineContext.getOrgIdFromPath())
            when(organisationResponse){
                is Response.Success ->{
                    if (organisationResponse.data == null){
                        call.respond(status = HttpStatusCode.NotFound, message = Response.Failed("Org data not available"))
                    }else {
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = Response.Success(mapOf("org" to organisationResponse.data))
                        )
                    }
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to get org data"))
                }
            }
        }
    }

    suspend fun configureOrg(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val orgRequestData = call.receive<RequestBody<UpdateData>>()
        if (retrofit == null || organisationService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val configureOrgResponse = organisationService.configureOrg(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()), pipelineContext.getOrgIdFromPath(), orgRequestData)
            when(configureOrgResponse){
                is Response.Success ->{
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(configureOrgResponse.data))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Failed to send invitation"))
                }
            }
        }

    }
}