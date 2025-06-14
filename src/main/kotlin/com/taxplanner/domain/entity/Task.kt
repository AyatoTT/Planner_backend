package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import com.taxplanner.domain.enums.TaskPriority
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tasks")
data class Task(
    @Column(nullable = false)
    var title: String,

    @Column(length = 4000)
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var priority: TaskPriority = TaskPriority.MEDIUM,

    @Column(name = "due_date")
    var dueDate: LocalDateTime? = null,

    @Column(name = "order_index", nullable = false)
    var orderIndex: Int = 0,

    @Column(name = "is_completed", nullable = false)
    var isCompleted: Boolean = false,

    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    val board: Board,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    var status: TaskStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    val creator: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    var assignee: User? = null,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinTable(
        name = "task_tags",
        joinColumns = [JoinColumn(name = "task_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: MutableList<Tag> = mutableListOf(),

    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val checklists: MutableList<Checklist> = mutableListOf(),

    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val comments: MutableList<TaskComment> = mutableListOf()
) : BaseEntity() {

    constructor() : this(
        title = "",
        board = Board(),
        status = TaskStatus(),
        creator = User()
    )
} 