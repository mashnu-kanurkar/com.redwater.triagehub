package com.redwater.repository

import com.redwater.model.Invitation
import com.redwater.model.InvitationStatus
import org.bson.BsonValue
import org.bson.types.ObjectId

interface InvitationRepository {

    suspend fun addInvitation(invitationList: List<Invitation>): Map<Int, BsonValue>?
    suspend fun updateInvitation(invitationId: ObjectId, invitationStatus: InvitationStatus): Long
    suspend fun getInvitation(invitationId: ObjectId): Invitation?
    suspend fun getNewInvitations(): List<Invitation>?
    suspend fun validateToken(token: String): Invitation?
}