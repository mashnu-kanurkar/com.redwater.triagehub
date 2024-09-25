package com.redwater.plugins

import com.redwater.*
import com.redwater.authentication.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(){

    install(RoleBasedAuthorization)
    install(Authentication) {
        jwt {
            verifier(JwtService(this@configureRouting).verifier)
            validate { credential ->
                // Validate the claims in the token
                val userId = credential.payload.subject
                val email = credential.payload.getClaim("email").asString()
                val role = credential.payload.getClaim("role").asString()
                val orgId = credential.payload.getClaim("orgId").asString()
                val expirationTime = credential.payload.expiresAt.time

                // Perform necessary checks
                if (userId != null
                    && email.isNotEmpty()
                    && role.isNotEmpty()
                    && orgId.isNotEmpty()
                ) {
                    // Optionally: Check if the token is expired
                    if (System.currentTimeMillis() > expirationTime) {
                        null  // Token has expired
                    } else {
                        JWTPrincipal(credential.payload)
                    }
                } else {
                    null  // Invalid token
                }
            }
        }
    }

    routing {
        get("/"){
            call.respond("welcome to organisation service")
        }
        authenticate {
            route("/organisation/{orgId}"){
                organisationRoutes()
                teamRoutes()
                ticketCustomPropRoutes()
                userCustomPropRoutes()
                userStatusRoutes()
            }
        }

    }
}