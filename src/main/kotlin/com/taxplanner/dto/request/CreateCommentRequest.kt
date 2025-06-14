package com.taxplanner.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateCommentRequest(
    @field:NotBlank(message = "Content is required")
    @field:Size(max = 2000, message = "Content must not exceed 2000 characters")
    val content: String
) 