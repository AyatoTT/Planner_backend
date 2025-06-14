package com.taxplanner.repository

import com.taxplanner.domain.entity.TaskComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskCommentRepository : JpaRepository<TaskComment, UUID> {
    fun findByTaskIdOrderByCreatedAtDesc(taskId: UUID): List<TaskComment>
    fun findByTaskIdAndId(taskId: UUID, id: UUID): TaskComment?
} 