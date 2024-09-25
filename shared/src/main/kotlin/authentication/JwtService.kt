package com.redwater.authentication

import com.redwater.model.User

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.redwater.model.InternalUser
import io.ktor.server.application.*
import java.util.Date


class JwtService(application: Application) {

    private val issuer = "auto-triager-server"
    private val jwtSecret = application.environment.config.propertyOrNull("jwt.secret")?.getString()
    private val algorithm = Algorithm.HMAC256(jwtSecret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience("all")
        .build()


    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject(user._id.toString())
            .withIssuer(issuer)
            .withClaim("email", user.email)
            .withClaim("role", user.role.name)
            .withClaim("orgId", user.organisationId.toString())
            .withClaim("teamIds", listOf(user.teamIds))
            .withExpiresAt(Date(System.currentTimeMillis() + (1000 * 60 * 60))) // Token valid for 10 minutes
            .sign(algorithm)
    }

    fun generateTokenForInternalUser(internalUser: InternalUser): String{
        return JWT.create()
            .withSubject(internalUser._id.toString())
            .withIssuer(issuer)
            .withClaim("email", internalUser.email)
            .withClaim("role", internalUser.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + (1000 * 60 * 60)))
            .sign(algorithm)
    }
}
