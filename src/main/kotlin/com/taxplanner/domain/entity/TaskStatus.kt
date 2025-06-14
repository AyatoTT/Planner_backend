package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "task_statuses",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["board_id", "name"]),
        UniqueConstraint(columnNames = ["board_id", "order_index"])
    ]
)
data class TaskStatus(
    @Column(nullable = false)
    var name: String,

    @Column(name = "order_index", nullable = false)
    var orderIndex: Int,

    @Column(nullable = false)
    var color: String = "#6B7280", // Default gray color

    @Column(name = "is_final", nullable = false)
    var isFinal: Boolean = false, // Mark if this is a completion status

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    val board: Board,

    @OneToMany(mappedBy = "status", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val tasks: MutableList<Task> = mutableListOf()
) : BaseEntity() {

    constructor() : this(
        name = "",
        orderIndex = 0,
        board = Board()
    )
} 