package com.redwater

import com.mongodb.client.model.Updates
import com.redwater.model.*
import com.redwater.repository.UserStatusRepositoryImpl
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

class UserStatusOperations(orgId: String) {

    private val userStatusRepository = UserStatusRepositoryImpl(orgId)

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    suspend fun createStatus(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val prop = call.receive<RequestBody<UserStatus>>()
        val existedProp = userStatusRepository.getStatusByName(prop.d.first().statusName)
        if (existedProp != null) {
            call.respond(status = HttpStatusCode.Conflict, message = Response.Failed("Prop name already exists"))
        }else{
            val result = userStatusRepository.create(prop.d.first())
            call.respond(status = HttpStatusCode.OK, message = Response.Success(result))
        }
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    suspend fun getAllStatuses(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val propList = userStatusRepository.getAll()
        call.respond(status = HttpStatusCode.OK, message = Response.Success(propList))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    suspend fun getStatus(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val statusId = call.parameters.get("statusId")
        val team = userStatusRepository.getStatus(ObjectId(statusId))
        call.respond(status = HttpStatusCode.OK, message = Response.Success(team))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    suspend fun updateStatus(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val principal = call.principal<JWTPrincipal>()
        val userRole = principal?.payload?.getClaim("role").toString()
        val requestBody = call.receive<RequestBody<UpdateData>>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        requestBody.d.forEach{updateData->
            validateFieldUpdates<UserStatus>(updateData.data, userRole)
        }
        val updateData = requestBody.d.first { updateDateList ->
            updateDateList.scope == UpdateDataScope.TEAM && updateDateList.type == UpdateDataType.UPDATE
        }
        val orgUpdateList = updateData.data.filter { data ->
            data.key != UserStatus::_id.name
        }.map {(field, value) ->
            Updates.set(field, value)
        }
        val combinedUpdate = Updates.combine(orgUpdateList)
        val updatedCount = userStatusRepository.update(ObjectId(updateData.entityId), combinedUpdate)
        call.respond(status = HttpStatusCode.OK, message = Response.Success("updated $updatedCount records"))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    suspend fun deleteStatus(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val requestBody = call.receive<RequestBody<DeleteData>>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        val updateData = requestBody.d.first { updateDateList ->
            updateDateList.scope == UpdateDataScope.TEAM
        }
        val result = userStatusRepository.delete(ObjectId(updateData.entityId))
        call.respond(status = HttpStatusCode.OK, message = Response.Success("deleted $result"))
    }
}