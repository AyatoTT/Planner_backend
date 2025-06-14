package com.taxplanner.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Min

data class CreateTaskStatusRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 100, message = "Name must not exceed 100 characters")
    val name: String,
    
    @field:Size(max = 7, message = "Color must not exceed 7 characters")
    val color: String? = null,
    
    @field:NotNull(message = "Order index is required")
    @field:Min(value = 0, message = "Order index must be non-negative")
    val orderIndex: Int,
    
    val isFinal: Boolean? = false
) 