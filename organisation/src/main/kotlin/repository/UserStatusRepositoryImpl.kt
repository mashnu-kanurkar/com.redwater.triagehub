package com.redwater.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.redwater.model.UserStatus
import com.redwater.mongodb.MongoDBUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class UserStatusRepositoryImpl(databaseOrgId: String):UserStatusRepository {
    private val database: MongoDatabase = MongoDBUtils.getDatabaseByOrgId(databaseOrgId)

    companion object {
        private const val USER_STATUS_COLLECTION = "user_status"
    }
    override suspend fun getStatus(userStatusId: ObjectId): UserStatus? {
        return try {
            MongoDBUtils.getCollection<UserStatus>(database, USER_STATUS_COLLECTION, UserStatus::class.java)
                .withDocumentClass<UserStatus>()
                .find(Filters.eq(UserStatus::_id.name, userStatusId)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun getAll(): List<UserStatus>? {
        return try {
            MongoDBUtils.getCollection<UserStatus>(database, USER_STATUS_COLLECTION, UserStatus::class.java)
                .withDocumentClass<UserStatus>()
                .find(Filters.empty()).toList()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun getStatusByName(name: String): UserStatus?{
        return try {
            MongoDBUtils.getCollection<UserStatus>(database, USER_STATUS_COLLECTION, UserStatus::class.java)
                .withDocumentClass<UserStatus>()
                .find(Filters.eq(UserStatus::statusName.name, name)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }

    }

    override suspend fun create(userStatus: UserStatus): BsonValue? {
        try {
            val result = MongoDBUtils.getCollection<UserStatus>(database, USER_STATUS_COLLECTION, UserStatus::class.java)
                .insertOne(userStatus)
            return result.insertedId
        }catch (e: Exception){
            System.err.println("Unable to create due to an error: $e")
        }
        return null
    }

    override suspend fun update(userStatusId: ObjectId, updates: Bson): Long {
        try {
            val query = Filters.eq(UserStatus::_id.name, userStatusId)
            val options = UpdateOptions().upsert(false)
            val result = MongoDBUtils.getCollection<UserStatus>(database, USER_STATUS_COLLECTION, UserStatus::class.java)
                .updateOne(query, updates, options)
            return result.modifiedCount
        }catch (e: Exception){
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun delete(userStatusId: ObjectId): Long {
        return try {
            val result = MongoDBUtils.getCollection<UserStatus>(database, USER_STATUS_COLLECTION, UserStatus::class.java)
                .deleteOne(Filters.eq(UserStatus::_id.name, userStatusId))
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: $e")
            0
        }
    }
}