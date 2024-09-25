package com.redwater

import com.mongodb.client.model.Updates
import com.redwater.model.*
import com.redwater.plugins.getOrgIdFromPath
import com.redwater.repository.OrganisationRepositoryImpl
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

class OrgOperations(orgId: String){
    private val organisationRepository = OrganisationRepositoryImpl(orgId)

    @RequiresRole(RoleNames.system)
    suspend fun createOrganisation(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        // org can not be created via api call. it must be created by superuser directly in mongodb atlas
        // For example, a org database will be created by internal user in mongodb
        // and then send a invitation to creator user i.e first user from org
        val call = pipelineContext.call
        val orgToCreate =
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    suspend fun getOrg(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        //validateOrgId(call)
        val org = organisationRepository.get(ObjectId(pipelineContext.getOrgIdFromPath()))
        call.respond(status = HttpStatusCode.OK, message = Response.Success(org))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun updateOrg(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        //validateOrgId(call)
        val principal = call.principal<JWTPrincipal>()
        val userRole = principal?.payload?.getClaim("role").toString()
        val requestBody = call.receive<RequestBody<UpdateData>>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        val updateData = requestBody.d.first { updateDateList ->
            updateDateList.scope == UpdateDataScope.ORG && updateDateList.type == UpdateDataType.UPDATE
        }
        validateFieldUpdates<Organisation>(updateData.data, userRole)

        val orgUpdateList = updateData.data.filter { data ->
            data.key != Organisation::_id.name
        }.map {(field, value) ->
            Updates.set(field, value)
        }
        val combinedUpdate = Updates.combine(orgUpdateList)
        val updatedCount = organisationRepository.update(ObjectId(updateData.entityId), combinedUpdate)
        call.respond(status = HttpStatusCode.OK, message = Response.Success("updated $updatedCount records"))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun deleteOrg(orgId: String){
        // this operation can only be performed by superuser directly in mongodb atlas
    }

}