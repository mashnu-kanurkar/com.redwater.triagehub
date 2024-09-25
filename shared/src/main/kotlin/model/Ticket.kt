package com.redwater.model

import com.redwater.utils.UpdateAllowedBy
import kotlinx.serialization.Contextual
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Ticket(
    @Contextual @BsonId val _id: ObjectId = ObjectId(),

    val identifier: String,

    @Contextual val teamId: ObjectId,

    val createdAt: Long,

    val ticketTriageStatus: TicketTriageStatus,

    val ticketCustomPropsList: List<TicketCustomProps>? = null
)

data class TicketCustomProps(
    @Contextual @BsonId val _id: ObjectId = ObjectId(),

    @Contextual val teamId: ObjectId,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val propName: String,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val valuesDataType: SupportedDataTypes,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val possibleValues: List<Any>
)

enum class TicketTriageStatus{
    CREATED,
    TRIAGED,
    IN_TROUBLE,
    DELETED
}
