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
