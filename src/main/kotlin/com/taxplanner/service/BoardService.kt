package com.taxplanner.service

import com.taxplanner.domain.entity.Board
import com.taxplanner.domain.entity.TaskStatus
import com.taxplanner.domain.enums.BoardViewType
import com.taxplanner.dto.request.CreateBoardRequest
import com.taxplanner.dto.request.UpdateBoardRequest
import com.taxplanner.dto.request.CreateTaskStatusRequest
import com.taxplanner.dto.request.UpdateBoardStatusRequest
import com.taxplanner.dto.request.ReorderStatusesRequest
import com.taxplanner.dto.response.BoardResponse
import com.taxplanner.dto.response.TaskResponse
import com.taxplanner.dto.response.ProjectSummaryResponse
import com.taxplanner.dto.response.TaskStatusResponse
import com.taxplanner.dto.response.UserResponse
import com.taxplanner.dto.response.TagResponse
import com.taxplanner.repository.BoardRepository
import com.taxplanner.repository.ProjectRepository
import com.taxplanner.repository.OrganizationMemberRepository
import com.taxplanner.repository.TaskStatusRepository
import com.taxplanner.repository.TaskRepository
import com.taxplanner.exception.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class BoardService(
    private val boardRepository: BoardRepository,
    private val projectRepository: ProjectRepository,
    private val organizationMemberRepository: OrganizationMemberRepository,
    private val taskStatusRepository: TaskStatusRepository,
    private val taskRepository: TaskRepository
) {

    fun getById(id: UUID, userId: UUID): BoardResponse {
        val board = boardRepository.findByIdOrNull(id)
            ?: throw BoardNotFoundException(id)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        return BoardResponse(
            id = board.id,
            name = board.name,
            description = board.description,
            viewType = board.viewType,
            project = ProjectSummaryResponse(
                id = board.project.id,
                name = board.project.name,
                description = board.project.description,
                colorTheme = board.project.colorTheme,
                memberCount = board.project.members.size,
                taskCount = board.project.boards.sumOf { it.tasks.size },
                completedTaskCount = board.project.boards.sumOf { b -> 
                    b.tasks.count { it.status.isFinal }
                }
            ),
            statuses = board.statuses.sortedBy { it.orderIndex }.map { status ->
                TaskStatusResponse(
                    id = status.id,
                    name = status.name,
                    color = status.color,
                    orderIndex = status.orderIndex,
                    boardId = board.id,
                    isFinal = status.isFinal
                )
            },
            createdAt = board.createdAt,
            updatedAt = board.updatedAt
        )
    }

    fun getTasks(boardId: UUID, userId: UUID): List<TaskResponse> {
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw BoardNotFoundException(boardId)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        return board.tasks.map { task ->
            TaskResponse(
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
                    boardId = board.id,
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
                        color = tag.color,
                        projectId = tag.project.id
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

    fun create(request: CreateBoardRequest, userId: UUID): BoardResponse {
        val project = projectRepository.findByIdOrNull(request.projectId)
            ?: throw ProjectNotFoundException(request.projectId)

        // Check if user has access to this project
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val board = Board(
            name = request.name,
            description = request.description,
            viewType = request.viewType ?: BoardViewType.KANBAN,
            project = project
        )
        val savedBoard = boardRepository.save(board)

        // Create default statuses
        val defaultStatuses = listOf(
            TaskStatus(name = "К выполнению", color = "#6B7280", orderIndex = 0, isFinal = false, board = savedBoard),
            TaskStatus(name = "В работе", color = "#3B82F6", orderIndex = 1, isFinal = false, board = savedBoard),
            TaskStatus(name = "Выполнено", color = "#10B981", orderIndex = 2, isFinal = true, board = savedBoard)
        )
        val savedStatuses = taskStatusRepository.saveAll(defaultStatuses)

        return BoardResponse(
            id = savedBoard.id,
            name = savedBoard.name,
            description = savedBoard.description,
            viewType = savedBoard.viewType,
            project = ProjectSummaryResponse(
                id = project.id,
                name = project.name,
                description = project.description,
                colorTheme = project.colorTheme,
                memberCount = project.members.size,
                taskCount = project.boards.sumOf { it.tasks.size },
                completedTaskCount = project.boards.sumOf { b -> 
                    b.tasks.count { it.status.isFinal }
                }
            ),
            statuses = savedStatuses.map { status ->
                TaskStatusResponse(
                    id = status.id,
                    name = status.name,
                    color = status.color,
                    orderIndex = status.orderIndex,
                    boardId = savedBoard.id,
                    isFinal = status.isFinal
                )
            },
            createdAt = savedBoard.createdAt,
            updatedAt = savedBoard.updatedAt
        )
    }

    fun update(id: UUID, request: UpdateBoardRequest, userId: UUID): BoardResponse {
        val board = boardRepository.findByIdOrNull(id)
            ?: throw BoardNotFoundException(id)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        request.name?.let { board.name = it }
        request.description?.let { board.description = it }
        request.viewType?.let { board.viewType = it }

        val updatedBoard = boardRepository.save(board)

        return BoardResponse(
            id = updatedBoard.id,
            name = updatedBoard.name,
            description = updatedBoard.description,
            viewType = updatedBoard.viewType,
            project = ProjectSummaryResponse(
                id = board.project.id,
                name = board.project.name,
                description = board.project.description,
                colorTheme = board.project.colorTheme,
                memberCount = board.project.members.size,
                taskCount = board.project.boards.sumOf { it.tasks.size },
                completedTaskCount = board.project.boards.sumOf { b -> 
                    b.tasks.count { it.status.isFinal }
                }
            ),
            statuses = updatedBoard.statuses.sortedBy { it.orderIndex }.map { status ->
                TaskStatusResponse(
                    id = status.id,
                    name = status.name,
                    color = status.color,
                    orderIndex = status.orderIndex,
                    boardId = updatedBoard.id,
                    isFinal = status.isFinal
                )
            },
            createdAt = updatedBoard.createdAt,
            updatedAt = updatedBoard.updatedAt
        )
    }

    fun delete(id: UUID, userId: UUID) {
        val board = boardRepository.findByIdOrNull(id)
            ?: throw BoardNotFoundException(id)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        boardRepository.delete(board)
    }

    fun getStatuses(boardId: UUID, userId: UUID): List<TaskStatusResponse> {
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw BoardNotFoundException(boardId)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        return board.statuses.sortedBy { it.orderIndex }.map { status ->
            TaskStatusResponse(
                id = status.id,
                name = status.name,
                color = status.color,
                orderIndex = status.orderIndex,
                boardId = board.id,
                isFinal = status.isFinal
            )
        }
    }

    fun createStatus(boardId: UUID, request: CreateTaskStatusRequest, userId: UUID): TaskStatusResponse {
        println("Creating status: ${request.name}, isFinal: ${request.isFinal}")
        
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw BoardNotFoundException(boardId)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        // Create the new status first
        val status = TaskStatus(
            name = request.name,
            color = request.color ?: "#6B7280",
            orderIndex = request.orderIndex,
            board = board,
            isFinal = request.isFinal ?: false
        )

        val savedStatus = taskStatusRepository.save(status)
        println("Saved status: ${savedStatus.name}, isFinal: ${savedStatus.isFinal}")

        // If this status is being marked as final, remove final flag from other statuses
        if (savedStatus.isFinal) {
            println("Removing final flag from other statuses")
            board.statuses.forEach { existingStatus ->
                if (existingStatus.id != savedStatus.id && existingStatus.isFinal) {
                    println("Removing final flag from: ${existingStatus.name}")
                    existingStatus.isFinal = false
                    taskStatusRepository.save(existingStatus)
                }
            }
            
            // Update all tasks with this status
            syncTasksForStatus(savedStatus)
        }
        
        return TaskStatusResponse(
            id = savedStatus.id,
            name = savedStatus.name,
            color = savedStatus.color,
            orderIndex = savedStatus.orderIndex,
            boardId = board.id,
            isFinal = savedStatus.isFinal
        )
    }

    fun updateStatus(boardId: UUID, statusId: UUID, request: UpdateBoardStatusRequest, userId: UUID): TaskStatusResponse {
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw BoardNotFoundException(boardId)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val status = taskStatusRepository.findByIdOrNull(statusId)
            ?: throw TaskStatusNotFoundException(statusId)

        if (status.board.id != boardId) {
            throw AccessDeniedException("Status does not belong to this board")
        }

        val wasFinalbefore = status.isFinal

        // Update status fields first
        request.name?.let { status.name = it }
        request.color?.let { status.color = it }
        request.orderIndex?.let { status.orderIndex = it }
        request.isFinal?.let { status.isFinal = it }

        // If this status is being marked as final, remove final flag from other statuses
        if (status.isFinal && !wasFinalbefore) {
            board.statuses.forEach { existingStatus ->
                if (existingStatus.id != statusId && existingStatus.isFinal) {
                    existingStatus.isFinal = false
                    taskStatusRepository.save(existingStatus)
                }
            }
        }

        val updatedStatus = taskStatusRepository.save(status)
        
        // Update tasks if final status changed
        if (wasFinalbefore != updatedStatus.isFinal) {
            syncTasksForStatus(updatedStatus)
            
            // If this status is no longer final, also sync tasks that were using it
            if (!updatedStatus.isFinal) {
                updatedStatus.tasks.forEach { task ->
                    task.updateCompletionStatus()
                    taskRepository.save(task)
                }
            }
        }
        
        return TaskStatusResponse(
            id = updatedStatus.id,
            name = updatedStatus.name,
            color = updatedStatus.color,
            orderIndex = updatedStatus.orderIndex,
            boardId = board.id,
            isFinal = updatedStatus.isFinal
        )
    }

    private fun syncTasksForStatus(status: TaskStatus) {
        status.tasks.forEach { task ->
            task.updateCompletionStatus()
            taskRepository.save(task)
        }
    }

    fun deleteStatus(boardId: UUID, statusId: UUID, userId: UUID) {
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw BoardNotFoundException(boardId)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val status = taskStatusRepository.findByIdOrNull(statusId)
            ?: throw TaskStatusNotFoundException(statusId)

        if (status.board.id != boardId) {
            throw AccessDeniedException("Status does not belong to this board")
        }

        // Check if status has tasks - prevent deletion if it does
        if (status.tasks.isNotEmpty()) {
            throw BusinessLogicException("Cannot delete status with existing tasks")
        }

        taskStatusRepository.delete(status)
    }

    fun reorderStatuses(boardId: UUID, request: ReorderStatusesRequest, userId: UUID): List<TaskStatusResponse> {
        println("Reordering statuses for board: $boardId, request: $request")
        
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw BoardNotFoundException(boardId)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        if (request.statusOrders.isEmpty()) {
            throw BusinessLogicException("Status orders list cannot be empty")
        }

        // Update order for each status
        request.statusOrders.forEach { statusOrder ->
            println("Processing status order: $statusOrder")
            val status = taskStatusRepository.findByIdOrNull(statusOrder.statusId)
                ?: throw TaskStatusNotFoundException(statusOrder.statusId)

            if (status.board.id != boardId) {
                throw AccessDeniedException("Status does not belong to this board")
            }

            status.orderIndex = statusOrder.orderIndex
            taskStatusRepository.save(status)
        }

        // Return updated statuses
        return getStatuses(boardId, userId)
    }

    fun syncTaskCompletion(boardId: UUID, userId: UUID): Map<String, Any> {
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw BoardNotFoundException(boardId)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        var updatedTasks = 0
        var completedTasks = 0
        var uncompletedTasks = 0

        // Update all tasks in this board to match their status finality
        board.tasks.forEach { task ->
            val wasCompleted = task.isCompleted
            task.updateCompletionStatus()
            
            if (wasCompleted != task.isCompleted) {
                updatedTasks++
                if (task.isCompleted) {
                    completedTasks++
                } else {
                    uncompletedTasks++
                }
                taskRepository.save(task)
            }
        }

        return mapOf(
            "message" to "Task completion status synchronized",
            "boardId" to boardId,
            "totalTasks" to board.tasks.size,
            "updatedTasks" to updatedTasks,
            "completedTasks" to completedTasks,
            "uncompletedTasks" to uncompletedTasks
        )
    }
} 