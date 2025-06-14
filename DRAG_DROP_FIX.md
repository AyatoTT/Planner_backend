# Исправление ошибки 500 для Drag & Drop API

## Проблема
При выполнении PATCH запроса `/api/tasks/{id}/status` с телом:
```json
{
    "statusId": "50ac76f3-ae82-49b7-a05e-d85c50452c4b"
}
```

Получали ошибку 500 "Internal Server Error".

## Причина
Контроллер `TaskController.updateTaskStatus()` ожидал параметры как `@RequestParam`, но фронтенд отправлял их в теле запроса как JSON.

## Исправления

### 1. Создан новый DTO для запроса обновления статуса задачи
**Файл**: `backend/src/main/kotlin/com/taxplanner/dto/request/UpdateTaskStatusRequest.kt`
```kotlin
data class UpdateTaskStatusRequest(
    @field:NotNull(message = "Status ID is required")
    val statusId: UUID,
    val orderIndex: Int? = null
)
```

### 2. Обновлен TaskController
**Было**:
```kotlin
@PatchMapping("/{id}/status")
fun updateTaskStatus(
    @PathVariable id: UUID,
    @RequestParam statusId: UUID,
    @RequestParam(required = false) orderIndex: Int?,
    @AuthenticationPrincipal userPrincipal: UserPrincipal
): ResponseEntity<TaskResponse>
```

**Стало**:
```kotlin
@PatchMapping("/{id}/status")
fun updateTaskStatus(
    @PathVariable id: UUID,
    @Valid @RequestBody request: UpdateTaskStatusRequest,
    @AuthenticationPrincipal userPrincipal: UserPrincipal
): ResponseEntity<TaskResponse>
```

### 3. Исправлены типы в Response DTO
- `TaskStatusResponse.color`: изменен с `String?` на `String`
- `TagResponse.color`: изменен с `String?` на `String`

### 4. Добавлена валидация в TaskService.updateStatus()
```kotlin
// Check if the new status belongs to the same board as the task
if (status.board.id != task.board.id) {
    throw ValidationException("Status does not belong to the same board as the task")
}
```

### 5. Создан отдельный DTO для статусов доски
**Файл**: `backend/src/main/kotlin/com/taxplanner/dto/request/UpdateBoardStatusRequest.kt`
```kotlin
data class UpdateBoardStatusRequest(
    val name: String?,
    val color: String?,
    val orderIndex: Int?
)
```

## Результат

✅ **Drag & Drop теперь работает!**

API эндпоинт `PATCH /api/tasks/{id}/status` теперь:
- Принимает JSON в теле запроса
- Правильно валидирует данные
- Проверяет, что новый статус принадлежит той же доске
- Возвращает обновленную задачу

**Тестирование**:
```bash
curl -X PATCH http://localhost:8080/api/tasks/{taskId}/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"statusId": "{statusId}", "orderIndex": 1}'
``` 