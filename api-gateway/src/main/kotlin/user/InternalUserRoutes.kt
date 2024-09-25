package com.redwater.user

import com.redwater.plugins.tryOrReject
import com.redwater.plugins.tryOrRejectWithHandler
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.internalUserRoutes(){
    route("/auth/basic/internal"){
        post("/login"){
            tryOrReject {
                InternalUserOperations.login(this)
            }
        }
    }
    authenticate {
        route("/user/internal"){
            post("/create"){
                tryOrRejectWithHandler {
                    InternalUserOperations.createInternalUser(this)
                }
            }
            post("/delete"){
                tryOrRejectWithHandler {
                    InternalUserOperations.deleteInternalUser(this)
                }
            }
            get("/{userId}"){
                tryOrRejectWithHandler {
                    InternalUserOperations.getInternalUser(this)
                }
            }
            get("/all"){
                tryOrRejectWithHandler {
                    InternalUserOperations.getAllInternalUser(this)
                }
            }
            put("/update"){
                tryOrRejectWithHandler {
                    InternalUserOperations.updateInternalUser(this)
                }
            }
        }
    }
}