package com.redwater

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.redwater.authentication.validateTeamId
import com.redwater.model.*
import com.redwater.repository.InternalUserRepositoryImpl
import com.redwater.utils.RequiresRole
import com.redwater.utils.validateFieldUpdates
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.bson.types.ObjectId

class InternalUserOperation {
    private val internalUserRepository = InternalUserRepositoryImpl()

    @RequiresRole(RoleNames.system)
    suspend fun createInternalUser(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val requestBody = call.receive<RequestBody<InternalUser>>()
        if (requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Empty data"))
        }else{
            val result = internalUserRepository.create(requestBody.d.first())
            call.respond(status = HttpStatusCode.OK, message = Response.Success("created internal user with id $result"))
        }

    }

    @RequiresRole(RoleNames.system)
    suspend fun getInternalUser(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val internalUserId = call.parameters.get("userId")
        val result = internalUserRepository.get(ObjectId(internalUserId))
        if (result == null){
            call.respond(status = HttpStatusCode.NotFound, message = Response.Failed("User not found"))
        }else{
            call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("internal_user" to result)))
        }
    }

    @RequiresRole(RoleNames.system)
    suspend fun getAllInternalUser(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val result = internalUserRepository.getAll()
        if (result == null){
            call.respond(status = HttpStatusCode.NotFound, message = Response.Failed("User not found"))
        }else{
            call.respond(status = HttpStatusCode.OK, message = Response.Success(mapOf("internal_users" to result)))
        }
    }

    @RequiresRole(RoleNames.system)
    suspend fun updateInternalUsers(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val principal = call.principal<JWTPrincipal>()
        val userRole = principal?.payload?.getClaim("role").toString()
        val requestBody = call.receive<RequestBody<UpdateData>>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        requestBody.d.forEach{updateData->
            validateFieldUpdates<InternalUser>(updateData.data, userRole)
        }
        val updateOperations = requestBody.d.filter {updateDateList->
            updateDateList.scope == UpdateDataScope.USER && updateDateList.type == UpdateDataType.UPDATE }
            .map{updateDataList->
                val singleUserUpdateList = updateDataList.data.filter {data->
                    data.key != InternalUser::_id.name && data.key != InternalUser::email.name //_id and email can not be updated
                }.map { (field, value) ->
                    Updates.set(field, value)
                }
                val combinedUpdate = Updates.combine(singleUserUpdateList)
                val options = UpdateOptions().upsert(false)
                UpdateOneModel<InternalUser>(
                    Filters.eq(InternalUser::_id.name, updateDataList.entityId), // Assuming "_id" is the field holding user IDs
                    combinedUpdate,
                    options
                )
            }
        val updatedCount = internalUserRepository.update(updateOperations)
        call.respond(status = HttpStatusCode.OK, message = Response.Success("updated $updatedCount records"))
    }

    @RequiresRole(RoleNames.system)
    suspend fun deleteInternalUser(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        //validateOrgId(call)
        validateTeamId(call)
        val requestBody = call.receive<RequestBody<DeleteData>>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        val userIds = requestBody.d.filter {
            it.scope == UpdateDataScope.USER
        }.map {
            ObjectId(it.entityId)
        }
        val deletedCount = internalUserRepository.delete(userIds)
        call.respond(status = HttpStatusCode.OK, message = Response.Success("deleted $deletedCount records"))
    }
}