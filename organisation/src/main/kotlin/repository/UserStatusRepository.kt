package com.redwater.repository

import com.redwater.model.UserStatus
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

interface UserStatusRepository {

    suspend fun getStatus(userStatusId: ObjectId): UserStatus?

    suspend fun getAll(): List<UserStatus>?

    suspend fun getStatusByName(name: String): UserStatus?

    suspend fun create(userStatus: UserStatus): BsonValue?

    suspend fun update(userStatusId: ObjectId, updates: Bson): Long

    suspend fun delete(userStatusId: ObjectId): Long
}