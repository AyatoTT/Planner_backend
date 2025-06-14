package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import com.taxplanner.domain.enums.BoardViewType
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "boards")
data class Board(
    @Column(nullable = false)
    var name: String,

    @Column(length = 1000)
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "view_type", nullable = false)
    var viewType: BoardViewType = BoardViewType.KANBAN,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: Project,

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val tasks: MutableList<Task> = mutableListOf(),

    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val statuses: MutableList<TaskStatus> = mutableListOf(),

    @Column(name = "default_status_id")
    var defaultStatusId: UUID? = null
) : BaseEntity() {

    constructor() : this(
        name = "",
        project = Project()
    )
} 