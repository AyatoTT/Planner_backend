package com.taxplanner.repository

import com.taxplanner.domain.entity.Task
import com.taxplanner.domain.enums.TaskPriority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface TaskRepository : JpaRepository<Task, UUID> {
    
    fun findByBoardId(boardId: UUID): List<Task>
    
    fun findByBoardIdOrderByOrderIndexAsc(boardId: UUID): List<Task>
    
    fun findByStatusIdOrderByOrderIndexAsc(statusId: UUID): List<Task>
    
    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId ORDER BY t.dueDate ASC NULLS LAST")
    fun findByAssigneeIdOrderByDueDateAsc(@Param("userId") userId: UUID): List<Task>
    
    @Query("""
        SELECT t FROM Task t 
        WHERE t.board.id = :boardId 
        AND (:statusId IS NULL OR t.status.id = :statusId)
        AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)
        AND (:priority IS NULL OR t.priority = :priority)
        ORDER BY t.orderIndex ASC
    """)
    fun findTasksWithFilters(
        @Param("boardId") boardId: UUID,
        @Param("statusId") statusId: UUID?,
        @Param("assigneeId") assigneeId: UUID?,
        @Param("priority") priority: TaskPriority?
    ): List<Task>
    
    @Query("""
        SELECT t FROM Task t 
        JOIN t.tags tag 
        WHERE tag.id IN :tagIds 
        AND t.board.id = :boardId
        ORDER BY t.orderIndex ASC
    """)
    fun findByBoardIdAndTagsIn(@Param("boardId") boardId: UUID, @Param("tagIds") tagIds: List<UUID>): List<Task>
    
    @Query("""
        SELECT t FROM Task t 
        WHERE t.dueDate BETWEEN :startDate AND :endDate
        AND t.assignee.id = :userId
        ORDER BY t.dueDate ASC
    """)
    fun findTasksDueBetween(
        @Param("userId") userId: UUID,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Task>
} 