# Cloud Storage Service

## Overview
Cloud Storage is a RESTful service that allows users to store, retrieve, and manage their files in the cloud. The application provides a secure interface for file uploads, downloads, and management through a REST API.

## Features
- User authentication with JWT tokens
- File upload and download
- File management (list, rename, delete)
- Secure storage of user files

## Technology Stack
- Java + Spring Boot
- Spring Security with JWT authentication
- PostgreSQL database
- Spring Data JPA

## Prerequisites
- JDK 17 or later
- PostgreSQL 14 or later
- Maven 3.8+

## Configuration
The application can be configured through the `application.properties` file:

```properties
server.port=8081
spring.application.name=CloudStorage
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=mypassword
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
file.storage.path=C/Dev/Filestorage
```

## API Endpoints

### Authentication
- `POST /login` - User login
- `POST /logout` - User logout
- `POST /sign-up` - User registration

### File Operations
- `POST /file` - Upload a file
- `GET /file` - Download a file
- `PUT /file` - Rename a file
- `DELETE /file` - Delete a file
- `GET /list` - Get list of files

All endpoints except `/login` and `/sign-up` require authentication with the `auth-token` header.

## Frontend Integration
This backend service can be integrated with the provided frontend application. Follow these steps:

1. Install Node.js (version 19.7.0 or higher) by following the [instructions](https://nodejs.org/en/download/current/).
2. Download the frontend application from the `/netology-diplom-frontend` directory.
3. Navigate to the frontend directory and run:
   ```
   npm install
   npm run serve
   ```
4. Configure the backend URL in the `.env` file of the frontend project:
   ```
   VUE_APP_BASE_URL=http://localhost:8081
   ```
5. Restart the frontend application if necessary.

## Authentication Flow
1. The client sends login credentials to the `/login` endpoint
2. The server validates credentials and returns a JWT token in the `auth-token` field
3. For subsequent requests, the client must include this token in the `auth-token` header
4. To logout, send a request to `/logout` with the token

## Running the Application
```
mvn spring-boot:run
```

The application will start on port 8081 by default.

## Security
- All passwords are encrypted using BCrypt
- JWT tokens are used for stateless authentication
- File access is restricted to the file owner
