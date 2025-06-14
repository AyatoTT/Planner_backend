package com.taxplanner.dto.request

import com.taxplanner.domain.enums.TaskPriority
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

data class UpdateTaskRequest(
    @field:Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    val title: String? = null,

    @field:Size(max = 2000, message = "Description must not exceed 2000 characters")
    val description: String? = null,

    val priority: TaskPriority? = null,

    val dueDate: LocalDateTime? = null,

    val statusId: UUID? = null,

    val assigneeId: UUID? = null
) 