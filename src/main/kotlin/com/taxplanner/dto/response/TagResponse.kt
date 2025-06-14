package com.taxplanner.dto.response

import java.util.*

data class TagResponse(
    val id: UUID,
    val name: String,
    val color: String,
    val projectId: UUID
) 