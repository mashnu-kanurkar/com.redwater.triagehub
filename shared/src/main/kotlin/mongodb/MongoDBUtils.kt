package com.redwater.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.redwater.model.SystemRoleCodec
import org.bson.codecs.configuration.CodecRegistries


/**
 * Establishes connection with a MongoDB database.
 *
 * The following configuration properties (in application.yaml/application.conf) can be specified:
 * * `db.mongo.user` username for your database
 * * `db.mongo.password` password for the user
 * * `db.mongo.host` host that will be used for the database connection
 * * `db.mongo.port` port that will be used for the database connection
 * * `db.mongo.maxPoolSize` maximum number of connections to a MongoDB server
 * * `db.mongo.database.name` name of the database
 *
 * IMPORTANT NOTE: in order to make MongoDB connection working, you have to start a MongoDB server first.
 * See the instructions here: https://www.mongodb.com/docs/manual/administration/install-community/
 * all the parameters above
 *
 * @returns [MongoDatabase] instance
 * */
object MongoDBUtils {
    private fun getClientSettings(): MongoClientSettings {
        val user = System.getenv("db_mongo_user")
        val password = System.getenv("db_mongo_password")
        val appName = System.getenv("db_mongo_appname")
        val host = System.getenv("db_mongo_host") ?: "127.0.0.1"
        val port = System.getenv("db_mongo_port") ?: "27017"
        val maxPoolSize = 20

        val credentials = user?.let { userVal ->
            password?.let { passwordVal ->
                "$userVal:$passwordVal"
            }
        }.orEmpty()
        val connectionString = "mongodb+srv://$credentials@$host/?maxPoolSize=$maxPoolSize&w=majority&appName=$appName"

        val serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build()

        val codecRegistry = CodecRegistries.fromRegistries(
            CodecRegistries.fromCodecs(SystemRoleCodec()),
            MongoClientSettings.getDefaultCodecRegistry()
        )
        val mongoClientSettings = MongoClientSettings.builder()
            .codecRegistry(codecRegistry)
            .applyConnectionString(ConnectionString(connectionString))
            .serverApi(serverApi)
            .build()
        return mongoClientSettings
    }
    private val client = MongoClient.create(getClientSettings())

    fun getClient(): MongoClient {
        return client
    }

    fun getDatabaseByOrgId(orgUniqueId: String): MongoDatabase {
        return client.getDatabase(orgUniqueId)
    }
    fun getUserDatabase(): MongoDatabase{
        return client.getDatabase("users")
    }

    fun getInternalDatabase(): MongoDatabase{
        return client.getDatabase("internal")
    }

    fun <T : Any> getCollection(database: MongoDatabase, collectionName: String, resultClass: Class<T>): MongoCollection<T>{
        return database.getCollection(collectionName, resultClass)
    }

}