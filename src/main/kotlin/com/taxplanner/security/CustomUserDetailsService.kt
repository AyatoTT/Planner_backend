package com.taxplanner.security

import com.taxplanner.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Transactional
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmailAndIsActiveTrue(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")

        return UserPrincipal.create(user)
    }

    @Transactional
    fun loadUserById(id: UUID): UserDetails {
        val user = userRepository.findById(id).orElseThrow {
            UsernameNotFoundException("User not found with id: $id")
        }

        if (!user.isActive) {
            throw UsernameNotFoundException("User is inactive: $id")
        }

        return UserPrincipal.create(user)
    }
} 