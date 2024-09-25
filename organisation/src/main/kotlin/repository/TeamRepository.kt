package com.redwater.repository

import com.redwater.model.Team
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

interface TeamRepository {

    suspend fun getAllTeams(): List<Team>?

    suspend fun getTeam(teamId: ObjectId): Team?

    suspend fun getTeamByName(teamName: String): Team?

    suspend fun createTeam(team: Team): BsonValue?

    suspend fun update(teamId: ObjectId, updates: Bson): Long

    suspend fun delete(teamId: ObjectId): Long
}