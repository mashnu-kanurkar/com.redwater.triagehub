package com.redwater.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.redwater.model.Invitation
import com.redwater.model.InvitationStatus
import com.redwater.mongodb.MongoDBUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue
import org.bson.types.ObjectId

class InvitationRepositoryImpl(orgId: String) : InvitationRepository {
    private val database = MongoDBUtils.getDatabaseByOrgId(orgId)
    companion object{
        private const val INVITATION_COLLECTION = "invitation"
    }
    override suspend fun addInvitation(invitationList: List<Invitation>): Map<Int, BsonValue>? {
        return try {
            val result = MongoDBUtils.getCollection<Invitation>(database,
                INVITATION_COLLECTION, Invitation::class.java).insertMany(invitationList)
            result.insertedIds
        }catch (e: Exception){
            System.err.println("Unable to insert record due to an error: $e")
            null
        }
    }

    override suspend fun updateInvitation(invitationId: ObjectId, invitationStatus: InvitationStatus): Long {
        return try {
            val update = Updates.set(Invitation::invitationStatus.name, invitationStatus)
            val query = Filters.eq("_id", invitationId)
            val options = UpdateOptions().upsert(false)
            val result = MongoDBUtils.getCollection<Invitation>(database,
                INVITATION_COLLECTION, Invitation::class.java)
                .updateOne(query, update, options)
            result.modifiedCount
        }catch (e: Exception){
            System.err.println("Unable to update record due to an error: $e")
            0
        }
    }

    override suspend fun getInvitation(invitationId: ObjectId): Invitation? {
        return try {
             val result = MongoDBUtils.getCollection<Invitation>(database,
                INVITATION_COLLECTION, Invitation::class.java)
                .find(Filters.eq("_id", invitationId)).firstOrNull()
            result
        }catch (e: Exception){
            System.err.println("Unable to get record due to an error: $e")
            null
        }
    }

    override suspend fun getNewInvitations(): List<Invitation>? {
        return try {
            val result = MongoDBUtils.getCollection<Invitation>(database,
                INVITATION_COLLECTION, Invitation::class.java)
                .find(Filters.all(Invitation::invitationStatus.name, InvitationStatus.INITIATED))
            result.toList()
        }catch (e: Exception){
            System.err.println("Unable to get record due to an error: $e")
            null
        }
    }

    override suspend fun validateToken(token: String): Invitation? {
        return try {
            val result = MongoDBUtils.getCollection<Invitation>(database,
                INVITATION_COLLECTION, Invitation::class.java)
                .find(Filters.eq(Invitation::token.name, token)).firstOrNull()
            result
        }catch (e: Exception){
            System.err.println("Unable to get record due to an error: $e")
            null
        }
    }

}