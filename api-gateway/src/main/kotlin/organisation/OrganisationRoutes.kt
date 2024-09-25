package com.redwater.organisation

import com.redwater.plugins.tryOrRejectWithHandler
import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Route.organisationRoutes() {
    authenticate {
        route("/{orgId}") {
            get("/org") {
                tryOrRejectWithHandler {
                    OrganisationOperations.getOrg(this)
                }
            }
            post("/configure") {
                tryOrRejectWithHandler {
                    OrganisationOperations.configureOrg(this)
                }
            }
            post("/create/team") {
                tryOrRejectWithHandler {
                    TeamOperations.createTeam(this)
                }
            }
        }
    }
}


fun Route.teamRoutes() {
    authenticate {
        route("/{orgId}/team") {
            get("/{teamId}") {
                tryOrRejectWithHandler {
                    TeamOperations.getTeam(this)
                }
            }
            get("/all") {
                tryOrRejectWithHandler {
                    TeamOperations.getAllTeams(this)
                }
            }
            post("/update") {
                tryOrRejectWithHandler {
                    TeamOperations.updateTeams(this)
                }
            }
            post("/delete") {
                tryOrRejectWithHandler {
                    TeamOperations.deleteTeams(this)
                }
            }
        }
    }
}

fun Route.ticketCustomPropRoutes() {
    authenticate {
        route("/{orgId}/{teamId}/ticketprops") {
            get("/all") {
                tryOrRejectWithHandler {
                    TicketCustomPropsOperation.getAllProps(this)
                }
            }
            get("/{propId}") {
                tryOrRejectWithHandler {
                    TicketCustomPropsOperation.getProp(this)
                }
            }

            post {
                tryOrRejectWithHandler {
                    TicketCustomPropsOperation.createProp(this)
                }
            }

            put {
                tryOrRejectWithHandler {
                    TicketCustomPropsOperation.updateProps(this)
                }
            }

            post("/delete") {
                tryOrRejectWithHandler {
                    TicketCustomPropsOperation.deleteProps(this)
                }
            }
        }
    }
}

fun Route.userCustomPropRoutes() {
    authenticate {
        route("/{orgId}/{teamId}/userprops") {
            get("/all") {
                tryOrRejectWithHandler {
                    UserCustomPropsOperations.getAllProps(this)
                }
            }
            get("/{propId}") {
                tryOrRejectWithHandler {
                    UserCustomPropsOperations.getProp(this)
                }
            }

            post {
                tryOrRejectWithHandler {
                    UserCustomPropsOperations.createProp(this)
                }
            }

            put {
                tryOrRejectWithHandler {
                    UserCustomPropsOperations.updateProps(this)
                }
            }

            post("/delete") {
                tryOrRejectWithHandler {
                    UserCustomPropsOperations.deleteProps(this)
                }
            }
        }
    }
}

fun Route.userStatusRoutes() {
    authenticate {
        route("/{orgId}/{teamId}/userstatus") {
            get("/all") {
                tryOrRejectWithHandler {
                    UserStatusOperation.getAllStatuses(this)
                }
            }
            get("/{statusId}") {
                tryOrRejectWithHandler {
                    UserStatusOperation.getStatus(this)
                }
            }

            post {
                tryOrRejectWithHandler {
                    UserStatusOperation.createStatus(this)
                }
            }

            put {
                tryOrRejectWithHandler {
                    UserStatusOperation.updateStatuses(this)
                }
            }

            post("/delete") {
                tryOrRejectWithHandler {
                    UserStatusOperation.deleteStatuses(this)
                }
            }
        }
    }
}