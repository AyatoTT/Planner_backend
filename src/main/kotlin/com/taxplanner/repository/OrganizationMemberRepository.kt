package com.taxplanner.repository

import com.taxplanner.domain.entity.OrganizationMember
import com.taxplanner.domain.enums.OrganizationRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrganizationMemberRepository : JpaRepository<OrganizationMember, UUID> {
    
    fun findByOrganizationIdAndUserId(organizationId: UUID, userId: UUID): OrganizationMember?
    
    fun findByOrganizationId(organizationId: UUID): List<OrganizationMember>
    
    fun findByUserId(userId: UUID): List<OrganizationMember>
    
    fun findByUserIdAndRole(userId: UUID, role: OrganizationRole): List<OrganizationMember>
    
    fun countByOrganizationId(organizationId: UUID): Int
    
    fun existsByOrganizationIdAndUserIdAndRoleIn(
        organizationId: UUID, 
        userId: UUID, 
        roles: List<OrganizationRole>
    ): Boolean
} 