package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import com.taxplanner.domain.enums.OrganizationRole
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "organization_members",
    uniqueConstraints = [UniqueConstraint(columnNames = ["organization_id", "user_id"])]
)
data class OrganizationMember(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    val organization: Organization,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: OrganizationRole,

    @Column(name = "joined_at", nullable = false)
    val joinedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_id")
    val invitedBy: User? = null
) : BaseEntity() {

    constructor() : this(
        organization = Organization(),
        user = User(),
        role = OrganizationRole.MEMBER
    )
} 