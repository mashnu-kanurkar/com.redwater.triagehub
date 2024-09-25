package com.redwater.plugins

import com.redwater.authentication.validateOrgId
import com.redwater.model.Response
import com.redwater.utils.RequiresRole
import com.redwater.utils.checkRole
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

// Define a type representing the handler function
typealias FunctionHandler = suspend () -> Unit
// Create a key for storing and retrieving the function handler
val FunctionHandlerKey = AttributeKey<FunctionHandler>("FunctionHandlerKey")

val RoleBasedAuthorization = createRouteScopedPlugin("RoleBasedAuthorization") {
    onCall { call ->
        // Retrieve the function handler from attributes
        val functionHandler = call.attributes.getOrNull(FunctionHandlerKey)
        // If there's no function handler, there's nothing to check.
        functionHandler ?: return@onCall
        // Reflectively find the Role annotation on the function handler
        //val roleAnnotation = functionHandler::class.findAnnotation<RequiresRole>()
        // Check if the function handler is annotated with @RequiresRole
        val roleAnnotation = functionHandler::class.java.getMethod("invoke")
            .getAnnotation(RequiresRole::class.java)
        roleAnnotation?.let {
            val principal = call.principal<JWTPrincipal>()
            val userRole = principal?.payload?.getClaim("role")?: throw IllegalAccessException("Missing role in request")
            checkRole(it.systemRole, userRole.toString())
        }
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.tryOrReject(block: suspend (call: ApplicationCall)-> Unit){
    val logger = LoggerFactory.getLogger(this::class.java)
    try {
        block.invoke(this.call)
    }catch (e: IllegalAccessException){
        logger.info("IllegalAccessException while handling route: ${call.request.uri}")
        logger.info("${e.stackTrace}")
        this.call.respond(status = HttpStatusCode.Unauthorized, message = Response.Failed("IllegalAccess Exception Exception captured: ${e.message}"))
    }
    catch (e: Exception){
        logger.info("Exception while handling route: ${call.request.uri}")
        logger.info("${e.stackTrace}")
        this.call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed("General exception captured: ${e.message.toString()}"))
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.tryOrRejectWithHandler(
    functionHandler: suspend (PipelineContext<Unit, ApplicationCall>) -> Unit, // Suspended function that needs to be invoked
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    try {
        validateOrgId(call)
        // Attach the function handler to the call attributes
        call.attributes.put(FunctionHandlerKey) { functionHandler(this) }
        // Execute the provided function handler
        functionHandler(this)
    } catch (e: IllegalAccessException) {
        logger.info("${getOrgIdFromPath()} IllegalAccessException while handling route: ${call.request.uri}")
        logger.info("${e.stackTrace}")
        this.call.respond(
            status = HttpStatusCode.Unauthorized,
            message = Response.Failed(e.message.toString())
        )
    } catch (e: Exception) {
        logger.info("${getOrgIdFromPath()} Exception while handling route with handler: ${call.request.uri}")
        logger.info("${e.stackTrace}")
        this.call.respond(
            status = HttpStatusCode.InternalServerError,
            message = Response.Success(data = e.message)
        )
    }
}

fun PipelineContext<*, ApplicationCall>.getOrgIdFromPath(): String {
    val orgId = call.parameters.get("orgId")?:throw IllegalArgumentException("orgId missing in path")
    return orgId
}
fun PipelineContext<*, ApplicationCall>.getTeamIdFromPath(): String {
    val teamId = call.parameters.get("teamID")?:throw IllegalArgumentException("teamId missing in path")
    return teamId
}