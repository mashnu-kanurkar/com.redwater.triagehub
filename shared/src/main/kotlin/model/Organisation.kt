package com.redwater.model

import com.redwater.utils.UpdateAllowedBy
import kotlinx.serialization.Contextual
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.TimeZone

data class Organisation(
    @Contextual @BsonId val _id: ObjectId = ObjectId(),

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin)
    val name: String,

    val createdOn: Long,

    @Contextual val createdBy: ObjectId,

    //need to add plan and other billing details in future
)

data class Team(
    @Contextual @BsonId val _id: ObjectId = ObjectId(),

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val name: String,

    val createdOn: Long,

    @Contextual val createdBy: ObjectId,

    @Contextual val timezone: TimeZone

)


