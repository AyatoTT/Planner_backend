package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "projects")
data class Project(
    @Column(nullable = false)
    var name: String,

    @Column(length = 2000)
    var description: String? = null,

    @Column(name = "color_theme")
    var colorTheme: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    val organization: Organization,

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val boards: MutableList<Board> = mutableListOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val tags: MutableList<Tag> = mutableListOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val members: MutableList<ProjectMember> = mutableListOf()
) : BaseEntity() {

    constructor() : this(
        name = "",
        organization = Organization()
    )
} 