package com.taxplanner.dto.request

import com.taxplanner.domain.enums.BoardViewType
import jakarta.validation.constraints.Size

data class UpdateBoardRequest(
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String? = null,

    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    val description: String? = null,

    val viewType: BoardViewType? = null
) 