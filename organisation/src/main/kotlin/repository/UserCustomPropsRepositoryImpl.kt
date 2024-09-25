package com.redwater.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.redwater.model.UserCustomProps
import com.redwater.mongodb.MongoDBUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class UserCustomPropsRepositoryImpl(databaseOrgId: String): UserCustomPropRepository {
    private val database: MongoDatabase = MongoDBUtils.getDatabaseByOrgId(databaseOrgId)

    companion object {
        private const val USER_CUSTOM_PROP_COLLECTION = "user_custom_props"
    }
    override suspend fun getProp(userCustomPropsId: ObjectId): UserCustomProps? {
        return try {
            MongoDBUtils.getCollection<UserCustomProps>(database, USER_CUSTOM_PROP_COLLECTION, UserCustomProps::class.java).withDocumentClass<UserCustomProps>()
                .find(Filters.eq(UserCustomProps::_id.name, userCustomPropsId)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun getAll(): List<UserCustomProps>? {
        return try {
            MongoDBUtils.getCollection<UserCustomProps>(database, USER_CUSTOM_PROP_COLLECTION, UserCustomProps::class.java).withDocumentClass<UserCustomProps>()
                .find(Filters.empty()).toList()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun getPropByName(name: String): UserCustomProps? {
        return try {
            MongoDBUtils.getCollection<UserCustomProps>(database, USER_CUSTOM_PROP_COLLECTION, UserCustomProps::class.java).withDocumentClass<UserCustomProps>()
                .find(Filters.eq(UserCustomProps::propName.name, name)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun create(userCustomProps: UserCustomProps): BsonValue? {
        try {
            val result = MongoDBUtils.getCollection<UserCustomProps>(database, USER_CUSTOM_PROP_COLLECTION, UserCustomProps::class.java)
                .insertOne(userCustomProps)
            return result.insertedId
        }catch (e: Exception){
            System.err.println("Unable to create due to an error: $e")
        }
        return null
    }

    override suspend fun update(userCustomPropsId: ObjectId, updates: Bson): Long {
        try {
            val query = Filters.eq(UserCustomProps::_id.name, userCustomPropsId)
            val options = UpdateOptions().upsert(false)
            val result = MongoDBUtils.getCollection<UserCustomProps>(database, USER_CUSTOM_PROP_COLLECTION, UserCustomProps::class.java)
                .updateOne(query, updates, options)
            return result.modifiedCount
        }catch (e: Exception){
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun delete(userCustomPropsId: ObjectId): Long {
        return try {
            val result = MongoDBUtils.getCollection<UserCustomProps>(database, USER_CUSTOM_PROP_COLLECTION, UserCustomProps::class.java)
                .deleteOne(Filters.eq(UserCustomProps::_id.name, userCustomPropsId))
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: $e")
            0
        }
    }
}