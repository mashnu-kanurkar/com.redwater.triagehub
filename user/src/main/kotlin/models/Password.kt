package com.redwater.models

import kotlinx.serialization.Contextual
import org.bson.types.ObjectId

data class Password(
    @Contextual val userId: ObjectId,
    val password: String,
    val lastUpdate: Long,
)

data class UserCredential(
    @Contextual val userCredId: ObjectId,
    val email: String,
    @Contextual val passwordRef: ObjectId
)
