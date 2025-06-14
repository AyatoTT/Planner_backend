package com.taxplanner.repository

import com.taxplanner.domain.entity.Organization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrganizationRepository : JpaRepository<Organization, UUID> {
    
    @Query("""
        SELECT o FROM Organization o 
        JOIN o.members om 
        WHERE om.user.id = :userId AND o.isActive = true
        ORDER BY o.name
    """)
    fun findByUserId(@Param("userId") userId: UUID): List<Organization>
    
    @Query("""
        SELECT o FROM Organization o 
        JOIN o.members om 
        WHERE om.user.id = :userId AND o.id = :organizationId AND o.isActive = true
    """)
    fun findByIdAndUserId(@Param("organizationId") organizationId: UUID, @Param("userId") userId: UUID): Organization?
    
    fun findByNameContainingIgnoreCaseAndIsActiveTrue(name: String): List<Organization>
} 