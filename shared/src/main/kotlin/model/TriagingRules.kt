package com.redwater.model

import com.redwater.utils.UpdateAllowedBy
import kotlinx.serialization.Contextual
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class TriagingRules(
    @Contextual @BsonId val _id: ObjectId = ObjectId(),

    @Contextual val teamId: ObjectId,

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val allConditions: List<Condition>,  // must meet all these conditions

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val orConditions: List<Condition>,  // meet any of these conditions

    @UpdateAllowedBy(RoleNames.creator, RoleNames.admin, RoleNames.teamAdmin)
    val actions: List<Action>    // Actions to take if the conditions are met

)

data class Condition(
    val property: String,  // Property to check (e.g., "ticketType", "userStatus")
    val operator: Operator, // Comparison operator (e.g., EQUALS, NOT_EQUALS, GREATER_THAN)
    val value: Any          // The value to compare against
)

enum class Operator {
    EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN
}

data class Action(
    val property: String,  // Action property (e.g., "assignTicket", "skipAssignment")
    val value: Any         // Value associated with the action
)

val PredefinedActions = listOf(
    Action("assignTicket", true),
    Action("sendEmailAlert", true),
    Action("sendWebhookAlert", true)
)