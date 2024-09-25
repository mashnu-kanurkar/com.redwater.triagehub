package com.redwater.user

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.redwater.authentication.JwtService
import com.redwater.model.*
import com.redwater.retrofit.InternalUserService
import com.redwater.retrofit.RetrofitClientProvider
import com.redwater.retrofit.executeRetrofitRequest
import com.redwater.utils.ServiceKey
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory

object InternalUserOperations {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.USER)
    private val internalUserService = RetrofitClientProvider.getService<InternalUserService>(retrofit)

    suspend fun login(pipelineContext: PipelineContext<Unit, ApplicationCall>) {
        logger.debug("initiated internal login request")
        val call = pipelineContext.call
        logger.debug("initiated login call: ${call.request.userAgent()}")
        val loginRequest = call.receive<LoginRequest>()
        logger.info("Login request created for $loginRequest")
        internalUserService?.login(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()),
            loginRequest = loginRequest)?.executeRetrofitRequest(call){ internalUser->
            val userWithoutPassword = internalUser.copy(hashedPassword = "")
            val token = JwtService(pipelineContext.application).generateTokenForInternalUser(internalUser)
            logger.info("login successful for user $userWithoutPassword")
            val gson = GsonBuilder()
                .registerTypeAdapter(ObjectId::class.java, ObjectIdAdapter())
                .create()
            JsonObject().apply {
                add("user", gson.toJsonTree(userWithoutPassword))
                addProperty("token", token)
            }
        }
    }

    suspend fun createInternalUser(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        logger.debug("initiated createInternalUser request")
        val call = pipelineContext.call
        val requestBody = call.receive<RequestBody<InternalUser>>()
        val createUserCall = internalUserService?.createInternalUser(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()), requestBody = requestBody)
        createUserCall?.executeRetrofitRequest(call)
    }

    suspend fun getInternalUser(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        logger.debug("initiated getInternalUser request")
        val call = pipelineContext.call
        val userId = call.parameters.get("userId")
        logger.debug("initiated getInternalUser request for userId $userId")
        if (userId == null){
            call.respond(status = HttpStatusCode.BadRequest, message = "userId is mandatory")
        }else {
            val getUserCall = internalUserService?.getInternalUser(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()), userId = userId)
            getUserCall?.executeRetrofitRequest(call)
        }
    }

    suspend fun getAllInternalUser(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        logger.debug("initiated getAllInternalUser request")
        val call = pipelineContext.call
        val getAllUserCall = internalUserService?.getAllInternalUsers(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()))
        getAllUserCall?.executeRetrofitRequest(call)
    }

    suspend fun updateInternalUser(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        logger.debug("initiated updateInternalUser request")
        val call = pipelineContext.call
        val requestBody = call.receive<RequestBody<UpdateData>>()
        logger.debug("initiated updateInternalUser request with request body {}", requestBody)
        val updateUserCall = internalUserService?.updateInternalUser(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()), requestBody = requestBody)
        updateUserCall?.executeRetrofitRequest(call)
    }

    suspend fun deleteInternalUser(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        logger.debug("initiated deleteInternalUser request")
        val call = pipelineContext.call
        val requestBody = call.receive<RequestBody<DeleteData>>()
        logger.debug("initiated deleteInternalUser request with request body {}", requestBody)
        val deleteUserCall = internalUserService?.deleteInternalUser(headerMap = mapOf(HttpHeaders.XRequestId to call.callId.toString()), requestBody = requestBody)
        deleteUserCall?.executeRetrofitRequest(call)
    }
}