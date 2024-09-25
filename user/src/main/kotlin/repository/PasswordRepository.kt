package com.redwater.repository

import com.redwater.models.Password
import org.bson.BsonValue
import org.bson.types.ObjectId

interface PasswordRepository {
    suspend fun insert(password: Password): BsonValue?
    suspend fun update(password: Password): Long
    suspend fun getPassword(userId: ObjectId): Password?
    suspend fun delete(userId: ObjectId): Long
}