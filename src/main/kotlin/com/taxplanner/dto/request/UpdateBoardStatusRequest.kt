package com.taxplanner.dto.request

import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Min

data class UpdateBoardStatusRequest(
    @field:Size(max = 100, message = "Name must not exceed 100 characters")
    val name: String?,
    
    @field:Size(max = 7, message = "Color must not exceed 7 characters")
    val color: String?,
    
    @field:Min(value = 0, message = "Order index must be non-negative")
    val orderIndex: Int?,
    
    val isFinal: Boolean?
) 