package com.taxplanner.service

import com.taxplanner.domain.entity.Task
import com.taxplanner.dto.request.CreateTaskRequest
import com.taxplanner.dto.request.UpdateTaskRequest
import com.taxplanner.dto.response.TaskResponse
import com.taxplanner.dto.response.TaskStatusResponse
import com.taxplanner.dto.response.UserResponse
import com.taxplanner.dto.response.TagResponse
import com.taxplanner.repository.TaskRepository
import com.taxplanner.repository.BoardRepository
import com.taxplanner.repository.TaskStatusRepository
import com.taxplanner.repository.OrganizationMemberRepository
import com.taxplanner.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class TaskService(
    private val taskRepository: TaskRepository,
    private val boardRepository: BoardRepository,
    private val taskStatusRepository: TaskStatusRepository,
    private val organizationMemberRepository: OrganizationMemberRepository,
    private val userRepository: UserRepository
) {

    fun getTasksByBoard(boardId: UUID, userId: UUID): List<TaskResponse> {
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw RuntimeException("Board not found")

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        val tasks = taskRepository.findByBoardId(boardId)
        return tasks.map { task -> mapToTaskResponse(task) }
    }

    fun getById(id: UUID, userId: UUID): TaskResponse {
        val task = taskRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Task not found")

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        return mapToTaskResponse(task)
    }

    fun create(request: CreateTaskRequest, userId: UUID): TaskResponse {
        val board = boardRepository.findByIdOrNull(request.boardId)
            ?: throw RuntimeException("Board not found")

        val status = taskStatusRepository.findByIdOrNull(request.statusId)
            ?: throw RuntimeException("Status not found")

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        val creator = userRepository.findByIdOrNull(userId)
            ?: throw RuntimeException("User not found")

        val assignee = request.assigneeId?.let { assigneeId ->
            userRepository.findByIdOrNull(assigneeId)
                ?: throw RuntimeException("Assignee not found")
        }

        val task = Task(
            title = request.title,
            description = request.description,
            priority = request.priority ?: com.taxplanner.domain.enums.TaskPriority.MEDIUM,
            dueDate = request.dueDate,
            orderIndex = request.orderIndex ?: 0,
            board = board,
            status = status,
            creator = creator,
            assignee = assignee
        )

        val savedTask = taskRepository.save(task)
        return mapToTaskResponse(savedTask)
    }

    fun update(id: UUID, request: UpdateTaskRequest, userId: UUID): TaskResponse {
        val task = taskRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Task not found")

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        request.title?.let { task.title = it }
        request.description?.let { task.description = it }
        request.priority?.let { task.priority = it }
        request.dueDate?.let { task.dueDate = it }
        
        request.statusId?.let { statusId ->
            val status = taskStatusRepository.findByIdOrNull(statusId)
                ?: throw RuntimeException("Status not found")
            task.status = status
        }

        request.assigneeId?.let { assigneeId ->
            val assignee = userRepository.findByIdOrNull(assigneeId)
                ?: throw RuntimeException("Assignee not found")
            task.assignee = assignee
        }

        task.updatedAt = LocalDateTime.now()
        val updatedTask = taskRepository.save(task)
        return mapToTaskResponse(updatedTask)
    }

    fun delete(id: UUID, userId: UUID) {
        val task = taskRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Task not found")

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        taskRepository.delete(task)
    }

    fun updateStatus(id: UUID, statusId: UUID, orderIndex: Int?, userId: UUID): TaskResponse {
        val task = taskRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Task not found")

        val status = taskStatusRepository.findByIdOrNull(statusId)
            ?: throw RuntimeException("Status not found")

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        task.status = status
        orderIndex?.let { task.orderIndex = it }
        task.updatedAt = LocalDateTime.now()

        val updatedTask = taskRepository.save(task)
        return mapToTaskResponse(updatedTask)
    }

    private fun mapToTaskResponse(task: Task): TaskResponse {
        return TaskResponse(
            id = task.id,
            title = task.title,
            description = task.description,
            priority = task.priority,
            dueDate = task.dueDate,
            orderIndex = task.orderIndex,
            isCompleted = task.isCompleted,
            completedAt = task.completedAt,
            status = TaskStatusResponse(
                id = task.status.id,
                name = task.status.name,
                color = task.status.color,
                orderIndex = task.status.orderIndex,
                isFinal = task.status.isFinal
            ),
            creator = UserResponse(
                id = task.creator.id,
                email = task.creator.email,
                name = task.creator.name,
                avatarUrl = task.creator.avatarUrl,
                emailVerified = task.creator.emailVerified,
                createdAt = task.creator.createdAt
            ),
            assignee = task.assignee?.let { assignee ->
                UserResponse(
                    id = assignee.id,
                    email = assignee.email,
                    name = assignee.name,
                    avatarUrl = assignee.avatarUrl,
                    emailVerified = assignee.emailVerified,
                    createdAt = assignee.createdAt
                )
            },
            tags = task.tags.map { tag ->
                TagResponse(
                    id = tag.id,
                    name = tag.name,
                    color = tag.color
                )
            },
            checklistCount = task.checklists.size,
            completedChecklistCount = task.checklists.count { it.isCompleted },
            commentCount = task.comments.size,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }
} 