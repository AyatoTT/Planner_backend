package com.taxplanner.controller

import com.taxplanner.dto.request.CreateOrganizationRequest
import com.taxplanner.dto.request.UpdateOrganizationRequest
import com.taxplanner.dto.response.OrganizationResponse
import com.taxplanner.security.UserPrincipal
import com.taxplanner.service.OrganizationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/organizations")
@Tag(name = "Organizations", description = "Organization management APIs")
class OrganizationController(
    private val organizationService: OrganizationService
) {

    @GetMapping
    @Operation(summary = "Get all organizations for current user")
    fun getAllOrganizations(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<OrganizationResponse>> {
        val organizations = organizationService.getAllByUser(userPrincipal.id)
        return ResponseEntity.ok(organizations)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID")
    fun getOrganizationById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<OrganizationResponse> {
        val organization = organizationService.getById(id, userPrincipal.id)
        return ResponseEntity.ok(organization)
    }

    @PostMapping
    @Operation(summary = "Create a new organization")
    fun createOrganization(
        @Valid @RequestBody request: CreateOrganizationRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<OrganizationResponse> {
        val organization = organizationService.create(request, userPrincipal.id)
        return ResponseEntity.ok(organization)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update organization")
    fun updateOrganization(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateOrganizationRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<OrganizationResponse> {
        val organization = organizationService.update(id, request, userPrincipal.id)
        return ResponseEntity.ok(organization)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete organization")
    fun deleteOrganization(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<Map<String, String>> {
        organizationService.delete(id, userPrincipal.id)
        return ResponseEntity.ok(mapOf("message" to "Organization deleted successfully"))
    }
} 