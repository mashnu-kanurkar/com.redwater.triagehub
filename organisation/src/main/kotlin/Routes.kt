package com.redwater

import com.redwater.plugins.getOrgIdFromPath
import com.redwater.plugins.tryOrRejectWithHandler
import io.ktor.server.routing.*

fun Route.organisationRoutes(){
    get("/org") {
        tryOrRejectWithHandler {
            OrgOperations(getOrgIdFromPath()).getOrg(this)
        }
    }
    post("/configure"){
        tryOrRejectWithHandler {
            OrgOperations(getOrgIdFromPath()).updateOrg(this)
        }
    }

    post("/team"){
        tryOrRejectWithHandler {
            TeamOperations(getOrgIdFromPath()).createTeam(this)
        }
    }
}


fun Route.teamRoutes(){
    route("/team"){
        get("/{teamId}"){
            tryOrRejectWithHandler {
                TeamOperations(getOrgIdFromPath()).getTeam(this)
            }
        }
        get("/all"){
            tryOrRejectWithHandler {
                TeamOperations(getOrgIdFromPath()).getAllTeam(this)
            }
        }
        //team will be created in organisation routes
        put{
            tryOrRejectWithHandler {
                TeamOperations(getOrgIdFromPath()).updateTeam(this)
            }
        }
        post("/delete"){
            tryOrRejectWithHandler {
                TeamOperations(getOrgIdFromPath()).deleteTeam(this)
            }
        }
    }
}

fun Route.ticketCustomPropRoutes(){
    route("/{teamId}/ticketprops"){
        get("/all"){
            tryOrRejectWithHandler {
                TicketCustomPropOperation(getOrgIdFromPath()).getAllTicketCustomProps(this)
            }
        }
        get("/{propId}") {
            tryOrRejectWithHandler {
                TicketCustomPropOperation(getOrgIdFromPath()).getProp(this)
            }
        }

        post {
            tryOrRejectWithHandler {
                TicketCustomPropOperation(getOrgIdFromPath()).createProp(this)
            }
        }

        put{
            tryOrRejectWithHandler {
                TicketCustomPropOperation(getOrgIdFromPath()).updateProp(this)
            }
        }

        post("/delete") {
            tryOrRejectWithHandler {
                TicketCustomPropOperation(getOrgIdFromPath()).deleteProp(this)
            }
        }

    }
}

fun Route.userCustomPropRoutes(){
    route("/{teamId}/userprops"){
        get("/all"){
            tryOrRejectWithHandler {
                UserCustomPropOperations(getOrgIdFromPath()).getAllUserCustomProps(this)
            }
        }
        get("/{propId}") {
            tryOrRejectWithHandler {
                UserCustomPropOperations(getOrgIdFromPath()).getProp(this)
            }
        }

        post {
            tryOrRejectWithHandler {
                UserCustomPropOperations(getOrgIdFromPath()).createProp(this)
            }
        }

        put {
            tryOrRejectWithHandler {
                UserCustomPropOperations(getOrgIdFromPath()).updateProp(this)
            }
        }

        post("/delete") {
            tryOrRejectWithHandler {
                UserCustomPropOperations(getOrgIdFromPath()).deleteProp(this)
            }
        }
    }
}

fun Route.userStatusRoutes(){
    route("/{teamId}/userstatus"){
        get("/all"){
            tryOrRejectWithHandler {
                UserStatusOperations(getOrgIdFromPath()).getAllStatuses(this)
            }
        }
        get("/statusId") {
            tryOrRejectWithHandler {
                UserStatusOperations(getOrgIdFromPath()).getStatus(this)
            }
        }

        post {
            tryOrRejectWithHandler {
                UserStatusOperations(getOrgIdFromPath()).createStatus(this)
            }
        }

        put {
            tryOrRejectWithHandler {
                UserStatusOperations(getOrgIdFromPath()).updateStatus(this)
            }
        }

        post("/delete") {
            tryOrRejectWithHandler {
                UserStatusOperations(getOrgIdFromPath()).deleteStatus(this)
            }
        }
    }
}

