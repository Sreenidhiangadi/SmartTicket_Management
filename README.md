# Ticket Management System

A backend-driven, event-based ticket management system designed for customer support workflows.  
The system supports ticket creation, assignment, SLA tracking, notifications, and role-based access.

---

## Overview

This project implements a full ticket lifecycle used by support teams:

- End users create tickets
- Tickets are auto or manually assigned to agents
- Agents work, resolve, and update ticket status
- SLA breaches are detected and escalated
- Notifications are delivered asynchronously

The architecture follows microservice principles with event-driven communication using Kafka.

config server : https://github.com/Sreenidhiangadi/ticket-config-repo
frontend-deployment link: https://smart-ticket-managment-system.netlify.app/login
---

## Architecture

### Services

- **API Gateway**
  - Central entry point
  - JWT validation
  - Request routing

- **User Service**
  - Authentication and authorization
  - User and role management

- **Ticket Service**
  - Ticket lifecycle management
  - Status transitions
  - Event publishing

- **Assignment Service**
  - Auto and manual ticket assignment
  - SLA calculation and breach detection

- **Notification Service**
  - In-app and email notifications
  - Event consumption

---

## Technology Stack

### Backend
- Java / Spring Boot
- Spring WebFlux (WebClient)
- Spring Security (JWT)
- Apache Kafka
- MongoDB

### Frontend
- Angular

### Infrastructure
- Docker
- Docker Compose

---

## Ticket Lifecycle

1. **CREATED**
   - User submits a ticket
   - Ticket is persisted

2. **ASSIGNED**
   - Assignment service selects an agent
   - SLA is calculated

3. **IN_PROGRESS**
   - Agent starts working on the ticket

4. **RESOLVED**
   - Agent resolves the issue

5. **CLOSED**
   - Manager closes the ticket

---

## Event Flow (Kafka)

| Event Name | Description |
|-----------|------------|
| `TICKET_CREATED` | Ticket created successfully |
| `TICKET_ASSIGNED` | Ticket assigned to agent |
| `STATUS_CHANGED` | Ticket status updated |
| `TICKET_RESOLVED` | Ticket resolved |
| `SLA_BREACHED` | SLA deadline missed |

---

## Data Model

### USERS
- `_id`
- `name`
- `email`
- `password`
- `active`
- `roles`

### TICKETS
- `_id`
- `title`
- `description`
- `category`
- `priority`
- `status`
- `createdBy`
- `assignedTo`
- `slaDueAt`
- `slaBreached`
- `createdAt`
- `updatedAt`
- `resolvedAt`
- `closedAt`

### ASSIGNMENTS
- `_id`
- `ticketId`
- `agentId`
- `assignedAt`
- `slaDueAt`
- `priority`
- `escalated`

### NOTIFICATIONS
- `_id`
- `userId`
- `title`
- `message`
- `type`
- `read`
- `createdAt`

---

## Roles and Permissions

| Role | Capabilities |
|----|-------------|
| USER | Create and view tickets |
| AGENT | Work on assigned tickets |
| MANAGER | Assign, close, escalate tickets |
| ADMIN | Manage users and roles |

---

## API Endpoints (Sample)
All APIs are exposed via the API Gateway and secured using JWT authentication unless stated otherwise.

---
## Ticket Service APIs

Base path:

### Ticket Management

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | `/tickets` | Create a new ticket |
| GET | `/tickets/{id}` | Get ticket by ID |
| GET | `/tickets/user/{userId}` | Get tickets created by a user |
| GET | `/tickets/agent/{agentId}` | Get tickets assigned to an agent |
| GET | `/tickets/status/{status}` | Get tickets by status |
| PUT | `/tickets/{id}/assign` | Assign ticket to agent |
| PUT | `/tickets/{id}/status` | Update ticket status |
| PUT | `/tickets/{id}/close` | Close ticket |
| PUT | `/tickets/{id}/reopen` | Reopen ticket |
| PUT | `/tickets/{id}/cancel` | Cancel ticket |

---

### Ticket Queries and Filters

| Method | Endpoint | Description |
|------|---------|-------------|
| GET | `/tickets` | Filter tickets by status and priority |
| GET | `/tickets?status=&priority=&page=&size=` | Paginated ticket search |

---

