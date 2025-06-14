package com.taxplanner.dto.response

import java.util.*

data class ProjectSummaryResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val colorTheme: String?,
    val memberCount: Int,
    val taskCount: Int,
    val completedTaskCount: Int
) 