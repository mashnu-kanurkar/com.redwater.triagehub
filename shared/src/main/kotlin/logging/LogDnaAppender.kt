package com.redwater.logging

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.UnsynchronizedAppenderBase
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.redwater.retrofit.RetrofitClientProvider
import com.redwater.retrofit.TimeoutSettings
import com.redwater.utils.ServiceKey
import io.ktor.http.*
import org.slf4j.LoggerFactory
import retrofit2.Response
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*


open class LogDnaAppender : UnsynchronizedAppenderBase<ILoggingEvent>() {

    companion object{
        const val CUSTOM_USER_AGENT = "LogDna Logback Appender"

    }
    private val errorLog = LoggerFactory.getLogger(LogDnaAppender::class.java)
    private var dataMapper: ObjectMapper = ObjectMapper()
    private val responseMapper = ObjectMapper()
    @Volatile
    private var service: LogDnaService? = null
    private var disabled: Boolean = false
    private var headers = mutableMapOf<String, String>()


    // Assignable fields
    lateinit var hostname: String

    var encoder: PatternLayoutEncoder? = null

    var appName: String? = null

    var ingestUrl: String = "https://logs.logdna.com/logs/ingest"
    var ingestKey: String = ""

    var mdcFields: String = ""


    var mdcTypes: String = ""

    lateinit var tags: String

    var connectTimeout: Long = 3

    var readTimeout: Long = 20

    var useTimeDrift: Boolean = true

    init {
        headers.put("User-Agent", CUSTOM_USER_AGENT)
        headers.put("Accept", "application/json")
        headers.put("Content-Type", "application/json")

        dataMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        dataMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)

        responseMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun start() {
        errorLog.info("ingestKey: $ingestKey")
        super.start()
    }

    protected fun identifyHostname(): String {
        return try {
            InetAddress.getLocalHost().hostName
        } catch (e: UnknownHostException) {
            "localhost"
        }
    }

    // Postpone client initialization to allow timeouts configuration
    protected fun service(): LogDnaService {
        if (service ==null){
            synchronized(this){
                hostname = identifyHostname()
                val retrofit = RetrofitClientProvider.getRetrofitClient(ServiceKey.LOG_DNA,
                    TimeoutSettings(connectTimeout = connectTimeout, readTimeout = readTimeout))
                errorLog.info("hostname: $hostname and retrofit instance: ${retrofit?.baseUrl()}")
                service = RetrofitClientProvider.getService<LogDnaService>(retrofit)
                errorLog.info("service: ${service}")
                return service!!
            }
        }else{
            return service!!
        }
    }

    override fun append(event: ILoggingEvent?) {
        if (disabled) {
            return
        }
        if (event!!.loggerName.equals(LogDnaAppender::class.java.name)) {
            return
        }
        if (ingestKey.isEmpty()){
            errorLog.warn("Empty ingest API key for LogDNA ; disabling LogDnaAppender");
            disabled = true;
            return;
        }
        try {
            headers.put("apiKey", ingestKey)
            val logDnaRequestBody = toLogDnaRequestBody(event)
            val response = callIngestApi(logDnaRequestBody)
            errorLog.info("sent logdna request ${response.raw()}")
            if (!response.isSuccessful){
                val logDnaResponse = response.body()
                errorLog.error("Error calling LogDna : {} ({})", logDnaResponse?.error, response.errorBody())
            }
        }catch (jsonProcessingException: JsonProcessingException) {
            errorLog.error("Error processing JSON data : {}", jsonProcessingException.message);
        } catch (e: Exception) {
            errorLog.error("Error trying to call LogDna : {}", e.message);
        }
    }

    /**
     * Call LogDna API posting given JSON formated string.
     *
     * @param LogDnaRequestBody
     * @return Response<LogDnaResponse>
     */
    protected fun callIngestApi(logDnaRequestBody: LogDnaRequestBody): Response<LogDnaResponse> {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("hostname", hostname)
        if (this::tags.isInitialized){
            queryMap.put("tags", tags)
        }
        if (useTimeDrift) {
            queryMap.put("now", System.currentTimeMillis().toString())
        }

        return service().sendLogs(headers, queryMap, logDnaRequestBody).execute()
    }

    /**
     * Converts a logback logging event to a LogDnaRequestBody.
     *
     * @param event
     * the logging event
     * @return a LogDnaRequestBody
     */

    private fun toLogDnaRequestBody(event: ILoggingEvent):LogDnaRequestBody{
        val msg = encoder?.encode(event)?.toString(Charsets.UTF_8)?: event.formattedMessage
        val meta: MutableMap<String, Any> = HashMap()
        meta["logger"] = event.loggerName
        val mdcFieldsList = mdcFields.split(",")
        val mdcTypesList = mdcTypes.split(",")
        errorLog.info("mdcFieldsList: $mdcFieldsList, mdcTypesList: $mdcTypesList, event.mdcPropertyMap keys: ${event.mdcPropertyMap.keys.toList()}, event.mdcPropertyMap values: ${event.mdcPropertyMap.values.toList()}")
        if (mdcFieldsList.isNotEmpty() && event.mdcPropertyMap.isNotEmpty()) {
            for ((key, value) in event.mdcPropertyMap.entries) {
                errorLog.info("key: $key and value: $value")
                if (mdcFieldsList.contains(key)) {
                    val type: String = mdcTypesList.get(mdcTypesList.indexOf(key))
                    meta[key] = getMetaValue(type, value)
                }
            }
        }
        return LogDnaRequestBody(lines = listOf(
            Line(timestamp = event.timeStamp.toString(),
            line = msg, app = appName.toString(),
            level = event.level.toString(),
            meta = meta.toString())
        ))
    }

    private fun getMetaValue(type: String, value: String): Any {
        try {
            if ("int" == type) {
                return value.toInt()
            }
            if ("long" == type) {
                return value.toLong()
            }
            if ("boolean" == type) {
                return value.toBoolean()
            }
        } catch (e: NumberFormatException) {
            errorLog.warn("Error getting meta value : {}", e.message)
        }
        return value
    }
}

