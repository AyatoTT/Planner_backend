package com.taxplanner.repository

import com.taxplanner.domain.entity.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository : JpaRepository<Project, UUID> {
    
    fun findByOrganizationIdAndIsActiveTrue(organizationId: UUID): List<Project>
    
    @Query("""
        SELECT p FROM Project p 
        JOIN p.members pm 
        WHERE pm.user.id = :userId AND p.isActive = true
        ORDER BY p.name
    """)
    fun findByUserId(@Param("userId") userId: UUID): List<Project>
    
    @Query("""
        SELECT p FROM Project p 
        JOIN p.members pm 
        WHERE pm.user.id = :userId AND p.id = :projectId AND p.isActive = true
    """)
    fun findByIdAndUserId(@Param("projectId") projectId: UUID, @Param("userId") userId: UUID): Project?
    
    fun findByOrganizationIdAndIdAndIsActiveTrue(organizationId: UUID, id: UUID): Project?
} 