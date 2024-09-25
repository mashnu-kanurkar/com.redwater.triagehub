package com.redwater.logging

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface LogDnaService {

    @POST("/logs/ingest")
    fun sendLogs(@HeaderMap(allowUnsafeNonAsciiValues=true) headers: Map<String, String>, @QueryMap queryMap: Map<String, String>, @Body requestBody: LogDnaRequestBody): Call<LogDnaResponse>
}