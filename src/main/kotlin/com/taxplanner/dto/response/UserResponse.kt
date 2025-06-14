package com.taxplanner.dto.response

import java.time.LocalDateTime
import java.util.*

data class UserResponse(
    val id: UUID,
    val email: String,
    val name: String,
    val avatarUrl: String?,
    val emailVerified: Boolean,
    val createdAt: LocalDateTime
) 