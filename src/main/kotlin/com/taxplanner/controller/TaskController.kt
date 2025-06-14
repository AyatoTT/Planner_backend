package com.taxplanner.controller

import com.taxplanner.dto.request.CreateTaskRequest
import com.taxplanner.dto.request.UpdateTaskRequest
import com.taxplanner.dto.request.UpdateTaskStatusRequest
import com.taxplanner.dto.request.CreateCommentRequest
import com.taxplanner.dto.request.CreateChecklistRequest
import com.taxplanner.dto.request.UpdateChecklistRequest
import com.taxplanner.dto.response.TaskResponse
import com.taxplanner.dto.response.CommentResponse
import com.taxplanner.dto.response.ChecklistResponse
import com.taxplanner.security.UserPrincipal
import com.taxplanner.service.TaskService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Task management APIs")
class TaskController(
    private val taskService: TaskService
) {

    @GetMapping
    @Operation(summary = "Get tasks by board ID")
    fun getTasksByBoard(
        @RequestParam boardId: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getTasksByBoard(boardId, userPrincipal.id)
        return ResponseEntity.ok(tasks)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    fun getTaskById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<TaskResponse> {
        val task = taskService.getById(id, userPrincipal.id)
        return ResponseEntity.ok(task)
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    fun createTask(
        @Valid @RequestBody request: CreateTaskRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<TaskResponse> {
        val task = taskService.create(request, userPrincipal.id)
        return ResponseEntity.ok(task)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    fun updateTask(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTaskRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<TaskResponse> {
        val task = taskService.update(id, request, userPrincipal.id)
        return ResponseEntity.ok(task)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    fun deleteTask(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Map<String, String>> {
        taskService.delete(id, userPrincipal.id)
        return ResponseEntity.ok(mapOf("message" to "Task deleted successfully"))
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status")
    fun updateTaskStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTaskStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<TaskResponse> {
        val task = taskService.updateStatus(id, request.statusId, request.orderIndex, userPrincipal.id)
        return ResponseEntity.ok(task)
    }

    // Comment endpoints
    @GetMapping("/{id}/comments")
    @Operation(summary = "Get task comments")
    fun getTaskComments(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<CommentResponse>> {
        val comments = taskService.getComments(id, userPrincipal.id)
        return ResponseEntity.ok(comments)
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Create task comment")
    fun createTaskComment(
        @PathVariable id: UUID,
        @Valid @RequestBody request: CreateCommentRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<CommentResponse> {
        val comment = taskService.createComment(id, request, userPrincipal.id)
        return ResponseEntity.ok(comment)
    }

    // Checklist endpoints
    @GetMapping("/{id}/checklists")
    @Operation(summary = "Get task checklists")
    fun getTaskChecklists(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<ChecklistResponse>> {
        val checklists = taskService.getChecklists(id, userPrincipal.id)
        return ResponseEntity.ok(checklists)
    }

    @PostMapping("/{id}/checklists")
    @Operation(summary = "Create task checklist")
    fun createTaskChecklist(
        @PathVariable id: UUID,
        @Valid @RequestBody request: CreateChecklistRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<ChecklistResponse> {
        val checklist = taskService.createChecklist(id, request, userPrincipal.id)
        return ResponseEntity.ok(checklist)
    }

    @PutMapping("/{taskId}/checklists/{checklistId}")
    @Operation(summary = "Update task checklist")
    fun updateTaskChecklist(
        @PathVariable taskId: UUID,
        @PathVariable checklistId: UUID,
        @Valid @RequestBody request: UpdateChecklistRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<ChecklistResponse> {
        val checklist = taskService.updateChecklist(taskId, checklistId, request, userPrincipal.id)
        return ResponseEntity.ok(checklist)
    }

    @DeleteMapping("/{taskId}/checklists/{checklistId}")
    @Operation(summary = "Delete task checklist")
    fun deleteTaskChecklist(
        @PathVariable taskId: UUID,
        @PathVariable checklistId: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Map<String, String>> {
        taskService.deleteChecklist(taskId, checklistId, userPrincipal.id)
        return ResponseEntity.ok(mapOf("message" to "Checklist deleted successfully"))
    }
} 