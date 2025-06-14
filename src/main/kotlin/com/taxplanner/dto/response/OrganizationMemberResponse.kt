package com.taxplanner.dto.response

import java.time.LocalDateTime
import java.util.*

data class OrganizationMemberResponse(
    val id: UUID,
    val userId: UUID,
    val userName: String,
    val userEmail: String,
    val role: String,
    val organizationId: UUID,
    val joinedAt: LocalDateTime
) 