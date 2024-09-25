package com.redwater.repository

import com.mongodb.client.model.UpdateOneModel
import com.redwater.model.InternalUser
import org.bson.BsonValue
import org.bson.types.ObjectId

interface InternalUserRepository {

    suspend fun getByEmail(email: String): InternalUser?

    suspend fun create(internalUser: InternalUser): BsonValue?

    suspend fun get(internalUserId: ObjectId): InternalUser?

    suspend fun getAll(): List<InternalUser>?

    suspend fun update(updateOperations: List<UpdateOneModel<InternalUser>>): Int

    suspend fun delete(internalUserIds: List<ObjectId>): Long
}