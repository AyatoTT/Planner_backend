package com.taxplanner.dto.request

import jakarta.validation.constraints.NotNull
import java.util.*

// DTO for updating task status (drag & drop)
data class UpdateTaskStatusRequest(
    @field:NotNull(message = "Status ID is required")
    val statusId: UUID,
    
    val orderIndex: Int? = null
) 