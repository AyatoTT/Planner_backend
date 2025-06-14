package com.taxplanner.domain.enums

enum class TaskPriority(val displayName: String, val order: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    CRITICAL("Critical", 4)
} 