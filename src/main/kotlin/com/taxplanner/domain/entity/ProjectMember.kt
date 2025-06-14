package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import com.taxplanner.domain.enums.OrganizationRole
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "project_members",
    uniqueConstraints = [UniqueConstraint(columnNames = ["project_id", "user_id"])]
)
data class ProjectMember(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    val project: Project,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: OrganizationRole, // Reusing organization roles for project level

    @Column(name = "joined_at", nullable = false)
    val joinedAt: LocalDateTime = LocalDateTime.now()
) : BaseEntity() {

    constructor() : this(
        project = Project(),
        user = User(),
        role = OrganizationRole.MEMBER
    )
} 