### Ticket Comments and History

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | `/tickets/{id}/comments` | Add comment to ticket |
| GET | `/tickets/{id}/comments` | Get ticket comments |
| GET | `/tickets/{id}/history` | Get ticket status change history |

---

## Reporting APIs

Base path:

| Method | Endpoint | Description |
|------|---------|-------------|
| GET | `/reports/tickets-by-status` | Ticket count grouped by status |
| GET | `/reports/tickets-by-priority` | Ticket count grouped by priority |
| GET | `/reports/avg-resolution-time` | Average ticket resolution time |
| GET | `/reports/sla-breaches` | SLA breach report |

---

## Dashboard APIs

| Method | Endpoint | Description |
|------|---------|-------------|
| GET | `/dashboard/summary` | Overall ticket and SLA summary |

---

## Assignment Service APIs

The Assignment Service handles agent selection, SLA enforcement, and escalations.

Base path:

### Assignment and SLA

| Method | Endpoint | Description |
|------|---------|-------------|
| POST | `/api/assign/{ticketId}` | Assign ticket to agent |
| POST | `/api/sla/check` | Trigger SLA evaluation |

---

### Agent Workload

| Method | Endpoint | Description |
|------|---------|-------------|
| GET | `/api/agents/workload` | Get workload of all agents |
| GET | `/api/agent/{agentId}` | Get workload of a specific agent |

---

### Escalations

| Method | Endpoint | Description |
|------|---------|-------------|
| GET | `/api/escalations/manager/{mgrId}` | Get escalations for a manager |
| GET | `/api/escalations/logs` | Get escalation audit logs |

---

## Database

### Ticket Collection

Primary collection used by the Ticket Service.

**Collection Name:** `tickets`

**Key Fields:**
- `_id`
- `title`
- `description`
- `category`
- `priority`
- `status`
- `createdBy`
- `assignedTo`
- `slaDueAt`
- `slaBreached`
- `createdAt`
- `updatedAt`
- `resolvedAt`
- `closedAt`

### UI Screenshots
1)User Register(Validations):
<img width="940" height="479" alt="image" src="https://github.com/user-attachments/assets/ad607297-70ea-447f-abbe-adfa0ebf9d54" />
<br>
2)User Register(Successful):
<img width="940" height="466" alt="image" src="https://github.com/user-attachments/assets/7c81c5e2-7b4e-4a40-800e-439c3a359f97" />
<br>
3)User login(Validations):
<img width="940" height="474" alt="image" src="https://github.com/user-attachments/assets/fe3b9b93-7cf9-4075-805b-faf60d3c5c2e" />
<br>
4)User Login (Successful):
<img width="940" height="470" alt="image" src="https://github.com/user-attachments/assets/3c9254e6-804c-4008-b9ff-192f96933428" />
<br>
5)Home Page:
<img width="940" height="454" alt="image" src="https://github.com/user-attachments/assets/4d2c32cd-e70f-4115-820b-36f7db7d5a5f" />
<br>
6)User raise ticket:
<img width="940" height="456" alt="image" src="https://github.com/user-attachments/assets/bd33f09d-1b01-4c08-9ef5-fbfba01ec13b" />
<br>
7)Ticket details:
<img width="940" height="427" alt="image" src="https://github.com/user-attachments/assets/67f43a05-7048-42ba-8581-1c46b8f97bfe" />
<br>
8)Agent Queue:
<img width="940" height="467" alt="image" src="https://github.com/user-attachments/assets/0681863c-57ee-406f-895a-3ceb3ac753eb" />
<br>
9)Manager Dashboard:
<img width="1919" height="932" alt="image" src="https://github.com/user-attachments/assets/b6d57f49-5571-4e78-9392-e734ee7f814b" />
<br>
10)Manager Actions(Reassigning):
<img width="940" height="470" alt="image" src="https://github.com/user-attachments/assets/229a7428-44e6-4502-a3cb-62cbec2ee2f3" />
<br>
11)Reports and Analytics:
<img width="1913" height="883" alt="image" src="https://github.com/user-attachments/assets/70d844bb-c9c7-4bde-881c-03c1d160289d" />
<br>
12)Admin Panel:
<img width="1919" height="905" alt="image" src="https://github.com/user-attachments/assets/9a0e07fc-de18-4020-a59b-3f6e8b4bcc7a" />
<br>













