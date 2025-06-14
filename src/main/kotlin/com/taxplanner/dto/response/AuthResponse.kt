package com.taxplanner.dto.response

import java.util.*

data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val user: UserResponse
)

data class UserResponse(
    val id: UUID,
    val email: String,
    val name: String,
    val avatarUrl: String?,
    val emailVerified: Boolean,
    val createdAt: java.time.LocalDateTime
) 