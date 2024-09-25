package com.redwater

import com.redwater.mongodb.MongoDBUtils
import com.redwater.plugins.configureRouting
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import java.text.DateFormat

fun main(args: Array<String>){
    EngineMain.main(args)
}

fun Application.organisationModule(){
    install(ContentNegotiation){
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    install(CallLogging) {
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            val contentType = call.request.contentType()
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent, content type: $contentType"
        }
    }
    configureRouting()
    environment.monitor.subscribe(ApplicationStopped) {
        // Close the MongoClient when the application stops
        MongoDBUtils.getClient().close()
    }
}