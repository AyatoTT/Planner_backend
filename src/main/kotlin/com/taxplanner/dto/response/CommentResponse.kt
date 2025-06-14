package com.taxplanner.dto.response

import java.time.LocalDateTime
import java.util.*

data class CommentResponse(
    val id: UUID,
    val content: String,
    val authorId: UUID,
    val authorName: String,
    val taskId: UUID,
    val createdAt: LocalDateTime
) 