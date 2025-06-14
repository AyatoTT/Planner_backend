package com.taxplanner.dto.request

import jakarta.validation.constraints.Size

data class UpdateChecklistRequest(
    @field:Size(max = 200, message = "Title must not exceed 200 characters")
    val title: String?,
    val isCompleted: Boolean?
) 