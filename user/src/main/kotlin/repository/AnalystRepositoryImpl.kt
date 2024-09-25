package com.redwater.repository

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.redwater.model.User
import com.redwater.models.Password
import com.redwater.mongodb.MongoDBUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue
import org.bson.types.ObjectId

class AnalystRepositoryImpl(databaseOrgId: String): AnalystRepository {
    private val database: MongoDatabase = MongoDBUtils.getDatabaseByOrgId(databaseOrgId)

    companion object {
        private const val USER_COLLECTION = "user"
        private const val PASSWORD_COLLECTION = "password"
    }

    //inserts the new record (user + password) and if any one of them fail, aborts the transaction
    override suspend fun insertOne(databaseUser: User, hashedPassword: String): BsonValue? {
        val session = MongoDBUtils.getClient().startSession()
        return try {
            val userInsertResult = MongoDBUtils.getCollection<User>(database,
                USER_COLLECTION, User::class.java).insertOne(databaseUser)
            val userId = userInsertResult.insertedId!!.asObjectId().value
            val password = Password(userId = userId,
                password = hashedPassword,
                lastUpdate = System.currentTimeMillis())
            val passwordInsertResult = MongoDBUtils.getCollection<Password>(database,
                PASSWORD_COLLECTION, Password::class.java).insertOne(password)
            session.commitTransaction()
            userInsertResult.insertedId
        } catch (e: MongoException) {
            System.err.println("Unable to insert record due to an error: $e")
            session.abortTransaction()
            null
        }finally {
            session.close()
        }
        return null
    }

    override suspend fun deleteById(objectIds: List<ObjectId>): Long {
        return try {
            if (objectIds.size == 1){
                val result = MongoDBUtils.getCollection<User>(database, USER_COLLECTION,
                    User::class.java).deleteOne(Filters.eq("_id", objectIds.first()))
                return result.deletedCount
            }else{
                val result = MongoDBUtils.getCollection<User>(database, USER_COLLECTION,
                    User::class.java).deleteMany(Filters.all("_id", objectIds) )
                result.deletedCount
            }
        } catch (e: MongoException) {
            System.err.println("Unable to delete due to an error: $e")
            0
        }
    }

    override suspend fun findById(objectId: ObjectId): User? {
        return try {
            MongoDBUtils.getCollection<User>(database, USER_COLLECTION, User::class.java).withDocumentClass<User>()
                .find(Filters.eq("_id", objectId)).firstOrNull()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }

    }

    override suspend fun findByEmail(email: String): User? {
        return MongoDBUtils.getCollection(database, USER_COLLECTION, User::class.java).withDocumentClass<User>()
            .find(Filters.eq("email", email)).firstOrNull()
    }

    override suspend fun findAll(): List<User>? {
        return try {
            MongoDBUtils.getCollection<User>(database, USER_COLLECTION, User::class.java).withDocumentClass<User>()
                .find(Filters.empty()).toList()
        }catch (e: Exception){
            System.err.println("Unable to get due to an error: $e")
            null
        }
    }

    override suspend fun updateMany( updateOperations: List<UpdateOneModel<User>>): Int {
        try {
            val result =
                MongoDBUtils.getCollection<User>(database, USER_COLLECTION, User::class.java)
                    .bulkWrite(updateOperations)
            return result.modifiedCount
        } catch (e: MongoException) {
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun findByTeamId(teamId: ObjectId): List<User> {
        return MongoDBUtils.getCollection<User>(database, USER_COLLECTION, User::class.java).withDocumentClass<User>()
            .find(Filters.eq("teamId", teamId)).toList()
    }

    override suspend fun findByOrgId(orgId: ObjectId): List<User> {
        return MongoDBUtils.getCollection<User>(database, USER_COLLECTION, User::class.java).withDocumentClass<User>()
            .find(Filters.eq("organisationId", orgId)).toList()
    }

    override suspend fun addUserToTeam(userIds: List<ObjectId>, teamId: ObjectId): Long {
        try {
            val query = Filters.all("_id", userIds)
            val updates = Updates.combine(
                Updates.set(User::teamIds.name, teamId)
            )
            val options = UpdateOptions().upsert(false)
            val result = MongoDBUtils.getCollection<User>(database, USER_COLLECTION, User::class.java)
                .updateMany(query, updates, options)

            return result.modifiedCount
        } catch (e: MongoException) {
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }

    override suspend fun removeFromTeam(userIds: List<ObjectId>, teamId: ObjectId): Long {
        try {
            val query = Filters.all("_id", userIds)
            val updates = Updates.combine(
                Updates.set(User::teamIds.name, null)
            )
            val options = UpdateOptions().upsert(false)
            val result =
                MongoDBUtils.getCollection<User>(database, USER_COLLECTION, User::class.java)
                    .updateMany(query, updates, options)

            return result.modifiedCount
        } catch (e: MongoException) {
            System.err.println("Unable to update due to an error: $e")
        }
        return 0
    }
}