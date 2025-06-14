package com.taxplanner.service

import com.taxplanner.domain.entity.Project
import com.taxplanner.domain.entity.ProjectMember
import com.taxplanner.domain.enums.OrganizationRole
import com.taxplanner.dto.request.CreateProjectRequest
import com.taxplanner.dto.request.UpdateProjectRequest
import com.taxplanner.dto.response.ProjectResponse
import com.taxplanner.dto.response.BoardResponse
import com.taxplanner.dto.response.OrganizationSummaryResponse
import com.taxplanner.dto.response.ProjectSummaryResponse
import com.taxplanner.dto.response.TaskStatusResponse
import com.taxplanner.repository.ProjectRepository
import com.taxplanner.repository.OrganizationMemberRepository
import com.taxplanner.repository.OrganizationRepository
import com.taxplanner.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val organizationRepository: OrganizationRepository,
    private val organizationMemberRepository: OrganizationMemberRepository,
    private val userRepository: UserRepository
) {

    fun getAllByUser(userId: UUID): List<ProjectResponse> {
        val projects = projectRepository.findByUserId(userId)
        return projects.map { project ->
            val userRole = organizationMemberRepository.findByOrganizationIdAndUserId(
                project.organization.id, userId
            )?.role ?: OrganizationRole.VIEWER

            ProjectResponse(
                id = project.id,
                name = project.name,
                description = project.description,
                colorTheme = project.colorTheme,
                organization = OrganizationSummaryResponse(
                    id = project.organization.id,
                    name = project.organization.name,
                    logoUrl = project.organization.logoUrl
                ),
                memberCount = project.members.size,
                boardCount = project.boards.size,
                tagCount = project.tags.size,
                userRole = userRole,
                createdAt = project.createdAt,
                updatedAt = project.updatedAt
            )
        }
    }

    fun getById(id: UUID, userId: UUID): ProjectResponse {
        val project = projectRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Project not found")

        // Check if user has access to this project
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        return ProjectResponse(
            id = project.id,
            name = project.name,
            description = project.description,
            colorTheme = project.colorTheme,
            organization = OrganizationSummaryResponse(
                id = project.organization.id,
                name = project.organization.name,
                logoUrl = project.organization.logoUrl
            ),
            memberCount = project.members.size,
            boardCount = project.boards.size,
            tagCount = project.tags.size,
            userRole = membership.role,
            createdAt = project.createdAt,
            updatedAt = project.updatedAt
        )
    }

    fun getBoards(projectId: UUID, userId: UUID): List<BoardResponse> {
        val project = projectRepository.findByIdOrNull(projectId)
            ?: throw RuntimeException("Project not found")

        // Check if user has access to this project
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        return project.boards.map { board ->
            BoardResponse(
                id = board.id,
                name = board.name,
                description = board.description,
                viewType = board.viewType,
                project = ProjectSummaryResponse(
                    id = project.id,
                    name = project.name,
                    description = project.description,
                    colorTheme = project.colorTheme,
                    memberCount = project.members.size,
                    taskCount = project.boards.sumOf { it.tasks.size },
                    completedTaskCount = project.boards.sumOf { board -> 
                        board.tasks.count { it.status.name.lowercase() == "done" || it.status.name.lowercase() == "completed" }
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
    }

    fun create(request: CreateProjectRequest, userId: UUID): ProjectResponse {
        val organization = organizationRepository.findByIdOrNull(request.organizationId)
            ?: throw RuntimeException("Organization not found")

        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            request.organizationId, userId
        ) ?: throw RuntimeException("Access denied")

        if (membership.role == OrganizationRole.VIEWER) {
            throw RuntimeException("Insufficient permissions to create projects")
        }

        val user = userRepository.findByIdOrNull(userId)
            ?: throw RuntimeException("User not found")

        val project = Project(
            name = request.name,
            description = request.description,
            colorTheme = request.colorTheme,
            organization = organization
        )
        val savedProject = projectRepository.save(project)

        // Add creator as project member
        val projectMember = ProjectMember(
            project = savedProject,
            user = user,
            role = membership.role
        )
        savedProject.members.add(projectMember)
        projectRepository.save(savedProject)

        return ProjectResponse(
            id = savedProject.id,
            name = savedProject.name,
            description = savedProject.description,
            colorTheme = savedProject.colorTheme,
            organization = OrganizationSummaryResponse(
                id = savedProject.organization.id,
                name = savedProject.organization.name,
                logoUrl = savedProject.organization.logoUrl
            ),
            memberCount = 1,
            boardCount = 0,
            tagCount = 0,
            userRole = membership.role,
            createdAt = savedProject.createdAt,
            updatedAt = savedProject.updatedAt
        )
    }

    fun update(id: UUID, request: UpdateProjectRequest, userId: UUID): ProjectResponse {
        val project = projectRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Project not found")

        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        if (membership.role == OrganizationRole.VIEWER) {
            throw RuntimeException("Insufficient permissions to update project")
        }

        request.name?.let { project.name = it }
        request.description?.let { project.description = it }
        request.colorTheme?.let { project.colorTheme = it }

        val updatedProject = projectRepository.save(project)

        return ProjectResponse(
            id = updatedProject.id,
            name = updatedProject.name,
            description = updatedProject.description,
            colorTheme = updatedProject.colorTheme,
            organization = OrganizationSummaryResponse(
                id = updatedProject.organization.id,
                name = updatedProject.organization.name,
                logoUrl = updatedProject.organization.logoUrl
            ),
            memberCount = updatedProject.members.size,
            boardCount = updatedProject.boards.size,
            tagCount = updatedProject.tags.size,
            userRole = membership.role,
            createdAt = updatedProject.createdAt,
            updatedAt = updatedProject.updatedAt
        )
    }

    fun delete(id: UUID, userId: UUID) {
        val project = projectRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Project not found")

        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(
            project.organization.id, userId
        ) ?: throw RuntimeException("Access denied")

        if (membership.role != OrganizationRole.OWNER && membership.role != OrganizationRole.ADMIN) {
            throw RuntimeException("Insufficient permissions to delete project")
        }

        projectRepository.delete(project)
    }
} 