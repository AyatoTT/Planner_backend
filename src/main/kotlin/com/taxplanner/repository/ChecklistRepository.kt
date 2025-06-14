package com.taxplanner.repository

import com.taxplanner.domain.entity.Checklist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChecklistRepository : JpaRepository<Checklist, UUID> {
    fun findByTaskIdOrderByCreatedAtAsc(taskId: UUID): List<Checklist>
    fun findByTaskIdAndId(taskId: UUID, id: UUID): Checklist?
} 