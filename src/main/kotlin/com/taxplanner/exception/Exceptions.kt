package com.taxplanner.exception

// Base exception classes
open class PlannerException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

// Entity not found exceptions
open class EntityNotFoundException(entityName: String, id: Any) : PlannerException("$entityName not found with id: $id")
class UserNotFoundException(id: Any) : EntityNotFoundException("User", id)
class OrganizationNotFoundException(id: Any) : EntityNotFoundException("Organization", id)
class ProjectNotFoundException(id: Any) : EntityNotFoundException("Project", id)
class BoardNotFoundException(id: Any) : EntityNotFoundException("Board", id)
class TaskNotFoundException(id: Any) : EntityNotFoundException("Task", id)
class TaskStatusNotFoundException(id: Any) : EntityNotFoundException("TaskStatus", id)
class TagNotFoundException(id: Any) : EntityNotFoundException("Tag", id)
class CommentNotFoundException(id: Any) : EntityNotFoundException("Comment", id)
class ChecklistNotFoundException(id: Any) : EntityNotFoundException("Checklist", id)

// Permission exceptions
class AccessDeniedException(message: String = "Access denied") : PlannerException(message)
class InsufficientPermissionsException(operation: String) : PlannerException("Insufficient permissions to $operation")

// Validation exceptions
class ValidationException(message: String) : PlannerException(message)
class DuplicateEntityException(entityName: String, field: String, value: Any) : 
    PlannerException("$entityName with $field '$value' already exists")

// Authentication exceptions
open class AuthenticationException(message: String) : PlannerException(message)
class InvalidCredentialsException : AuthenticationException("Invalid credentials")
class TokenExpiredException : AuthenticationException("Token has expired")
class InvalidTokenException : AuthenticationException("Invalid token")

// Business logic exceptions
open class BusinessLogicException(message: String) : PlannerException(message)
class InvalidStatusTransitionException(from: String, to: String) : 
    BusinessLogicException("Cannot transition from status '$from' to '$to'")
class TaskAlreadyAssignedException(taskId: Any) : 
    BusinessLogicException("Task $taskId is already assigned") 