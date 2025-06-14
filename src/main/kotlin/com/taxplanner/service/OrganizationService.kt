package com.taxplanner.service

import com.taxplanner.domain.entity.Organization
import com.taxplanner.domain.entity.OrganizationMember
import com.taxplanner.domain.entity.User
import com.taxplanner.domain.enums.OrganizationRole
import com.taxplanner.dto.request.CreateOrganizationRequest
import com.taxplanner.dto.request.UpdateOrganizationRequest
import com.taxplanner.dto.response.OrganizationResponse
import com.taxplanner.repository.OrganizationRepository
import com.taxplanner.repository.OrganizationMemberRepository
import com.taxplanner.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class OrganizationService(
    private val organizationRepository: OrganizationRepository,
    private val organizationMemberRepository: OrganizationMemberRepository,
    private val userRepository: UserRepository
) {

    fun getAllByUser(userId: UUID): List<OrganizationResponse> {
        val memberships = organizationMemberRepository.findByUserId(userId)
        return memberships.map { membership ->
            val org = membership.organization
            OrganizationResponse(
                id = org.id,
                name = org.name,
                description = org.description,
                logoUrl = org.logoUrl,
                memberCount = organizationMemberRepository.countByOrganizationId(org.id),
                projectCount = org.projects.size,
                userRole = membership.role,
                createdAt = org.createdAt
            )
        }
    }

    fun getById(id: UUID, userId: UUID): OrganizationResponse {
        val organization = organizationRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Organization not found")
        
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(id, userId)
            ?: throw RuntimeException("Access denied")

        return OrganizationResponse(
            id = organization.id,
            name = organization.name,
            description = organization.description,
            logoUrl = organization.logoUrl,
            memberCount = organizationMemberRepository.countByOrganizationId(id),
            projectCount = organization.projects.size,
            userRole = membership.role,
            createdAt = organization.createdAt
        )
    }

    fun create(request: CreateOrganizationRequest, userId: UUID): OrganizationResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw RuntimeException("User not found")

        val organization = Organization(
            name = request.name,
            description = request.description
        )
        val savedOrganization = organizationRepository.save(organization)

        // Add creator as owner
        val membership = OrganizationMember(
            organization = savedOrganization,
            user = user,
            role = OrganizationRole.OWNER
        )
        organizationMemberRepository.save(membership)

        return OrganizationResponse(
            id = savedOrganization.id,
            name = savedOrganization.name,
            description = savedOrganization.description,
            logoUrl = savedOrganization.logoUrl,
            memberCount = 1,
            projectCount = 0,
            userRole = OrganizationRole.OWNER,
            createdAt = savedOrganization.createdAt
        )
    }

    fun update(id: UUID, request: UpdateOrganizationRequest, userId: UUID): OrganizationResponse {
        val organization = organizationRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Organization not found")

        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(id, userId)
            ?: throw RuntimeException("Access denied")

        if (membership.role != OrganizationRole.OWNER && membership.role != OrganizationRole.ADMIN) {
            throw RuntimeException("Insufficient permissions")
        }

        request.name?.let { organization.name = it }
        request.description?.let { organization.description = it }

        val updatedOrganization = organizationRepository.save(organization)

        return OrganizationResponse(
            id = updatedOrganization.id,
            name = updatedOrganization.name,
            description = updatedOrganization.description,
            logoUrl = updatedOrganization.logoUrl,
            memberCount = organizationMemberRepository.countByOrganizationId(id),
            projectCount = updatedOrganization.projects.size,
            userRole = membership.role,
            createdAt = updatedOrganization.createdAt
        )
    }

    fun delete(id: UUID, userId: UUID) {
        val organization = organizationRepository.findByIdOrNull(id)
            ?: throw RuntimeException("Organization not found")

        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(id, userId)
            ?: throw RuntimeException("Access denied")

        if (membership.role != OrganizationRole.OWNER) {
            throw RuntimeException("Only owners can delete organizations")
        }

        organizationRepository.delete(organization)
    }
} 