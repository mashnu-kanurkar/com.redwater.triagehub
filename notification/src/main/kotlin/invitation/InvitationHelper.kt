package com.redwater.invitation

import com.redwater.model.*
import com.redwater.plugins.getOrgIdFromPath
import com.redwater.repository.InvitationRepositoryImpl
import com.redwater.utils.RequiresRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.bson.types.ObjectId
import java.security.SecureRandom
import java.util.Base64

class InvitationHelper(orgId: String) {

    private val invitationRepository = InvitationRepositoryImpl(orgId)

    @RequiresRole(RoleNames.creator, RoleNames.admin)
    suspend fun inviteUser(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        //validateOrgId(call) this is now added in tryOrRejectWithHandler since this is called whenever JWT auth and role validate is required
        val principal = call.principal<JWTPrincipal>()
        val orgId = pipelineContext.getOrgIdFromPath()
        val invitedByEmail = principal?.payload?.getClaim("email").toString()
        if (invitedByEmail.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("user email missing"))
        }
        val requestBody = call.receive<RequestBody<InvitationRequest>>()

        if(requestBody.d.isEmpty()){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("empty data"))
        }
        val invitations = requestBody.d.map {
            val token = generateSecureRandomToken()
            Invitation(toEmail = it.toEmail,
                token = token,
                createdOn = System.currentTimeMillis(),
                orgId = ObjectId(orgId), invitedByEmail = invitedByEmail,
                role = it.role,
                validTill = System.currentTimeMillis() + (1000 * 60 * 60 * 48), //valid for 48hrs
                invitationStatus = InvitationStatus.INITIATED)
        }
        val insertedIds = invitationRepository.addInvitation(invitations)

        invitations.forEach {
            //send email with invitation link and token
        }
        //temporary
        call.respond(status = HttpStatusCode.OK, message = Response.Success(invitations.map { it.token }))
    }

    suspend fun validateToken(pipelineContext: PipelineContext<Unit, ApplicationCall>){
        val call = pipelineContext.call
        val token = call.parameters.get("token").toString()
        if (token.isEmpty()){
            val invitation = invitationRepository.validateToken(token)
            if (invitation == null){
                call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Invalid request"))
            }
            call.respond(status = HttpStatusCode.OK, message = Response.Success(invitation))
        }else{
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("Invalid request"))
        }
    }
}

fun generateSecureRandomToken(length: Int = 32): String {
    val secureRandom = SecureRandom() // Cryptographically secure random generator
    val randomBytes = ByteArray(length)
    secureRandom.nextBytes(randomBytes) // Fill the byte array with random bytes
    // Encode the bytes as a URL-safe Base64 string
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
}
