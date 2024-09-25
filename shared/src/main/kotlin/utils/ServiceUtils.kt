package com.redwater.utils

enum class ServiceKey(val key: String, val domain: String,  val port: String? = null){
    ORGANISATION("organisation","http://localhost","8083"),
    USER("analyst","http://localhost","8084"),
    TRIAGER("triager","http://localhost","8085"),
    NOTIFICATION("notification","http://localhost","8086"),
    API_GATEWAY("api-gateway","http://localhost","8088"),
    LOG_DNA("log_dna", "https://logs.logdna.com", )
}