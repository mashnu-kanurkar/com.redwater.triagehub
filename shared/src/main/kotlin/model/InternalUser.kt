package com.redwater.model

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.redwater.utils.UpdateAllowedBy
import kotlinx.serialization.Contextual
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class InternalUser(
    @Contextual @BsonId val _id: ObjectId = ObjectId(),

    @UpdateAllowedBy(RoleNames.system)
    val email: String,

    @UpdateAllowedBy(RoleNames.system)
    val userName: String,

    @UpdateAllowedBy(RoleNames.system)
    val role: SystemRole,

    @UpdateAllowedBy(RoleNames.system)
    val hashedPassword: String
)

class ObjectIdAdapter : TypeAdapter<ObjectId>() {
    override fun write(out: JsonWriter, value: ObjectId) {
        out.beginObject()
        out.name("\$oid").value(value.toHexString())
        out.endObject()
    }

    override fun read(reader: JsonReader): ObjectId {
        reader.beginObject()
        reader.nextName() // Skip the "$oid" name
        val objectId = ObjectId(reader.nextString())
        reader.endObject()
        return objectId
    }
}
