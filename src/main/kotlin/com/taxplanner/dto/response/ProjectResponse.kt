package com.taxplanner.dto.response

import com.taxplanner.domain.enums.OrganizationRole
import java.time.LocalDateTime
import java.util.*

data class ProjectResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val colorTheme: String?,
    val organization: OrganizationSummaryResponse,
    val memberCount: Int,
    val boardCount: Int,
    val tagCount: Int,
    val userRole: OrganizationRole,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 