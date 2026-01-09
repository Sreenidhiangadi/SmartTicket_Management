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
<br>
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
<img width="1919" height="965" alt="image" src="https://github.com/user-attachments/assets/00934ebb-8932-4d42-bfdf-3ea1d586fb2a" />
<br>
2)User Register(Successful):
<img width="1919" height="963" alt="image" src="https://github.com/user-attachments/assets/cc0b4a19-2456-4a6d-a5b6-0e3e5ae9d6f1" />
<br>
3)User login(Validations):
<img width="1919" height="966" alt="image" src="https://github.com/user-attachments/assets/e03d06cf-9eb1-42cb-9592-85518b24f5a9" />
<br>
4)User Login (Successful):
<img width="1912" height="976" alt="image" src="https://github.com/user-attachments/assets/ede08cbe-1e09-4116-87f3-c6b953ae7372" />
<br>
5)Home Page:
<img width="1916" height="964" alt="image" src="https://github.com/user-attachments/assets/68ed5189-ce2c-49f9-a9e6-2a917ca939d9" />
<br>
6)User raise ticket:
<img width="1919" height="966" alt="image" src="https://github.com/user-attachments/assets/51f38055-ea75-4056-94ad-ededbc034954" />
<br>
7)Ticket details:
<img width="1919" height="972" alt="image" src="https://github.com/user-attachments/assets/51f56163-3e77-43f6-9dfb-c50ce720f62d" />
<br>
8)Agent Queue:
<img width="1910" height="776" alt="image" src="https://github.com/user-attachments/assets/929f0228-c392-404e-bca9-bdbd9d4b76d9" />
<br>
9)Manager Dashboard:
<img width="1919" height="932" alt="image" src="https://github.com/user-attachments/assets/b6d57f49-5571-4e78-9392-e734ee7f814b" />
<br>
10)Manager Actions(Reassigning):
<img width="1912" height="963" alt="image" src="https://github.com/user-attachments/assets/ad027859-6948-4cfb-bca5-42cd7eab1f63" />
<br>
11)Reports and Analytics:
<img width="1913" height="883" alt="image" src="https://github.com/user-attachments/assets/70d844bb-c9c7-4bde-881c-03c1d160289d" />
<br>
12)Admin Panel:
<img width="1919" height="905" alt="image" src="https://github.com/user-attachments/assets/9a0e07fc-de18-4020-a59b-3f6e8b4bcc7a" />
<br>













