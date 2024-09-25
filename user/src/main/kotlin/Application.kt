package com.redwater

import com.redwater.model.InternalUser
import com.redwater.model.ObjectIdAdapter
import com.redwater.model.Response
import com.redwater.model.ResponseTypeAdapter
import com.redwater.mongodb.MongoDBUtils
import com.redwater.plugins.configureRouting
import com.redwater.utils.Constants
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import org.bson.types.ObjectId
import java.text.DateFormat

fun main(args: Array<String>){
    EngineMain.main(args)
}

fun Application.userModule(){
    install(ContentNegotiation){
        gson {
            val gson = this.create()
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
            registerTypeAdapter(ObjectId::class.java, ObjectIdAdapter())
            registerTypeAdapter(Response::class.java, ResponseTypeAdapter<Response<Any>>(gson))
            registerTypeAdapter(Response::class.java, ResponseTypeAdapter<InternalUser>(gson))
        }
    }
    install(CallId){
        replyToHeader(HttpHeaders.XRequestId)
        retrieveFromHeader(HttpHeaders.XRequestId)
        retrieve { call ->
            val callId = call.request.header(HttpHeaders.XRequestId)
            if (callId == null){
                generate(10, Constants.CALL_ID_DICTIONARY)
            }
            callId
        }
        verify { callId->
            callId.isNotEmpty()
        }
    }
    install(CallLogging) {
        callIdMdc("call-id")
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            val contentType = call.request.contentType()
            val isSent = call.response.isSent
            val sentHeaders = call.response.headers.allValues()
            "***************************************\n" +
                    "Status: $status " +
                    "\nHTTP method: $httpMethod, " +
                    "\nUser agent: $userAgent, " +
                    "\ncontent type: $contentType, " +
                    "\nisSent: $isSent, " +
                    "\nsentHeaders: $sentHeaders" +
                    "\n***************************************"
        }
    }
    configureRouting()
    environment.monitor.subscribe(ApplicationStopped) {
        // Close the MongoClient when the application stops
        MongoDBUtils.getClient().close()
    }
}