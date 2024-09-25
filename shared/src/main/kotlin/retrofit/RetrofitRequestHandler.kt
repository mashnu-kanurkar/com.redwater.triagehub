package com.redwater.retrofit


import com.google.gson.JsonObject
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import retrofit2.Call

suspend inline fun <reified T> Call<T>.executeRetrofitRequest(
    ktorCall: ApplicationCall,
    noinline modifyResponseOperation: ((T) -> JsonObject)? = null
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    withContext(Dispatchers.IO) {
        try {
            val retrofitCall = this@executeRetrofitRequest
            val response = retrofitCall.execute()
            if (response.isSuccessful) {
                val body = response.body()
                if (body == null) {
                    logger.error("path: ${ktorCall.request.uri} => resource not found")
                    ktorCall.respond(status = HttpStatusCode.NotFound, message = "Resource not found")
                } else {
                    logger.info("path: ${ktorCall.request.uri} => response successful ")
                    if (modifyResponseOperation != null) {
                        val modifiedResponse = modifyResponseOperation.invoke(body)
                        ktorCall.respond(status = HttpStatusCode.OK, message = modifiedResponse)
                    } else {
                        ktorCall.respond(status = HttpStatusCode.OK, message = body)
                    }
                }
            } else {
                logger.error("path: ${ktorCall.request.uri} => Request failed with code: ${response.code()} and response: ${response.errorBody()?.string()}")
                ktorCall.respond(
                    status = HttpStatusCode.BadRequest,
                    message = response.errorBody()?.string()?:"Unable to process request"
                )
            }
        } catch (e: Exception) {
            logger.error("path: ${ktorCall.request.uri} => Request failed due to exception", e)
            ktorCall.respond(
                status = HttpStatusCode.InternalServerError,
                message = "Something went wrong"
            )
        }
    }
}
