package com.taxplanner.dto.response

import com.taxplanner.domain.enums.OrganizationRole
import java.time.LocalDateTime
import java.util.*

data class OrganizationResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val logoUrl: String?,
    val memberCount: Int,
    val projectCount: Int,
    val userRole: OrganizationRole,
    val createdAt: LocalDateTime
) 