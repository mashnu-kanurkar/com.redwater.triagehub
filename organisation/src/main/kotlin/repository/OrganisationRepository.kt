package com.redwater.repository

import com.redwater.model.Organisation
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

interface OrganisationRepository {

    suspend fun create(organisation: Organisation):BsonValue?
    suspend fun get(orgId: ObjectId): Organisation?
    suspend fun update(orgId: ObjectId, updates: Bson): Long
    suspend fun delete(orgId: ObjectId): Long
}