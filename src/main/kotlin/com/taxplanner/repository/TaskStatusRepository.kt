package com.taxplanner.repository

import com.taxplanner.domain.entity.TaskStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskStatusRepository : JpaRepository<TaskStatus, UUID> {
    
    fun findByBoardIdOrderByOrderIndexAsc(boardId: UUID): List<TaskStatus>
    
    fun findByBoardIdAndName(boardId: UUID, name: String): TaskStatus?
    
    fun findByBoardIdAndOrderIndex(boardId: UUID, orderIndex: Int): TaskStatus?
    
    fun countByBoardId(boardId: UUID): Long
} 