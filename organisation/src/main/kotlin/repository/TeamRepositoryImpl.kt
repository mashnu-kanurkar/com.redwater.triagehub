package com.redwater.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.redwater.model.Team
import com.redwater.mongodb.MongoDBUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class TeamRepositoryImpl(databaseOrgId: String): TeamRepository {
    private val database: MongoDatabase = MongoDBUtils.getDatabaseByOrgId(databaseOrgId)

    companion object {
        private const val TEAM_COLLECTION = "teams"
    }

    override suspend fun getAllTeams(): List<Team>? {
        return try {
            MongoDBUtils.getCollection<Team>(database, TEAM_COLLECTION, Team::class.java).withDocumentClass<Team>()
                .find(Filters.empty()).toList()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun getTeam(teamId: ObjectId): Team? {
        return try {
            MongoDBUtils.getCollection<Team>(database, TEAM_COLLECTION, Team::class.java).withDocumentClass<Team>()
                .find(Filters.eq("_id", teamId)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun getTeamByName(teamName: String): Team? {
        return try {
            MongoDBUtils.getCollection<Team>(database, TEAM_COLLECTION, Team::class.java).withDocumentClass<Team>()
                .find(Filters.eq(Team::name.name, teamName)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun createTeam(team: Team): BsonValue? {
        try {
            val result = MongoDBUtils.getCollection<Team>(database, TEAM_COLLECTION, Team::class.java)
                .insertOne(team)
            return result.insertedId
        }catch (e: Exception){
            System.err.println("Unable to create due to an error: $e")
        }
        return null
    }

    override suspend fun update(teamId: ObjectId, updates: Bson): Long {
        try {
            val query = Filters.eq("_id", teamId)
            val options = UpdateOptions().upsert(false)
            val result = MongoDBUtils.getCollection<Team>(database, TEAM_COLLECTION, Team::class.java)
                .updateOne(query, updates, options)
            return result.modifiedCount
        }catch (e: Exception){
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun delete(teamId: ObjectId): Long {
        return try {
            val result = MongoDBUtils.getCollection<Team>(database, TEAM_COLLECTION, Team::class.java)
                .deleteOne(Filters.eq("_id", teamId))
            return result.deletedCount
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: $e")
            0
        }
    }
}