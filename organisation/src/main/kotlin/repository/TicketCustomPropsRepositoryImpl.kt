package com.redwater.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.redwater.model.TicketCustomProps
import com.redwater.mongodb.MongoDBUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class TicketCustomPropsRepositoryImpl(databaseOrgId: String): TicketCustomPropsRepository {
    private val database: MongoDatabase = MongoDBUtils.getDatabaseByOrgId(databaseOrgId)

    companion object {
        private const val TICKET_CUSTOM_PROP_COLLECTION = "ticket_custom_props"
    }
    override suspend fun getProp(ticketCustomPropsId: ObjectId): TicketCustomProps? {
        return try {
            MongoDBUtils.getCollection<TicketCustomProps>(database, TICKET_CUSTOM_PROP_COLLECTION, TicketCustomProps::class.java)
                .withDocumentClass<TicketCustomProps>()
                .find(Filters.eq(TicketCustomProps::_id.name, ticketCustomPropsId)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun getAll(): List<TicketCustomProps>? {
        return try {
            MongoDBUtils.getCollection<TicketCustomProps>(database, TICKET_CUSTOM_PROP_COLLECTION, TicketCustomProps::class.java)
                .withDocumentClass<TicketCustomProps>()
                .find(Filters.empty()).toList()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun getPropByName(name: String): TicketCustomProps? {
        return try {
            MongoDBUtils.getCollection<TicketCustomProps>(database, TICKET_CUSTOM_PROP_COLLECTION, TicketCustomProps::class.java)
                .withDocumentClass<TicketCustomProps>()
                .find(Filters.eq(TicketCustomProps::propName.name, name)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun create(ticketCustomProps: TicketCustomProps): BsonValue? {
        try {
            val result = MongoDBUtils.getCollection<TicketCustomProps>(database, TICKET_CUSTOM_PROP_COLLECTION, TicketCustomProps::class.java)
                .insertOne(ticketCustomProps)
            return result.insertedId
        }catch (e: Exception){
            System.err.println("Unable to create due to an error: $e")
        }
        return null
    }

    override suspend fun update(ticketCustomPropsId: ObjectId, updates: Bson): Long {
        try {
            val query = Filters.eq(TicketCustomProps::_id.name, ticketCustomPropsId)
            val options = UpdateOptions().upsert(false)
            val result = MongoDBUtils.getCollection<TicketCustomProps>(database, TICKET_CUSTOM_PROP_COLLECTION, TicketCustomProps::class.java)
                .updateOne(query, updates, options)
            return result.modifiedCount
        }catch (e: Exception){
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun delete(ticketCustomPropsId: ObjectId): Long {
        return try {
            val result = MongoDBUtils.getCollection<TicketCustomProps>(database, TICKET_CUSTOM_PROP_COLLECTION, TicketCustomProps::class.java)
                .deleteOne(Filters.eq(TicketCustomProps::_id.name, ticketCustomPropsId))
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: $e")
            0
        }
    }
}