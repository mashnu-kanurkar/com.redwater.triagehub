package com.redwater.model

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

enum class SystemRole(val roleName: String){
    SYSTEM (RoleNames.system),
    CREATOR(RoleNames.creator),
    ADMIN(RoleNames.admin),
    TEAM_ADMIN(RoleNames.teamAdmin),
    USER(RoleNames.user)
}
class SystemRoleCodec : Codec<SystemRole> {
    override fun decode(reader: BsonReader, decoderContext: DecoderContext): SystemRole {
        val roleName = reader.readString()
        return SystemRole.entries.find { it.roleName == roleName }
            ?: throw IllegalArgumentException("Unknown role name: $roleName")
    }

    override fun encode(writer: BsonWriter, value: SystemRole, encoderContext: EncoderContext) {
        writer.writeString(value.roleName)
    }

    override fun getEncoderClass(): Class<SystemRole> = SystemRole::class.java
}


object RoleNames{
    const val system = "System"
    const val creator = "Creator"
    const val admin = "Admin"
    const val teamAdmin = "Team_Admin"
    const val user = "User"
}