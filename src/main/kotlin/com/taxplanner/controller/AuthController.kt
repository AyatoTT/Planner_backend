package com.taxplanner.controller

import com.taxplanner.dto.request.LoginRequest
import com.taxplanner.dto.request.RegisterRequest
import com.taxplanner.dto.response.AuthResponse
import com.taxplanner.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    fun logout(): ResponseEntity<Map<String, String>> {
        // Since we're using stateless JWT, logout is handled on client side
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }
} 