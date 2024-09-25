package com.redwater.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.redwater.model.Organisation
import com.redwater.mongodb.MongoDBUtils
import kotlinx.coroutines.flow.firstOrNull
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class OrganisationRepositoryImpl(databaseOrgId: String): OrganisationRepository {
    private val database: MongoDatabase = MongoDBUtils.getDatabaseByOrgId(databaseOrgId)

    companion object {
        private const val ORG_COLLECTION = "organisation"
    }
    override suspend fun create(organisation: Organisation): BsonValue? {
        try {
            
            val result = MongoDBUtils.getCollection<Organisation>(database, ORG_COLLECTION, Organisation::class.java)
                .insertOne(organisation)
            return result.insertedId
        }catch (e: Exception){
            System.err.println("Unable to create due to an error: $e")
        }
        return null
    }

    override suspend fun get(orgId: ObjectId): Organisation? {
        return try {
            MongoDBUtils.getCollection<Organisation>(database, ORG_COLLECTION, Organisation::class.java).withDocumentClass<Organisation>()
                .find(Filters.eq("_id", orgId)).firstOrNull()

        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun update(orgId: ObjectId, updates: Bson): Long {
        try {
            val query = Filters.eq("_id", orgId)
            val options = UpdateOptions().upsert(false)
            val result = MongoDBUtils.getCollection<Organisation>(database, ORG_COLLECTION, Organisation::class.java)
                .updateOne(query, updates, options)
            return result.modifiedCount
        }catch (e: Exception){
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun delete(orgId: ObjectId): Long {
        return try {
            val result = MongoDBUtils.getCollection<Organisation>(database, ORG_COLLECTION, Organisation::class.java)
                .deleteOne(Filters.eq("_id", orgId))
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: $e")
            0
        }
    }
}