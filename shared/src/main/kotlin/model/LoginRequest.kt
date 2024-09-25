package com.redwater.model


data class LoginRequest(
    val orgId: String,
    val userEmail: String,
    val password: String
)

data class SignUpRequest(
    val userEmail: String,
    val password: String,
    val userName: String,
    val role: SystemRole,
    val orgId: String
)