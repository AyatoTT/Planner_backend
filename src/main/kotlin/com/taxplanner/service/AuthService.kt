package com.taxplanner.service

import com.taxplanner.domain.entity.Organization
import com.taxplanner.domain.entity.OrganizationMember
import com.taxplanner.domain.entity.User
import com.taxplanner.domain.enums.OrganizationRole
import com.taxplanner.dto.request.LoginRequest
import com.taxplanner.dto.request.RegisterRequest
import com.taxplanner.dto.response.AuthResponse
import com.taxplanner.dto.response.UserResponse
import com.taxplanner.repository.OrganizationMemberRepository
import com.taxplanner.repository.OrganizationRepository
import com.taxplanner.repository.UserRepository
import com.taxplanner.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
    private val organizationMemberRepository: OrganizationMemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val tokenProvider: JwtTokenProvider
) {

    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email is already registered")
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name
        )

        val savedUser = userRepository.save(user)

        // Create default organization for the user
        val organization = Organization(
            name = "${savedUser.name}'s Organization"
        )
        val savedOrganization = organizationRepository.save(organization)

        // Add user as owner of the organization
        val organizationMember = OrganizationMember(
            organization = savedOrganization,
            user = savedUser,
            role = OrganizationRole.OWNER
        )
        organizationMemberRepository.save(organizationMember)

        val token = tokenProvider.generateTokenFromUserId(savedUser.id)

        return AuthResponse(
            accessToken = token,
            user = UserResponse(
                id = savedUser.id,
                email = savedUser.email,
                name = savedUser.name,
                avatarUrl = savedUser.avatarUrl,
                emailVerified = savedUser.emailVerified,
                createdAt = savedUser.createdAt
            )
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        val token = tokenProvider.generateToken(authentication)
        
        val user = userRepository.findByEmailAndIsActiveTrue(request.email)
            ?: throw IllegalArgumentException("User not found")

        return AuthResponse(
            accessToken = token,
            user = UserResponse(
                id = user.id,
                email = user.email,
                name = user.name,
                avatarUrl = user.avatarUrl,
                emailVerified = user.emailVerified,
                createdAt = user.createdAt
            )
        )
    }
} 