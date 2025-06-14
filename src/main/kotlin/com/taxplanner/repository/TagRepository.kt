package com.taxplanner.repository

import com.taxplanner.domain.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TagRepository : JpaRepository<Tag, UUID> {
    
    fun findByProjectId(projectId: UUID): List<Tag>
    
    fun findByProjectIdAndName(projectId: UUID, name: String): Tag?
    
    fun findByIdInAndProjectId(ids: List<UUID>, projectId: UUID): List<Tag>
} 