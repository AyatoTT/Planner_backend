package com.taxplanner.service

import com.taxplanner.domain.entity.Organization
import com.taxplanner.domain.entity.OrganizationMember
import com.taxplanner.domain.entity.User
import com.taxplanner.domain.enums.OrganizationRole
import com.taxplanner.dto.request.CreateOrganizationRequest
import com.taxplanner.dto.request.UpdateOrganizationRequest
import com.taxplanner.dto.request.InviteMemberRequest
import com.taxplanner.dto.response.OrganizationResponse
import com.taxplanner.dto.response.OrganizationMemberResponse
import com.taxplanner.repository.OrganizationRepository
import com.taxplanner.repository.OrganizationMemberRepository
import com.taxplanner.repository.UserRepository
import com.taxplanner.exception.*
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
            ?: throw OrganizationNotFoundException(id)
        
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(id, userId)
            ?: throw AccessDeniedException()

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
            ?: throw UserNotFoundException(userId)

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
            ?: throw OrganizationNotFoundException(id)

        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(id, userId)
            ?: throw AccessDeniedException()

        if (membership.role != OrganizationRole.OWNER && membership.role != OrganizationRole.ADMIN) {
            throw InsufficientPermissionsException("update organization")
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
            ?: throw OrganizationNotFoundException(id)

        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(id, userId)
            ?: throw AccessDeniedException()

        if (membership.role != OrganizationRole.OWNER) {
            throw InsufficientPermissionsException("delete organization")
        }

        organizationRepository.delete(organization)
    }

    fun getMembers(organizationId: UUID, userId: UUID): List<OrganizationMemberResponse> {
        val organization = organizationRepository.findByIdOrNull(organizationId)
            ?: throw OrganizationNotFoundException(organizationId)
        
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(organizationId, userId)
            ?: throw AccessDeniedException()

        val members = organizationMemberRepository.findByOrganizationId(organizationId)
        return members.map { member ->
            OrganizationMemberResponse(
                id = member.id,
                userId = member.user.id,
                userName = member.user.name,
                userEmail = member.user.email,
                role = member.role.toString(),
                organizationId = member.organization.id,
                joinedAt = member.joinedAt
            )
        }
    }

    fun inviteMember(organizationId: UUID, request: InviteMemberRequest, userId: UUID): OrganizationMemberResponse {
        val organization = organizationRepository.findByIdOrNull(organizationId)
            ?: throw OrganizationNotFoundException(organizationId)
        
        val membership = organizationMemberRepository.findByOrganizationIdAndUserId(organizationId, userId)
            ?: throw AccessDeniedException()

        if (membership.role == OrganizationRole.VIEWER) {
            throw InsufficientPermissionsException("invite members")
        }

        // Find user by email
        val userToInvite = userRepository.findByEmail(request.email)
            ?: throw UserNotFoundException("email: ${request.email}")

        // Check if user is already a member
        val existingMembership = organizationMemberRepository.findByOrganizationIdAndUserId(
            organizationId, userToInvite.id
        )
        if (existingMembership != null) {
            throw DuplicateEntityException("Member", "email", request.email)
        }

        // Parse role
        val role = try {
            OrganizationRole.valueOf(request.role.uppercase())
        } catch (e: IllegalArgumentException) {
            throw ValidationException("Invalid role: ${request.role}")
        }

        // Only owners can invite other owners/admins
        if ((role == OrganizationRole.OWNER || role == OrganizationRole.ADMIN) && 
            membership.role != OrganizationRole.OWNER) {
            throw InsufficientPermissionsException("invite ${role.name.lowercase()}s")
        }

        val newMember = OrganizationMember(
            organization = organization,
            user = userToInvite,
            role = role
        )
        val savedMember = organizationMemberRepository.save(newMember)

        return OrganizationMemberResponse(
            id = savedMember.id,
            userId = savedMember.user.id,
            userName = savedMember.user.name,
            userEmail = savedMember.user.email,
            role = savedMember.role.toString(),
            organizationId = savedMember.organization.id,
            joinedAt = savedMember.joinedAt
        )
    }
} 