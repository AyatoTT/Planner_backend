package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["project_id", "name"])]
)
data class Tag(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var color: String = "#3B82F6", // Default blue color

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: Project,

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    val tasks: MutableList<Task> = mutableListOf()
) : BaseEntity() {

    constructor() : this(
        name = "",
        project = Project()
    )
} 