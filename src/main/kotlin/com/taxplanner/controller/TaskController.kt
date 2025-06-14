package com.taxplanner.controller

import com.taxplanner.dto.request.CreateTaskRequest
import com.taxplanner.dto.request.UpdateTaskRequest
import com.taxplanner.dto.response.TaskResponse
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
        @RequestParam statusId: UUID,
        @RequestParam(required = false) orderIndex: Int?,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<TaskResponse> {
        val task = taskService.updateStatus(id, statusId, orderIndex, userPrincipal.id)
        return ResponseEntity.ok(task)
    }
} 