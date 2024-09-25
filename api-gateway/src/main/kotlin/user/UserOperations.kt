package com.redwater.user

import com.redwater.authentication.JwtService
import com.redwater.model.*
import com.redwater.plugins.getOrgIdFromPath
import com.redwater.plugins.getTeamIdFromPath
import com.redwater.retrofit.RetrofitClientProvider
import com.redwater.retrofit.UserService
import com.redwater.utils.ServiceKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.slf4j.LoggerFactory

object UserOperations {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.USER)
    private val userService = RetrofitClientProvider.getService<UserService>(retrofit)

    suspend fun login(pipeline: PipelineContext<Unit, ApplicationCall>){
        logger.debug("initiated login request")
        val call = pipeline.call
        val loginRequest = call.receive<LoginRequest>()

        if (retrofit == null || userService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val loginResponse = userService.login(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),loginRequest)
            when(loginResponse){
                is Response.Success ->{
                    val user = loginResponse.data
                    val token = JwtService(pipeline.application).generateToken(user)
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("user" to user, "token" to token)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(loginResponse.error))

                }
            }
        }
    }

    suspend fun signUp(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        if (retrofit == null || userService == null){
            call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("Something went wrong"))
        }else{
            val signUpRequest = call.receive<SignUpRequest>()
            val signUpResponse = userService.signUp(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),signUpRequest)
            when(signUpResponse){
                is Response.Success ->{
                    val user = signUpResponse.data
                    val token = JwtService(pipeline.application).generateToken(user)
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("user" to user, "token" to token)))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(signUpResponse.error))
                }
            }
        }
    }

    suspend fun getUserByOrgId(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        val orgId = pipeline.getOrgIdFromPath()
        if (orgId.isEmpty() || userService == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("OgrId is mandatory"))
        }else {
            val response = userService.getUserById(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),orgId)
            when(response){
                is Response.Success ->{
                    val user = response.data
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(user))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(response.error))
                }
            }
        }
    }

    suspend fun getUserListByOrgId(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val orgId = pipelineContext.getOrgIdFromPath()
        if (userService == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("OgrId is mandatory"))
        }else {
            val response = userService.getUserListByOrgId(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),orgId)
            when(response){
                is Response.Success ->{
                    val user = response.data
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(user))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(response.error))
                }
            }
        }
    }

    suspend fun updateUserByOrgId(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val orgId = pipelineContext.getOrgIdFromPath()
        if (userService == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("OgrId is mandatory"))
        }else {
            val requestBody = call.receive<RequestBody<UpdateData>>()
            val response = userService.updateByOrgId(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()), orgId, requestBody)
            when(response){
                is Response.Success ->{
                    val user = response.data
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(user))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(response.error))
                }
            }
        }
    }

    suspend fun deleteUserByOrgId(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val orgId = pipelineContext.getOrgIdFromPath()
        if (userService == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("OgrId is mandatory"))
        }else {
            val requestBody = call.receive<RequestBody<UpdateData>>()
            val response = userService.deleteByOrgId(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),orgId, requestBody)
            when(response){
                is Response.Success ->{
                    val user = response.data
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(user))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(response.error))
                }
            }
        }
    }

    suspend fun getUserByTeamId(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val orgId = pipelineContext.getOrgIdFromPath()
        val teamId = pipelineContext.getTeamIdFromPath()
        if (userService == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("OgrId is mandatory"))
        }else {
            val response = userService.getUserByTeamId(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),orgId, teamId)
            when(response){
                is Response.Success ->{
                    val user = response.data
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(user))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(response.error))
                }
            }
        }
    }

    suspend fun getUserListByTeamId(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val orgId = pipelineContext.getOrgIdFromPath()
        val teamId = pipelineContext.getTeamIdFromPath()
        if (userService == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("OgrId is mandatory"))
        }else {
            val response = userService.getUserListByTeamId(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),orgId, teamId)
            when(response){
                is Response.Success ->{
                    val user = response.data
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(user))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(response.error))
                }
            }
        }
    }

    suspend fun addUserByTeamId(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call

        val orgId = pipelineContext.getOrgIdFromPath()
        val teamId = pipelineContext.getTeamIdFromPath()
        if (userService == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("OgrId is mandatory"))
        }else {
            val requestBody = call.receive<RequestBody<UpdateData>>()
            val response = userService.addByTeamId(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),orgId, teamId, requestBody)
            when(response){
                is Response.Success ->{
                    val user = response.data
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(user))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(response.error))
                }
            }
        }
    }

    suspend fun updateUserByTeamId(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val orgId = pipelineContext.getOrgIdFromPath()
        val teamId = pipelineContext.getTeamIdFromPath()
        if (userService == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("OgrId is mandatory"))
        }else {
            val requestBody = call.receive<RequestBody<UpdateData>>()
            val response = userService.updateByTeamId(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),orgId, teamId, requestBody)
            when(response){
                is Response.Success ->{
                    val user = response.data
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(user))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(response.error))
                }
            }
        }
    }

    suspend fun deleteUserByTeamId(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val orgId = pipelineContext.getOrgIdFromPath()
        val teamId = pipelineContext.getTeamIdFromPath()
        if (userService == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("OgrId is mandatory"))
        }else {
            val requestBody = call.receive<RequestBody<UpdateData>>()
            val response = userService.deleteByTeamId(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),orgId, teamId, requestBody)
            when(response){
                is Response.Success ->{
                    val user = response.data
                    call.respond(status = HttpStatusCode.OK, message = Response.Success(user))
                }
                is Response.Failed ->{
                    call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed(response.error))
                }
            }
        }
    }
}