package com.taxplanner.dto.request

import com.taxplanner.domain.enums.TaskPriority
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

data class CreateTaskRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    val title: String,

    @field:Size(max = 2000, message = "Description must not exceed 2000 characters")
    val description: String? = null,

    val priority: TaskPriority? = null,

    val dueDate: LocalDateTime? = null,

    @field:NotNull(message = "Status ID is required")
    val statusId: UUID,

    val assigneeId: UUID? = null,

    @field:NotNull(message = "Board ID is required")
    val boardId: UUID,

    val orderIndex: Int? = null
) 