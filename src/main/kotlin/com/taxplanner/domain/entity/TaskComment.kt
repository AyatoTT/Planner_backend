package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "task_comments")
data class TaskComment(
    @Column(length = 2000, nullable = false)
    var content: String,

    @Column(name = "is_edited", nullable = false)
    var isEdited: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    val task: Task,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: User
) : BaseEntity() {

    constructor() : this(
        content = "",
        task = Task(),
        author = User()
    )
} 