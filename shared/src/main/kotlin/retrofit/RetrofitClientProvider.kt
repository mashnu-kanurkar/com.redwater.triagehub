package com.redwater.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.redwater.model.InternalUser
import com.redwater.model.ObjectIdAdapter
import com.redwater.model.Response
import com.redwater.model.ResponseTypeAdapter
import com.redwater.utils.ServiceKey
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.bson.types.ObjectId

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object RetrofitClientProvider {
    private val retrofitClientStore: ConcurrentHashMap<String, Retrofit> = ConcurrentHashMap()

    fun getRetrofitClient(serviceKey: ServiceKey, timeoutSettings: TimeoutSettings? = null): Retrofit? {
        return retrofitClientStore.getOrPut(serviceKey.key){
            createClient(serviceKey = serviceKey, timeoutSettings = timeoutSettings)
        }
    }

    private fun createClient(serviceKey: ServiceKey, timeoutSettings: TimeoutSettings?): Retrofit{
        //show Network information in to the logcat
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
                // time out setting
                .connectTimeout(timeoutSettings?.connectTimeout?:3, TimeUnit.SECONDS)
                .readTimeout(timeoutSettings?.readTimeout?:20, TimeUnit.SECONDS)
                .writeTimeout(timeoutSettings?.writeTimeout?:25, TimeUnit.SECONDS)
                .addInterceptor {chain->
                    val request = chain.request().newBuilder()
                        .addHeader("Accept", "application/json")
                        .build()
                    chain.proceed(request)
                }
        }.build()
        var baseUrl = serviceKey.domain
        if (serviceKey.port != null){
            baseUrl = baseUrl.plus(":${serviceKey.port}")
        }
        val gson = GsonBuilder()
            .registerTypeAdapter(Response::class.java, ResponseTypeAdapter<Response<Any>>(Gson()))
            .registerTypeAdapter(ObjectId::class.java, ObjectIdAdapter())
            .registerTypeAdapter(Response::class.java, ResponseTypeAdapter<InternalUser>(Gson()))
            .create()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    inline fun <reified T> getService(retrofit: Retrofit?): T?{
        return retrofit?.create(T::class.java)
    }
}

data class TimeoutSettings(
    val connectTimeout: Long? = null,
    val readTimeout: Long? = null,
    val writeTimeout: Long? = null
)