package com.taxplanner.controller

import com.taxplanner.dto.request.CreateProjectRequest
import com.taxplanner.dto.request.UpdateProjectRequest
import com.taxplanner.dto.response.ProjectResponse
import com.taxplanner.dto.response.BoardResponse
import com.taxplanner.security.UserPrincipal
import com.taxplanner.service.ProjectService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/projects")
@Tag(name = "Projects", description = "Project management APIs")
class ProjectController(
    private val projectService: ProjectService
) {

    @GetMapping
    @Operation(summary = "Get all projects for current user")
    fun getAllProjects(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<ProjectResponse>> {
        val projects = projectService.getAllByUser(userPrincipal.id)
        return ResponseEntity.ok(projects)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    fun getProjectById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<ProjectResponse> {
        val project = projectService.getById(id, userPrincipal.id)
        return ResponseEntity.ok(project)
    }

    @GetMapping("/{id}/boards")
    @Operation(summary = "Get all boards for a project")
    fun getProjectBoards(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<BoardResponse>> {
        val boards = projectService.getBoards(id, userPrincipal.id)
        return ResponseEntity.ok(boards)
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    fun createProject(
        @Valid @RequestBody request: CreateProjectRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<ProjectResponse> {
        val project = projectService.create(request, userPrincipal.id)
        return ResponseEntity.ok(project)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project")
    fun updateProject(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateProjectRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<ProjectResponse> {
        val project = projectService.update(id, request, userPrincipal.id)
        return ResponseEntity.ok(project)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project")
    fun deleteProject(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Map<String, String>> {
        projectService.delete(id, userPrincipal.id)
        return ResponseEntity.ok(mapOf("message" to "Project deleted successfully"))
    }
} 