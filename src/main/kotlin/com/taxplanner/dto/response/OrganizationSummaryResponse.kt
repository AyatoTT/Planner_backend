package com.taxplanner.dto.response

import java.util.*

data class OrganizationSummaryResponse(
    val id: UUID,
    val name: String,
    val logoUrl: String?
) 