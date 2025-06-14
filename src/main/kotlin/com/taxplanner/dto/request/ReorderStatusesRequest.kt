package com.taxplanner.dto.request

import jakarta.validation.constraints.NotEmpty
import java.util.*

data class ReorderStatusesRequest(
    @field:NotEmpty(message = "Status order list cannot be empty")
    val statusOrders: List<StatusOrderItem>
)

data class StatusOrderItem(
    val statusId: UUID,
    val orderIndex: Int
) 