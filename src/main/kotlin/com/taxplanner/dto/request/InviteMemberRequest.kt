package com.taxplanner.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class InviteMemberRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,
    
    @field:NotBlank(message = "Role is required")
    val role: String
) 