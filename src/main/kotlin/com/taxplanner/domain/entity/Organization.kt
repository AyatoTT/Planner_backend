package com.taxplanner.domain.entity

import com.taxplanner.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "organizations")
data class Organization(
    @Column(nullable = false)
    var name: String,

    @Column(length = 1000)
    var description: String? = null,

    @Column(name = "logo_url")
    var logoUrl: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @OneToMany(mappedBy = "organization", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val members: MutableList<OrganizationMember> = mutableListOf(),

    @OneToMany(mappedBy = "organization", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val projects: MutableList<Project> = mutableListOf()
) : BaseEntity() {

    constructor() : this(name = "")
} 