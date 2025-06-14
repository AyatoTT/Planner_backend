package com.taxplanner.service

import com.taxplanner.domain.entity.Task
import com.taxplanner.domain.entity.TaskComment
import com.taxplanner.domain.entity.Checklist
import com.taxplanner.dto.request.CreateTaskRequest
import com.taxplanner.dto.request.UpdateTaskRequest
import com.taxplanner.dto.request.CreateCommentRequest
import com.taxplanner.dto.request.CreateChecklistRequest
import com.taxplanner.dto.request.UpdateChecklistRequest
import com.taxplanner.dto.response.TaskResponse
import com.taxplanner.dto.response.TaskStatusResponse
import com.taxplanner.dto.response.UserResponse
import com.taxplanner.dto.response.TagResponse
import com.taxplanner.dto.response.CommentResponse
import com.taxplanner.dto.response.ChecklistResponse
import com.taxplanner.repository.TaskRepository
import com.taxplanner.repository.BoardRepository
import com.taxplanner.repository.TaskStatusRepository
import com.taxplanner.repository.OrganizationMemberRepository
import com.taxplanner.repository.UserRepository
import com.taxplanner.repository.TaskCommentRepository
import com.taxplanner.repository.ChecklistRepository
import com.taxplanner.exception.*
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
    private val userRepository: UserRepository,
    private val taskCommentRepository: TaskCommentRepository,
    private val checklistRepository: ChecklistRepository
) {

    fun getTasksByBoard(boardId: UUID, userId: UUID): List<TaskResponse> {
        val board = boardRepository.findByIdOrNull(boardId)
            ?: throw BoardNotFoundException(boardId)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val tasks = taskRepository.findByBoardId(boardId)
        return tasks.map { task -> mapToTaskResponse(task) }
    }

    fun getById(id: UUID, userId: UUID): TaskResponse {
        val task = taskRepository.findByIdOrNull(id)
            ?: throw TaskNotFoundException(id)

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        return mapToTaskResponse(task)
    }

    fun create(request: CreateTaskRequest, userId: UUID): TaskResponse {
        val board = boardRepository.findByIdOrNull(request.boardId)
            ?: throw BoardNotFoundException(request.boardId)

        val status = taskStatusRepository.findByIdOrNull(request.statusId)
            ?: throw TaskStatusNotFoundException(request.statusId)

        // Check if user has access to this board
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val creator = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException(userId)

        val assignee = request.assigneeId?.let { assigneeId ->
            userRepository.findByIdOrNull(assigneeId)
                ?: throw UserNotFoundException(assigneeId)
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
            ?: throw TaskNotFoundException(id)

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        request.title?.let { task.title = it }
        request.description?.let { task.description = it }
        request.priority?.let { task.priority = it }
        request.dueDate?.let { task.dueDate = it }
        
        var statusChanged = false
        request.statusId?.let { statusId ->
            val status = taskStatusRepository.findByIdOrNull(statusId)
                ?: throw TaskStatusNotFoundException(statusId)
            if (task.status.id != statusId) {
                task.status = status
                statusChanged = true
            }
        }

        request.assigneeId?.let { assigneeId ->
            val assignee = userRepository.findByIdOrNull(assigneeId)
                ?: throw UserNotFoundException(assigneeId)
            task.assignee = assignee
        }

        task.updatedAt = LocalDateTime.now()
        
        // Update completion status if status was changed
        if (statusChanged) {
            task.updateCompletionStatus()
        }
        
        val updatedTask = taskRepository.save(task)
        return mapToTaskResponse(updatedTask)
    }

    fun delete(id: UUID, userId: UUID) {
        val task = taskRepository.findByIdOrNull(id)
            ?: throw TaskNotFoundException(id)

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        taskRepository.delete(task)
    }

    fun updateStatus(id: UUID, statusId: UUID, orderIndex: Int?, userId: UUID): TaskResponse {
        val task = taskRepository.findByIdOrNull(id)
            ?: throw TaskNotFoundException(id)

        val status = taskStatusRepository.findByIdOrNull(statusId)
            ?: throw TaskStatusNotFoundException(statusId)

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        // Check if the new status belongs to the same board as the task
        if (status.board.id != task.board.id) {
            throw ValidationException("Status does not belong to the same board as the task")
        }

        task.status = status
        orderIndex?.let { task.orderIndex = it }
        task.updatedAt = LocalDateTime.now()
        
        // Update completion status based on the new status
        task.updateCompletionStatus()

        val updatedTask = taskRepository.save(task)
        return mapToTaskResponse(updatedTask)
    }

    // Comment methods
    fun getComments(taskId: UUID, userId: UUID): List<CommentResponse> {
        val task = taskRepository.findByIdOrNull(taskId)
            ?: throw TaskNotFoundException(taskId)

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val comments = taskCommentRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
        return comments.map { comment -> mapToCommentResponse(comment) }
    }

    fun createComment(taskId: UUID, request: CreateCommentRequest, userId: UUID): CommentResponse {
        val task = taskRepository.findByIdOrNull(taskId)
            ?: throw TaskNotFoundException(taskId)

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val author = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException(userId)

        val comment = TaskComment(
            content = request.content,
            task = task,
            author = author
        )

        val savedComment = taskCommentRepository.save(comment)
        return mapToCommentResponse(savedComment)
    }

    // Checklist methods
    fun getChecklists(taskId: UUID, userId: UUID): List<ChecklistResponse> {
        val task = taskRepository.findByIdOrNull(taskId)
            ?: throw TaskNotFoundException(taskId)

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val checklists = checklistRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
        return checklists.map { checklist -> mapToChecklistResponse(checklist) }
    }

    fun createChecklist(taskId: UUID, request: CreateChecklistRequest, userId: UUID): ChecklistResponse {
        val task = taskRepository.findByIdOrNull(taskId)
            ?: throw TaskNotFoundException(taskId)

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val checklist = Checklist(
            title = request.title,
            task = task
        )

        val savedChecklist = checklistRepository.save(checklist)
        return mapToChecklistResponse(savedChecklist)
    }

    fun updateChecklist(taskId: UUID, checklistId: UUID, request: UpdateChecklistRequest, userId: UUID): ChecklistResponse {
        val task = taskRepository.findByIdOrNull(taskId)
            ?: throw TaskNotFoundException(taskId)

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val checklist = checklistRepository.findByTaskIdAndId(taskId, checklistId)
            ?: throw ChecklistNotFoundException(checklistId)

        request.title?.let { checklist.title = it }
        request.isCompleted?.let { checklist.isCompleted = it }

        val updatedChecklist = checklistRepository.save(checklist)
        return mapToChecklistResponse(updatedChecklist)
    }

    fun deleteChecklist(taskId: UUID, checklistId: UUID, userId: UUID) {
        val task = taskRepository.findByIdOrNull(taskId)
            ?: throw TaskNotFoundException(taskId)

        // Check if user has access to this task
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            task.board.project.organization.id, userId
        ) ?: throw AccessDeniedException()

        val checklist = checklistRepository.findByTaskIdAndId(taskId, checklistId)
            ?: throw ChecklistNotFoundException(checklistId)

        checklistRepository.delete(checklist)
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
                boardId = task.board.id,
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

    private fun mapToCommentResponse(comment: TaskComment): CommentResponse {
        return CommentResponse(
            id = comment.id,
            content = comment.content,
            authorId = comment.author.id,
            authorName = comment.author.name,
            taskId = comment.task.id,
            createdAt = comment.createdAt
        )
    }

    private fun mapToChecklistResponse(checklist: Checklist): ChecklistResponse {
        return ChecklistResponse(
            id = checklist.id,
            title = checklist.title,
            isCompleted = checklist.isCompleted,
            taskId = checklist.task.id,
            createdAt = checklist.createdAt
        )
    }
} 