package com.redwater

import com.mongodb.client.model.Updates
import com.redwater.model.*
import com.redwater.repository.UserCustomPropsRepositoryImpl
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

class UserCustomPropOperations(orgId: String) {
    private val userCustomPropRepository = UserCustomPropsRepositoryImpl(orgId)

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    suspend fun createProp(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val prop = call.receive<RequestBody<UserCustomProps>>()
        val existedProp = userCustomPropRepository.getPropByName(prop.d.first().propName)
        if (existedProp != null) {
            call.respond(status = HttpStatusCode.Conflict, message = Response.Failed("Prop name already exists"))
        }else{
            val result = userCustomPropRepository.create(prop.d.first())
            call.respond(status = HttpStatusCode.OK, message = Response.Success(result))
        }
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    suspend fun getAllUserCustomProps(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val propList = userCustomPropRepository.getAll()
        call.respond(status = HttpStatusCode.OK, message = Response.Success(propList))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    suspend fun getProp(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val propId = call.parameters.get("propId")
        val prop = userCustomPropRepository.getProp(ObjectId(propId))
        call.respond(status = HttpStatusCode.OK, message = Response.Success(prop))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    suspend fun updateProp(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val principal = call.principal<JWTPrincipal>()
        val userRole = principal?.payload?.getClaim("role").toString()
        val requestBody = call.receive<RequestBody<UpdateData>>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        requestBody.d.forEach{updateData->
            validateFieldUpdates<UserCustomProps>(updateData.data, userRole)
        }
        val updateData = requestBody.d.first { updateDateList ->
            updateDateList.scope == UpdateDataScope.TEAM && updateDateList.type == UpdateDataType.UPDATE
        }
        val orgUpdateList = updateData.data.filter { data ->
            data.key != TicketCustomProps::_id.name
        }.map {(field, value) ->
            Updates.set(field, value)
        }
        val combinedUpdate = Updates.combine(orgUpdateList)
        val updatedCount = userCustomPropRepository.update(ObjectId(updateData.entityId), combinedUpdate)
        call.respond(status = HttpStatusCode.OK, message = Response.Success("updated $updatedCount records"))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    suspend fun deleteProp(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val requestBody = call.receive<RequestBody<DeleteData>>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        val updateData = requestBody.d.first { updateDateList ->
            updateDateList.scope == UpdateDataScope.TEAM
        }
        val result = userCustomPropRepository.delete(ObjectId(updateData.entityId))
        call.respond(status = HttpStatusCode.OK, message = Response.Success("deleted $result"))
    }
}