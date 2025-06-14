package com.taxplanner.repository

import com.taxplanner.domain.entity.Board
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BoardRepository : JpaRepository<Board, UUID> {
    
    fun findByProjectIdAndIsActiveTrue(projectId: UUID): List<Board>
    
    fun findByProjectIdAndIdAndIsActiveTrue(projectId: UUID, id: UUID): Board?
} 