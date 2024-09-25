package com.redwater.user

import com.redwater.plugins.tryOrReject
import com.redwater.retrofit.RetrofitClientProvider
import com.redwater.retrofit.UserService
import com.redwater.utils.ServiceKey
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.userRoutes(){
    route("/auth/basic"){
        post("/login") {
            tryOrReject {
                UserOperations.login(this)
            }

        }
        post("/signup"){
            tryOrReject {
                UserOperations.signUp(this)
            }
        }
    }
    authenticate{
        route("/{orgId}") {
            get("/user"){
                tryOrReject{
                    UserOperations.getUserByOrgId(this)
                }
            }
            get("/users"){
                tryOrReject{
                    UserOperations.getUserListByOrgId(this)
                }
            }
            //update name, role, status, teamId
            put("/update") {
                tryOrReject {
                    UserOperations.updateUserByOrgId(this)
                }
            }

            post("/delete") {
                tryOrReject {
                    UserOperations.deleteUserByOrgId(this)
                }
            }
            route("/{teamId}"){
                get("/user"){
                    tryOrReject{
                        UserOperations.getUserByTeamId(this)
                    }
                }
                get("/users") {
                    tryOrReject{
                        UserOperations.getUserListByTeamId(this)
                    }
                }
                post("/add"){
                    tryOrReject{
                        UserOperations.addUserByTeamId(this)
                    }
                }
                post("/update") {
                    tryOrReject{
                        UserOperations.updateUserByTeamId(this)
                    }
                }
                post("/delete") {
                    tryOrReject{
                        UserOperations.deleteUserByTeamId(this)
                    }
                }
            }
        }
    }
}