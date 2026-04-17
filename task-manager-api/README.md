# Task Manager API (Spring Boot + AWS)

## Overview

This project is a production-style backend application built using Spring Boot. It is a multi-user task management system where users can securely register, log in, and manage their personal tasks. The application is designed to demonstrate real-world backend development practices along with cloud deployment, authentication, and DevOps workflows.

The system uses JWT-based authentication, is deployed on AWS EC2, connects to an AWS RDS database, includes CI/CD via GitHub Actions, and integrates logging with CloudWatch. Swagger UI is used as the primary interface to interact with and test the APIs.

---

## Features

* User registration and login
* JWT-based authentication (stateless)
* Secure API endpoints
* Create, read, update, delete tasks
* Task completion toggle
* User-specific task isolation
* Request validation
* Global exception handling
* Swagger UI for API testing
* AWS deployment (EC2 + RDS)
* CI/CD using GitHub Actions
* Logging and monitoring with CloudWatch

---

## Tech Stack

Backend:

* Java 17
* Spring Boot
* Spring Web
* Spring Security
* JWT (JSON Web Token)
* Spring Data JPA (Hibernate)
* Validation API

Database:

* MySQL or PostgreSQL (AWS RDS)

Infrastructure:

* AWS EC2 (application hosting)
* AWS RDS (database)
* Nginx (reverse proxy)

DevOps:

* Git & GitHub
* GitHub Actions (CI/CD)

Monitoring:

* AWS CloudWatch

Documentation / Interface:

* Swagger / OpenAPI

---

## Architecture

Client (Swagger UI / Postman)
→ Nginx (EC2)
→ Spring Boot Application
→ AWS RDS Database

Logs:
Spring Boot / Nginx → CloudWatch

Deployment:
GitHub → GitHub Actions → EC2

---

## API Endpoints

### Authentication

POST /auth/signup
Register a new user

POST /auth/login
Authenticate user and return JWT token

---

### Tasks (Protected - Requires JWT)

GET /tasks
Get all tasks for the authenticated user

GET /tasks/{id}
Get a specific task by ID

POST /tasks
Create a new task

PUT /tasks/{id}
Update an existing task

DELETE /tasks/{id}
Delete a task

PATCH /tasks/{id}/complete
Mark task as completed or toggle status

---

## Authentication Flow

1. User registers using /auth/signup
2. User logs in using /auth/login
3. Server returns a JWT token
4. Client sends token in header:

Authorization: Bearer <token>

5. Backend validates token for each request
6. Access is granted only if token is valid

---

## Data Model

User:

* id
* name / username
* email
* password (hashed)
* createdAt
* updatedAt

Task:

* id
* title
* description
* status
* completed flag
* priority (optional)
* dueDate (optional)
* createdAt
* updatedAt
* user_id (foreign key)

Relationship:
One user → many tasks

---

## Local Setup Instructions

1. Clone the repository
2. Open project in IDE
3. Configure database in application.properties or application.yml
4. Run the Spring Boot application
5. Access Swagger UI:

http://localhost:8080/swagger-ui/index.html

---

## AWS Deployment

EC2:

* Hosts Spring Boot application (JAR)
* Runs behind Nginx

RDS:

* Stores application data
* Connected securely to EC2

Nginx:

* Routes HTTP traffic to application port

---

## CI/CD Pipeline

* Triggered on push to main branch
* Builds project (Maven/Gradle)
* Creates JAR file
* Connects to EC2 via SSH
* Deploys new version
* Restarts application

---

## Logging & Monitoring

* Application logs collected from Spring Boot
* Nginx logs included
* Logs pushed to CloudWatch
* Used for debugging and monitoring system behavior

---

## Environment Variables (Example)

* DB_URL
* DB_USERNAME
* DB_PASSWORD
* JWT_SECRET
* JWT_EXPIRATION
* SERVER_PORT

Sensitive values should not be hardcoded.

---

## Testing the API

Primary interface:
Swagger UI

Alternative:
Postman

Steps:

1. Register user
2. Login
3. Copy JWT token
4. Authorize in Swagger
5. Test protected endpoints

---

## Future Improvements

* Refresh tokens
* Role-based authorization
* Task filtering and search
* File uploads using S3
* Pagination
* Rate limiting
* Docker containerization
* HTTPS setup

---

## Summary

This project demonstrates:

* Backend development with Spring Boot
* Secure authentication with JWT
* Database integration using JPA and RDS
* Cloud deployment on AWS
* CI/CD automation
* Logging and monitoring
* Clean architecture and API design

It is designed to reflect a real-world backend system rather than a basic demo project.
