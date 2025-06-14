package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var name: String,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Column(name = "email_verified", nullable = false)
    var emailVerified: Boolean = false,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val organizationMembers: MutableList<OrganizationMember> = mutableListOf(),

    @OneToMany(mappedBy = "creator", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val createdTasks: MutableList<Task> = mutableListOf(),

    @OneToMany(mappedBy = "assignee", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val assignedTasks: MutableList<Task> = mutableListOf()
) : BaseEntity() {

    constructor() : this(
        email = "",
        password = "",
        name = ""
    )
} 