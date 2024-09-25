package com.redwater.repository

import com.redwater.model.UserCustomProps
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

interface UserCustomPropRepository {

    suspend fun getProp(userCustomPropsId: ObjectId): UserCustomProps?

    suspend fun getAll(): List<UserCustomProps>?

    suspend fun getPropByName(name: String): UserCustomProps?

    suspend fun create(userCustomProps: UserCustomProps): BsonValue?

    suspend fun update(userCustomPropsId: ObjectId, updates: Bson): Long

    suspend fun delete(userCustomPropsId: ObjectId): Long
}