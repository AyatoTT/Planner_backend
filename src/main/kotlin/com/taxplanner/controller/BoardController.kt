package com.taxplanner.controller

import com.taxplanner.dto.response.TaskStatusResponse
import com.taxplanner.dto.request.CreateBoardRequest
import com.taxplanner.dto.request.UpdateBoardRequest
import com.taxplanner.dto.request.CreateTaskStatusRequest
import com.taxplanner.dto.request.UpdateBoardStatusRequest
import com.taxplanner.dto.request.ReorderStatusesRequest
import com.taxplanner.dto.response.BoardResponse
import com.taxplanner.dto.response.TaskResponse
import com.taxplanner.security.UserPrincipal
import com.taxplanner.service.BoardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/boards")
@Tag(name = "Boards", description = "Board management APIs")
class BoardController(
    private val boardService: BoardService
) {

    @GetMapping("/{id}")
    @Operation(summary = "Get board by ID")
    fun getBoardById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<BoardResponse> {
        val board = boardService.getById(id, userPrincipal.id)
        return ResponseEntity.ok(board)
    }

    @GetMapping("/{id}/tasks")
    @Operation(summary = "Get all tasks for a board")
    fun getBoardTasks(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<TaskResponse>> {
        val tasks = boardService.getTasks(id, userPrincipal.id)
        return ResponseEntity.ok(tasks)
    }

    @PostMapping
    @Operation(summary = "Create a new board")
    fun createBoard(
        @Valid @RequestBody request: CreateBoardRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<BoardResponse> {
        val board = boardService.create(request, userPrincipal.id)
        return ResponseEntity.ok(board)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update board")
    fun updateBoard(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateBoardRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<BoardResponse> {
        val board = boardService.update(id, request, userPrincipal.id)
        return ResponseEntity.ok(board)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete board")
    fun deleteBoard(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Map<String, String>> {
        boardService.delete(id, userPrincipal.id)
        return ResponseEntity.ok(mapOf("message" to "Board deleted successfully"))
    }

    @GetMapping("/{id}/statuses")
    @Operation(summary = "Get board statuses")
    fun getBoardStatuses(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<TaskStatusResponse>> {
        val statuses = boardService.getStatuses(id, userPrincipal.id)
        return ResponseEntity.ok(statuses)
    }

    @PostMapping("/{id}/statuses")
    @Operation(summary = "Create board status")
    fun createBoardStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: CreateTaskStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<TaskStatusResponse> {
        val status = boardService.createStatus(id, request, userPrincipal.id)
        return ResponseEntity.ok(status)
    }

    @PutMapping("/{boardId}/statuses/{statusId}")
    @Operation(summary = "Update board status")
    fun updateBoardStatus(
        @PathVariable boardId: UUID,
        @PathVariable statusId: UUID,
        @Valid @RequestBody request: UpdateBoardStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<TaskStatusResponse> {
        val status = boardService.updateStatus(boardId, statusId, request, userPrincipal.id)
        return ResponseEntity.ok(status)
    }

    @DeleteMapping("/{boardId}/statuses/{statusId}")
    @Operation(summary = "Delete board status")
    fun deleteBoardStatus(
        @PathVariable boardId: UUID,
        @PathVariable statusId: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Map<String, String>> {
        boardService.deleteStatus(boardId, statusId, userPrincipal.id)
        return ResponseEntity.ok(mapOf("message" to "Status deleted successfully"))
    }

    @PatchMapping("/{id}/statuses/reorder")
    @Operation(summary = "Reorder board statuses")
    fun reorderBoardStatuses(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ReorderStatusesRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<TaskStatusResponse>> {
        val statuses = boardService.reorderStatuses(id, request, userPrincipal.id)
        return ResponseEntity.ok(statuses)
    }

    @PostMapping("/{id}/statuses/sync-completion")
    @Operation(summary = "Sync task completion status with final statuses")
    fun syncTaskCompletion(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Map<String, Any>> {
        val result = boardService.syncTaskCompletion(id, userPrincipal.id)
        return ResponseEntity.ok(result)
    }
} 