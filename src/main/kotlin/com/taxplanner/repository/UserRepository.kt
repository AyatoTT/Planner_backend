package com.taxplanner.repository

import com.taxplanner.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    
    fun findByEmail(email: String): User?
    
    fun existsByEmail(email: String): Boolean
    
    fun findByEmailAndIsActiveTrue(email: String): User?
    
    @Query("SELECT u FROM User u WHERE u.email IN :emails AND u.isActive = true")
    fun findByEmailsAndIsActiveTrue(@Param("emails") emails: List<String>): List<User>
    
    @Query("SELECT u FROM User u WHERE u.name ILIKE %:query% OR u.email ILIKE %:query%")
    fun searchByNameOrEmail(@Param("query") query: String): List<User>
} 