package com.redwater

import com.mongodb.client.model.Updates
import com.redwater.model.*
import com.redwater.plugins.getTeamIdFromPath
import com.redwater.repository.TeamRepositoryImpl
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

class TeamOperations(orgId: String) {

    private val teamRepository = TeamRepositoryImpl(orgId)

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun createTeam(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val team = call.receive<RequestBody<Team>>()
        val existedTeam = teamRepository.getTeamByName(team.d.first().name)
        if (existedTeam != null) {
            call.respond(status = HttpStatusCode.Conflict, message = Response.Failed("Team name already exists"))
        }else{
            val result = teamRepository.createTeam(team.d.first())
            call.respond(status = HttpStatusCode.OK, message = Response.Success(result))
        }

    }

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun getAllTeam(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val teamList = teamRepository.getAllTeams()
        call.respond(status = HttpStatusCode.OK, message = Response.Success(teamList))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    suspend fun getTeam(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        //validateOrgId(call)
        val team = teamRepository.getTeam(ObjectId(pipelineContext.getTeamIdFromPath()))
        call.respond(status = HttpStatusCode.OK, message = Response.Success(team))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    suspend fun updateTeam(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val principal = call.principal<JWTPrincipal>()
        val userRole = principal?.payload?.getClaim("role").toString()
        val requestBody = call.receive<RequestBody<UpdateData>>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        requestBody.d.forEach{updateData->
            validateFieldUpdates<Team>(updateData.data, userRole)
        }
        val updateData = requestBody.d.first { updateDateList ->
            updateDateList.scope == UpdateDataScope.TEAM && updateDateList.type == UpdateDataType.UPDATE
        }
        val orgUpdateList = updateData.data.filter { data ->
            data.key != Organisation::_id.name
        }.map {(field, value) ->
            Updates.set(field, value)
        }
        val combinedUpdate = Updates.combine(orgUpdateList)
        val updatedCount = teamRepository.update(ObjectId(updateData.entityId), combinedUpdate)
        call.respond(status = HttpStatusCode.OK, message = Response.Success("updated $updatedCount records"))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun deleteTeam(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val requestBody = call.receive<RequestBody<DeleteData>>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        val updateData = requestBody.d.first { updateDateList ->
            updateDateList.scope == UpdateDataScope.TEAM
        }
        val result = teamRepository.delete(ObjectId(updateData.entityId))
        call.respond(status = HttpStatusCode.OK, message = Response.Success("deleted $result"))
    }
}