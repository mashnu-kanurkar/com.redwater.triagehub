package com.redwater.model

import kotlinx.serialization.Contextual
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class InvitationRequest(
    val toEmail: String,
    val role: SystemRole
)


data class Invitation(
    @Contextual @BsonId val _id: ObjectId = ObjectId(),

    val token: String,

    val toEmail: String,

    val role: SystemRole,

    val createdOn: Long,

    val validTill: Long,

    @Contextual val orgId: ObjectId,

    val invitedByEmail: String,

    val invitationStatus: InvitationStatus
)

enum class InvitationStatus{
    INITIATED,
    EMAIL_SENT,
    EMAIL_FAILED,
    ACCEPTED,
    REJECTED,
    EXPIRED,
    FINISHED //once user completes the signup process
}