package com.redwater.repository

import com.mongodb.client.model.UpdateOneModel
import com.redwater.model.User
import org.bson.BsonValue
import org.bson.types.ObjectId

interface AnalystRepository {
    suspend fun insertOne(databaseUser: User, hashedPassword: String): BsonValue?
    suspend fun deleteById(objectIds: List<ObjectId>): Long
    suspend fun findById(objectId: ObjectId): User?
    suspend fun updateMany(updateOperations: List<UpdateOneModel<User>>): Int
    suspend fun findByEmail(email: String): User?
    suspend fun findAll(): List<User>?
    suspend fun findByTeamId(teamId: ObjectId): List<User>
    suspend fun findByOrgId(orgId: ObjectId): List<User>
    suspend fun addUserToTeam(userIds: List<ObjectId>, teamId: ObjectId): Long
    suspend fun removeFromTeam(userIds: List<ObjectId>, teamId: ObjectId): Long
}