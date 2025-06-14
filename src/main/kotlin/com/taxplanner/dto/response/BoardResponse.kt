package com.taxplanner.dto.response

import com.taxplanner.domain.enums.BoardViewType
import java.time.LocalDateTime
import java.util.*

data class BoardResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val viewType: BoardViewType,
    val project: ProjectSummaryResponse,
    val statuses: List<TaskStatusResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 