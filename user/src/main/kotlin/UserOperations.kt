package com.redwater

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.redwater.authentication.validateTeamId
import com.redwater.model.*
import com.redwater.repository.AnalystRepository
import com.redwater.repository.AnalystRepositoryImpl
import com.redwater.utils.RequiresRole
import com.redwater.utils.validateFieldUpdates
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext
import org.bson.types.ObjectId

class UserOperations(orgId: String) {
    private val analystRepository: AnalystRepository = AnalystRepositoryImpl(orgId)

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun getAllUsers(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val userList = analystRepository.findAll()
        call.respond(status = HttpStatusCode.OK, message = Response.Success(data = userList))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun deleteUser(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
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
        val deletedCount = analystRepository.deleteById(userIds)
        call.respond(status = HttpStatusCode.OK, message = Response.Success("deleted $deletedCount records"))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    suspend fun findUserById(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        //validateOrgId(call)
        validateTeamId(call)
        val principal = pipeline.call.principal<JWTPrincipal>()
        val userId = principal?.payload?.subject.toString()
        if (userId.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("User id is mandatory"))
        }
        val user = analystRepository.findById(objectId = ObjectId(userId))
        call.respond(status = HttpStatusCode.OK, message = Response.Success(data = user))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun findUserByOrgId(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        //validateOrgId(call)
        val principal = call.principal<JWTPrincipal>()
        val orgId = principal?.payload?.getClaim("orgId").toString()
        if (orgId.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Organisation is mandatory"))
        }

        val users = analystRepository.findByOrgId(orgId = ObjectId(orgId))
        call.respond(status = HttpStatusCode.OK, message = Response.Success(data = users))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    suspend fun updateUser(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        //validateOrgId(call)
        val principal = call.principal<JWTPrincipal>()
        val userRole = principal?.payload?.getClaim("role").toString()
        val requestBody = call.receive<RequestBody<UpdateData>>()
        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        requestBody.d.forEach{updateData->
            validateFieldUpdates<User>(updateData.data, userRole)
        }
        val updateOperations = requestBody.d.filter {updateDateList->
            updateDateList.scope == UpdateDataScope.USER && updateDateList.type == UpdateDataType.UPDATE }
            .map{updateDataList->
                val singleUserUpdateList = updateDataList.data.filter {data->
                    data.key != User::_id.name && data.key != User::email.name //_id and email can not be updated
                }.map { (field, value) ->
                    Updates.set(field, value)
                }
                val combinedUpdate = Updates.combine(singleUserUpdateList)
                val options = UpdateOptions().upsert(false)
                UpdateOneModel<User>(
                    Filters.eq("_id", updateDataList.entityId), // Assuming "_id" is the field holding user IDs
                    combinedUpdate,
                    options
                )
            }
        val updatedCount = analystRepository.updateMany(updateOperations)
        call.respond(status = HttpStatusCode.OK, message = Response.Success("updated $updatedCount records"))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    suspend fun getUsersFromTeam(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        //validateOrgId(call)
        validateTeamId(call)
        val principal = call.principal<JWTPrincipal>()
        val teamId = principal?.payload?.getClaim("teamId").toString()
        val users = analystRepository.findByTeamId(teamId = ObjectId(teamId)).map { it }
        call.respond(status = HttpStatusCode.OK, message = Response.Success(data = users))
    }

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun addUsersToTeam(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        //validateOrgId(call)
        validateTeamId(call)
        val principal = call.principal<JWTPrincipal>()
        val teamId = principal?.payload?.getClaim("teamId").toString()
        val userList = call.receive<List<String>>() //list of userId
        val res = analystRepository.addUserToTeam(userList.map { ObjectId(it) }, ObjectId(teamId))
        if (res > 0){
            call.respond(status = HttpStatusCode.OK, message = Response.Success(data = "Updated $res records"))
        }else{
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Unable to update user"))
        }
    }

}