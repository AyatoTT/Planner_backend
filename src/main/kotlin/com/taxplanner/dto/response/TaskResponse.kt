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

data class TaskSummaryResponse(
    val id: UUID,
    val title: String,
    val priority: TaskPriority,
    val dueDate: LocalDateTime?,
    val isCompleted: Boolean,
    val status: TaskStatusResponse,
    val assignee: UserResponse?,
    val tagCount: Int
)

data class TaskStatusResponse(
    val id: UUID,
    val name: String,
    val color: String,
    val orderIndex: Int,
    val isFinal: Boolean
)

data class TagResponse(
    val id: UUID,
    val name: String,
    val color: String
)

data class ChecklistResponse(
    val id: UUID,
    val title: String,
    val isCompleted: Boolean,
    val completedAt: LocalDateTime?,
    val orderIndex: Int,
    val completedBy: UserResponse?
)

data class TaskCommentResponse(
    val id: UUID,
    val content: String,
    val isEdited: Boolean,
    val author: UserResponse,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 