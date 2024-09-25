package com.redwater.plugins

import com.redwater.InternalUserOperation
import com.redwater.UserOperations
import com.redwater.authentication.AuthenticationProvider
import com.redwater.authentication.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(){
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
            call.respond("welcome to user service")
        }
        route("/auth") {
            route("/basic") {
                post("/login") {
                    tryOrReject {
                        val authProvider = AuthenticationProvider()
                        authProvider.login(this)
                    }
                }
                post("/signup") {
                    tryOrReject {
                        val authProvider = AuthenticationProvider()
                        authProvider.registerUser(this)
                    }
                }
                route("/internal") {
                    post("/login") {
                        tryOrReject {
                            val authenticationProvider = AuthenticationProvider()
                            authenticationProvider.loginInternalUser(this)
                        }
                    }
                }
            }
        }
        route("/user"){
            install(RoleBasedAuthorization)
            authenticate {
                route("/internal"){
                    post("/create"){
                        tryOrRejectWithHandler {
                            InternalUserOperation().createInternalUser(this)
                        }
                    }
                    get("/{userId}"){
                        tryOrRejectWithHandler {
                            InternalUserOperation().getInternalUser(this)
                        }
                    }
                    get("/all"){
                        tryOrRejectWithHandler {
                            InternalUserOperation().getAllInternalUser(this)
                        }
                    }
                    put("/update"){
                        tryOrRejectWithHandler {
                            InternalUserOperation().updateInternalUsers(this)
                        }
                    }
                    post("/delete"){
                        tryOrRejectWithHandler {
                            InternalUserOperation().deleteInternalUser(this)
                        }
                    }

                }
            }
            authenticate{
                route("/{orgId}") {
                    //self user data
                    get("/user"){
                        tryOrRejectWithHandler{
                            UserOperations(getOrgIdFromPath()).findUserById(this)
                        }
                    }
                    //all other
                    get("/users"){
                        tryOrRejectWithHandler {
                            UserOperations(getOrgIdFromPath()).getAllUsers(this)
                        }
                    }
                    //update name, role, status, teamId
                    put("/update") {
                        tryOrRejectWithHandler {
                            UserOperations(getOrgIdFromPath()).updateUser(this)
                        }
                    }

                    post("/delete") {
                        tryOrRejectWithHandler {
                            UserOperations(getOrgIdFromPath()).deleteUser(this)
                        }
                    }
                    route("/{teamId}"){
                        //self user data only
                        get("/user"){
                            tryOrRejectWithHandler{
                                UserOperations(getOrgIdFromPath()).findUserById(this)
                            }
                        }
                        //other user data
                        get("/users") {
                            tryOrRejectWithHandler {
                                UserOperations(getOrgIdFromPath()).getUsersFromTeam(this)
                            }
                        }
                        post("/add"){
                            tryOrRejectWithHandler {
                                UserOperations(getOrgIdFromPath()).addUsersToTeam(this)
                            }
                        }
                        put("/update") {
                            tryOrRejectWithHandler {
                                UserOperations(getOrgIdFromPath()).updateUser(this)
                            }
                        }
                        post("/delete") {
                            tryOrRejectWithHandler {
                                UserOperations(getOrgIdFromPath()).deleteUser(this)
                            }
                        }
                    }
                }
            }
        }
    }

}
