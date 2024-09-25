package com.redwater.authentication

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

inline fun validateOrgId(call: ApplicationCall){
    val principal = call.principal<JWTPrincipal>()
    val userOrgId = principal?.payload?.getClaim("orgId")
    val requestOrgId = call.parameters.get("orgId").toString()
    if (requestOrgId.isEmpty() || userOrgId.toString() != requestOrgId){
        throw IllegalAccessException("orgId mismatch: not authorised")
    }
}

inline fun validateTeamId(call: ApplicationCall){
    val principal = call.principal<JWTPrincipal>()
    val userTeamIds = principal?.payload?.getClaim("teamIds")?.asList(String::class.java)
    val requestTeamId = call.parameters.get("teamId").toString()
    val doesNotHaveTeam = (userTeamIds?.contains(requestTeamId) == true).not()
    if (userTeamIds.isNullOrEmpty() || requestTeamId.isEmpty() || doesNotHaveTeam){
        throw IllegalAccessException("teamId mismatch: not authorised")
    }
}