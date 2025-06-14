package com.taxplanner.security

import com.taxplanner.domain.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class UserPrincipal(
    val id: UUID,
    val email: String,
    val fullName: String,
    private val password: String,
    private val enabled: Boolean = true,
    private val authorities: Collection<GrantedAuthority> = emptyList()
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = enabled

    companion object {
        fun create(user: User): UserPrincipal {
            val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

            return UserPrincipal(
                id = user.id,
                email = user.email,
                fullName = user.name,
                password = user.password,
                enabled = user.isActive,
                authorities = authorities
            )
        }
    }
} 