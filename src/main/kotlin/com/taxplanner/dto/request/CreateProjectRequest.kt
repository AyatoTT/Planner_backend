package com.taxplanner.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class CreateProjectRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,

    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    val description: String? = null,

    @field:Size(max = 50, message = "Color theme must not exceed 50 characters")
    val colorTheme: String? = null,

    @field:NotNull(message = "Organization ID is required")
    val organizationId: UUID
) 