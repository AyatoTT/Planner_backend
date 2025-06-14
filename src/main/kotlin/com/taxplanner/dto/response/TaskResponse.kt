package com.taxplanner.dto.response

import com.taxplanner.domain.enums.TaskPriority
import java.time.LocalDateTime
import java.util.*

data class TaskResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val priority: TaskPriority,
    val dueDate: LocalDateTime?,
    val orderIndex: Int,
    val isCompleted: Boolean,
    val completedAt: LocalDateTime?,
    val status: TaskStatusResponse,
    val creator: UserResponse,
    val assignee: UserResponse?,
    val tags: List<TagResponse>,
    val checklistCount: Int,
    val completedChecklistCount: Int,
    val commentCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 