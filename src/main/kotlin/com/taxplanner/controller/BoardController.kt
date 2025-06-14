package com.taxplanner.controller

import com.taxplanner.dto.response.TaskStatusResponse
import com.taxplanner.dto.request.CreateBoardRequest
import com.taxplanner.dto.request.UpdateBoardRequest
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
} 