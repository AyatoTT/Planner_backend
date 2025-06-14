# API Endpoints Documentation

## Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration  
- `POST /api/auth/logout` - User logout

## Organizations
- `GET /api/organizations` - Get all organizations for current user
- `GET /api/organizations/{id}` - Get organization by ID
- `POST /api/organizations` - Create new organization
- `PUT /api/organizations/{id}` - Update organization
- `DELETE /api/organizations/{id}` - Delete organization

### Organization Members
- `GET /api/organizations/{id}/members` - Get organization members
- `POST /api/organizations/{id}/members` - Invite member to organization

## Projects  
- `GET /api/projects` - Get all projects for current user
- `GET /api/projects/{id}` - Get project by ID
- `POST /api/projects` - Create new project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project
- `GET /api/projects/{id}/boards` - Get all boards for a project

### Project Tags
- `GET /api/projects/{id}/tags` - Get project tags
- `POST /api/projects/{id}/tags` - Create project tag

## Boards
- `GET /api/boards/{id}` - Get board by ID
- `POST /api/boards` - Create new board
- `PUT /api/boards/{id}` - Update board
- `DELETE /api/boards/{id}` - Delete board
- `GET /api/boards/{id}/tasks` - Get all tasks for a board

### Board Statuses
- `GET /api/boards/{id}/statuses` - Get board statuses
- `POST /api/boards/{id}/statuses` - Create board status
- `PUT /api/boards/{boardId}/statuses/{statusId}` - Update board status
- `DELETE /api/boards/{boardId}/statuses/{statusId}` - Delete board status
- `PATCH /api/boards/{id}/statuses/reorder` - Reorder board statuses (drag & drop)

## Tasks
- `GET /api/tasks` - Get tasks by board ID (query param: boardId)
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PATCH /api/tasks/{id}/status` - Update task status (for drag & drop)

### Task Comments
- `GET /api/tasks/{id}/comments` - Get task comments
- `POST /api/tasks/{id}/comments` - Create task comment

### Task Checklists
- `GET /api/tasks/{id}/checklists` - Get task checklists
- `POST /api/tasks/{id}/checklists` - Create task checklist
- `PUT /api/tasks/{taskId}/checklists/{checklistId}` - Update task checklist
- `DELETE /api/tasks/{taskId}/checklists/{checklistId}` - Delete task checklist

## Key Features Implemented

### ✅ Drag & Drop Support
The critical `PATCH /api/tasks/{id}/status` endpoint is now implemented, enabling:
- Moving tasks between different statuses
- Reordering tasks within the same status
- Real-time status updates

### ✅ Enhanced Error Handling
- Custom exception types with appropriate HTTP status codes
- Detailed error messages with field validation
- Consistent error response format

### ✅ Complete CRUD Operations
All entities now have full CRUD support:
- Organizations with member management
- Projects with tag management
- Boards with status management
- Tasks with comments and checklists

### ✅ Permission-Based Access Control
- Role-based access control for organizations
- Proper permission checks for all operations
- Security validation at service layer

## Request/Response Examples

### Update Task Status (Drag & Drop)
```
PATCH /api/tasks/{taskId}/status
Query Parameters:
- statusId: UUID (required)
- orderIndex: Integer (optional)
```

### Create Comment
```
POST /api/tasks/{taskId}/comments
{
  "content": "This is a comment"
}
```

### Create Checklist Item
```
POST /api/tasks/{taskId}/checklists
{
  "title": "Complete documentation"
}
```

### Update Checklist Item
```
PUT /api/tasks/{taskId}/checklists/{checklistId}
{
  "title": "Updated title",
  "isCompleted": true
}
``` 