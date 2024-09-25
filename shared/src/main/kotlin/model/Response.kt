package com.redwater.model

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter


sealed class Response<out T> {
    data class Success<out T>(val data: T): Response<T>()
    data class Failed(val error: String): Response<Nothing>()
}

class ResponseTypeAdapter<T>(private val gson: Gson) : TypeAdapter<Response<T>>() {
    override fun write(out: JsonWriter, value: Response<T>) {
        out.beginObject()
        when (value) {
            is Response.Success -> {
                out.name("type").value("success")
                out.name("data")
                gson.toJson(value.data, value.data!!::class.java, out)
            }
            is Response.Failed -> {
                out.name("type").value("failed")
                out.name("error").value(value.error)
            }
        }
        out.endObject()
    }

    override fun read(reader: JsonReader): Response<T> {
        var type: String? = null
        var data: T? = null
        var error: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "type" -> type = reader.nextString()
                "data" -> data = gson.fromJson(reader, object : TypeToken<T>() {}.type)
                "error" -> error = reader.nextString()
            }
        }
        reader.endObject()

        return when (type) {
            "success" -> Response.Success(data!!)
            "failed" -> Response.Failed(error!!)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}



