package com.taxplanner.service

import com.taxplanner.domain.entity.Board
import com.taxplanner.domain.entity.TaskStatus
import com.taxplanner.domain.enums.BoardViewType
import com.taxplanner.dto.request.CreateBoardRequest
import com.taxplanner.dto.request.UpdateBoardRequest
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
    private val taskStatusRepository: TaskStatusRepository
) {

    fun getById(id: UUID, userId: UUID): BoardResponse {
        val board = boardRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Board not found")

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

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
                    b.tasks.count { it.status.name.lowercase() == "done" || it.status.name.lowercase() == "completed" }
                }
            ),
            statuses = board.statuses.sortedBy { it.orderIndex }.map { status ->
                TaskStatusResponse(
                    id = status.id,
                    name = status.name,
                    color = status.color,
                    orderIndex = status.orderIndex,
                    isFinal = status.isFinal
                )
            },
            createdAt = board.createdAt,
            updatedAt = board.updatedAt
        )
    }

    fun getTasks(boardId: UUID, userId: UUID): List<TaskResponse> {
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw RuntimeException("Board not found")

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

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

    fun create(request: CreateBoardRequest, userId: UUID): BoardResponse {
        val project = projectRepository.findByIdOrNull(request.projectId)
            ?: throw RuntimeException("Project not found")

        // Check if user has access to this project
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        val board = Board(
            name = request.name,
            description = request.description,
            viewType = request.viewType ?: BoardViewType.KANBAN,
            project = project
        )
        val savedBoard = boardRepository.save(board)

        // Create default statuses
        val defaultStatuses = listOf(
            TaskStatus(name = "To Do", color = "#6B7280", orderIndex = 0, isFinal = false, board = savedBoard),
            TaskStatus(name = "In Progress", color = "#3B82F6", orderIndex = 1, isFinal = false, board = savedBoard),
            TaskStatus(name = "Done", color = "#10B981", orderIndex = 2, isFinal = true, board = savedBoard)
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
                    b.tasks.count { it.status.name.lowercase() == "done" || it.status.name.lowercase() == "completed" }
                }
            ),
            statuses = savedStatuses.map { status ->
                TaskStatusResponse(
                    id = status.id,
                    name = status.name,
                    color = status.color,
                    orderIndex = status.orderIndex,
                    isFinal = status.isFinal
                )
            },
            createdAt = savedBoard.createdAt,
            updatedAt = savedBoard.updatedAt
        )
    }

    fun update(id: UUID, request: UpdateBoardRequest, userId: UUID): BoardResponse {
        val board = boardRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Board not found")

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

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
                    b.tasks.count { it.status.name.lowercase() == "done" || it.status.name.lowercase() == "completed" }
                }
            ),
            statuses = updatedBoard.statuses.sortedBy { it.orderIndex }.map { status ->
                TaskStatusResponse(
                    id = status.id,
                    name = status.name,
                    color = status.color,
                    orderIndex = status.orderIndex,
                    isFinal = status.isFinal
                )
            },
            createdAt = updatedBoard.createdAt,
            updatedAt = updatedBoard.updatedAt
        )
    }

    fun delete(id: UUID, userId: UUID) {
        val board = boardRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Board not found")

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        boardRepository.delete(board)
    }

    fun getStatuses(boardId: UUID, userId: UUID): List<TaskStatusResponse> {
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw RuntimeException("Board not found")

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        return board.statuses.sortedBy { it.orderIndex }.map { status ->
            TaskStatusResponse(
                id = status.id,
                name = status.name,
                color = status.color,
                orderIndex = status.orderIndex,
                isFinal = status.isFinal
            )
        }
    }
} 