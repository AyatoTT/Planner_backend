package com.taxplanner.dto.response

import java.time.LocalDateTime
import java.util.*

data class ChecklistResponse(
    val id: UUID,
    val title: String,
    val isCompleted: Boolean,
    val taskId: UUID,
    val createdAt: LocalDateTime
) 