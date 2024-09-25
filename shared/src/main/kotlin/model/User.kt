package com.redwater.model

import com.redwater.utils.UpdateAllowedBy
import kotlinx.serialization.Contextual
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId


data class User(

    @Contextual @BsonId val _id: ObjectId = ObjectId(),

    val email: String,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    val name: String,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin)
    @Contextual val teamIds: List<ObjectId>? = null,

    @Contextual val organisationId: ObjectId? = null,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin)
    val role: SystemRole,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin)
    val designation: String = "user",

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    @Contextual val status: UserStatus = getDefaultUserStatus(),

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    val statusSetLimit: Long? = null,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    val statusChangedAt: Long? = null,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin, RoleNames.user)
    val userCustomPropsList: List<UserCustomProps>? = null
)

data class UserStatus(
    @Contextual @BsonId val _id: ObjectId = ObjectId(),

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    @Contextual val teamId: ObjectId? = null,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val statusName: String,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val upperLimit: Long? = null,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val lowerLimit: Long? = null,

)

data class UserCustomProps(
    @Contextual @BsonId val _id: ObjectId = ObjectId(),

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    @Contextual val teamId: ObjectId,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val propName: String,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val valuesDataType: SupportedDataTypes,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val possibleValues: List<Any>
)

val defaultUserStatusList = listOf(UserStatus(statusName = "Available"),
    UserStatus(statusName = "Busy", upperLimit = 30*60*1000L, lowerLimit = 0), //max and default 30 min
    UserStatus(statusName = "Unavailable", upperLimit = 31*24*60*60*1000L, lowerLimit = 1*24*60*60*1000L) //max 31 days, default 1 day
)

fun getDefaultUserStatus() = defaultUserStatusList.first()