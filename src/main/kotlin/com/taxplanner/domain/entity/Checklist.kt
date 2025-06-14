package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "checklists")
data class Checklist(
    @Column(nullable = false)
    var title: String,

    @Column(name = "is_completed", nullable = false)
    var isCompleted: Boolean = false,

    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null,

    @Column(name = "order_index", nullable = false)
    var orderIndex: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    val task: Task,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by_id")
    var completedBy: User? = null
) : BaseEntity() {

    constructor() : this(
        title = "",
        task = Task()
    )
} 