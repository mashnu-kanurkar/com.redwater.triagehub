package com.redwater.notification

import com.redwater.plugins.tryOrReject
import com.redwater.plugins.tryOrRejectWithHandler
import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Route.invitationRoutes(){
    route("/{orgId}"){
        authenticate {
            post("/invitation"){
                tryOrRejectWithHandler {
                    InvitationOperations.invite(this)
                }
            }
        }

        get("/activation"){
            tryOrReject {
                InvitationOperations.validateInvitation(this)
            }
        }
    }
}