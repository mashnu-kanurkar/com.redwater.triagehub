package com.redwater.authentication

import com.redwater.model.*
import com.redwater.repository.AnalystRepositoryImpl
import com.redwater.repository.InternalUserRepositoryImpl
import com.redwater.repository.PasswordRepositoryImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory


class AuthenticationProvider {
    private val logger = LoggerFactory.getLogger(AuthenticationProvider::class.java)
    suspend fun loginInternalUser(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        val loginRequest = call.receive<LoginRequest>()
        val internalUserRepository = InternalUserRepositoryImpl()
        val internalUser = internalUserRepository.getByEmail(loginRequest.userEmail)
        if (internalUser == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("User not found"))
        }else{
            val passwordMatch = verifyPassword(loginRequest.password, internalUser.hashedPassword)
            if (passwordMatch){
                logger.info("Login success: internal user => $internalUser")
                call.respond(status = HttpStatusCode.OK, message = internalUser)
            }else{
                call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("email or password mismatch"))
            }
        }
    }

    suspend fun login(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        val loginRequest = call.receive<LoginRequest>()
        val analystRepository = AnalystRepositoryImpl(loginRequest.orgId)
        val user = analystRepository.findByEmail(loginRequest.userEmail)
        if (user == null){
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("User not found"))
        }else {
            val passwordRepository = PasswordRepositoryImpl()
            val password = passwordRepository.getPassword(user._id)
            if (password == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = Response.Failed("Unable to authenticate user")
                )
            }
            //we don't need to hash the incoming password again, bcrypt will check if it matches with stored hash
            val passwordSuccess = verifyPassword(loginRequest.password, password!!.password)
            if (passwordSuccess) {
                call.respond(status = HttpStatusCode.OK, message = Response.Success(data = user))
            }else{
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = Response.Failed("Unable to authenticate user")
                )
            }
        }
    }

    suspend fun registerUser(pipeline: PipelineContext<Unit, ApplicationCall>){
        val call = pipeline.call
        val signUpRequest = call.receive<SignUpRequest>()
        val hashedPassword = hashPassword(signUpRequest.password)
        val analystRepository = AnalystRepositoryImpl(signUpRequest.orgId)
        val userExist = analystRepository.findByEmail(signUpRequest.userEmail)

        if (userExist == null){
            val databaseUser = User(
                email = signUpRequest.userEmail,
                name = signUpRequest.userName,
                role = signUpRequest.role,
                organisationId = ObjectId(signUpRequest.orgId)
            )
            val userId = analystRepository.insertOne(databaseUser, hashedPassword)
            if (userId == null){
                call.respond(status = HttpStatusCode.InternalServerError, message = Response.Failed(error = "unable to create user"))
            }else{
                call.respond(status = HttpStatusCode.OK, message =  Response.Success(data = databaseUser))
            }
        }else{
            call.respond(status = HttpStatusCode.BadRequest, message = Response.Failed("User already exist. Login to continue"))
        }
    }

    private fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }

    private fun hashPassword(password: String): String {
        val salt = BCrypt.gensalt(12) // 12 is the default cost factor, which is secure and performant for most cases
        return BCrypt.hashpw(password, salt)
    }
}