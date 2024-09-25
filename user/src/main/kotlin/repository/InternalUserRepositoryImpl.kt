package com.redwater.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.redwater.model.InternalUser
import com.redwater.mongodb.MongoDBUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue
import org.bson.types.ObjectId

class InternalUserRepositoryImpl: InternalUserRepository{
    private val database: MongoDatabase = MongoDBUtils.getInternalDatabase()

    companion object {
        private const val INTERNAL_USER_COLLECTION = "internal_user"
    }

    override suspend fun getByEmail(email: String): InternalUser? {
        return try {
            MongoDBUtils.getCollection<InternalUser>(database,
                INTERNAL_USER_COLLECTION, InternalUser::class.java)
                .find(Filters.eq(InternalUser::email.name, email)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get record due to an error: $e")
            null
        }
    }
    override suspend fun create(internalUser: InternalUser): BsonValue? {
        return try {
            val userInsertResult = MongoDBUtils.getCollection<InternalUser>(database,
                INTERNAL_USER_COLLECTION, InternalUser::class.java).insertOne(internalUser)
            userInsertResult.insertedId
        }catch (e: Exception){
            System.err.println("Unable to insert record due to an error: $e")
            null
        }
    }

    override suspend fun get(internalUserId: ObjectId): InternalUser? {
        return try {
            MongoDBUtils.getCollection<InternalUser>(database,
                INTERNAL_USER_COLLECTION, InternalUser::class.java)
                .find(Filters.eq(InternalUser::_id.name, internalUserId)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get record due to an error: $e")
            null
        }
    }

    override suspend fun getAll(): List<InternalUser>? {
        return try {
            MongoDBUtils.getCollection<InternalUser>(database,
                INTERNAL_USER_COLLECTION, InternalUser::class.java)
                .find(Filters.empty()).toList()
        }catch (e: Exception){
            System.err.println("Unable to get record due to an error: $e")
            null
        }
    }

    override suspend fun update(updateOperations: List<UpdateOneModel<InternalUser>>): Int {
        return try {
            val result = MongoDBUtils.getCollection<InternalUser>(database,
                INTERNAL_USER_COLLECTION, InternalUser::class.java)
                .bulkWrite(updateOperations)
            result.modifiedCount
        }catch (e: Exception){
            System.err.println("Unable to update record due to an error: $e")
            0
        }
    }

    override suspend fun delete(internalUserIds: List<ObjectId>): Long {
        return try {
            val userInsertResult = MongoDBUtils.getCollection<InternalUser>(database,
                INTERNAL_USER_COLLECTION, InternalUser::class.java)
                .deleteMany(Filters.all(InternalUser::_id.name, internalUserIds) )
            userInsertResult.deletedCount
        }catch (e: Exception){
            System.err.println("Unable to delete record due to an error: $e")
            0
        }
    }
}