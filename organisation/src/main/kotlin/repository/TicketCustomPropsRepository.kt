package com.redwater.repository

import com.redwater.model.TicketCustomProps
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

interface TicketCustomPropsRepository {

    suspend fun getProp(ticketCustomPropsId: ObjectId): TicketCustomProps?

    suspend fun getAll(): List<TicketCustomProps>?

    suspend fun getPropByName(name: String): TicketCustomProps?

    suspend fun create(ticketCustomProps: TicketCustomProps): BsonValue?

    suspend fun update(ticketCustomPropsId: ObjectId, updates: Bson): Long

    suspend fun delete(ticketCustomPropsId: ObjectId): Long
}