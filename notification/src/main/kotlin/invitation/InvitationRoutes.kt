package com.redwater.invitation

import com.redwater.plugins.getOrgIdFromPath
import com.redwater.plugins.tryOrReject
import com.redwater.plugins.tryOrRejectWithHandler
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.routing.application


fun Route.invitationRoutes(){
    route("/notification/{orgId}"){
        authenticate {
            post("/invitation"){
                tryOrRejectWithHandler {
                    InvitationHelper(getOrgIdFromPath()).inviteUser(this)
                }
            }
        }
        get("/activate"){
            tryOrReject {

                InvitationHelper(getOrgIdFromPath()).validateToken(this)
            }
        }
    }
}