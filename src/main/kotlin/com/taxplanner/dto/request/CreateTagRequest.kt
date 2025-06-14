package com.taxplanner.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateTagRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 50, message = "Name must not exceed 50 characters")
    val name: String,
    
    @field:Size(max = 7, message = "Color must not exceed 7 characters")
    val color: String? = null
) 