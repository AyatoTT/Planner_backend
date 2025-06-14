package com.taxplanner.dto.response

import java.util.*

data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val user: UserResponse
) 