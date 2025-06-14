package com.taxplanner.dto.response

import java.util.*

data class TaskStatusResponse(
    val id: UUID,
    val name: String,
    val color: String,
    val orderIndex: Int,
    val boardId: UUID,
    val isFinal: Boolean = false
) 