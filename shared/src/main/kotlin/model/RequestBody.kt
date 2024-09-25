package com.redwater.model

data class UpdateData(
    val entityId: String, //userId or orgId or teamId or ticketPropId or statusId or userPropId which needs to be updated
    val type: UpdateDataType, //update or create
    val scope: UpdateDataScope, //user or org or team
    val data: Map<String, String>
)
data class DeleteData(
    val entityId: String, //userId or orgId or teamId which needs to be updated
    val scope: UpdateDataScope, //user or org or team
)


data class RequestBody<T>(
    val d: List<T>
)
enum class UpdateDataType{
    UPDATE,
    CREATE
}

enum class UpdateDataScope{
    USER,
    ORG,
    TEAM
}


/***
 * {
 * "d":[
 * {"_id":"user id",
 * "type":"update",
 * "scope":"user",
 * data:{
 * "role":"admin" // any user property except _id, email and organisationId
 * }
 * }
 * ]
 * }
 */
