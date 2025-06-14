package com.taxplanner.domain.enums

enum class OrganizationRole(val displayName: String, val level: Int) {
    OWNER("Owner", 3),
    ADMIN("Admin", 2),
    MEMBER("Member", 1),
    VIEWER("Viewer", 0)
} 