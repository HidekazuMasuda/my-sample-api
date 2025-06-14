# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Setup and Configuration
- Environment setup: `cp .env.example .env` then edit with database credentials
- Run application: `export $(cat .env | xargs) && ./gradlew bootRun`
- Build project: `./gradlew build`
- Run tests: `./gradlew test`

### Database
- PostgreSQL required (connection configured via .env file)
- Schema auto-created from `src/main/resources/schema.sql`
- Dev profile loads sample data from `src/main/resources/data-dev.sql`

## Architecture Overview

### Spring Boot Application Structure
- **Main Application**: `com.example.demo.DemoApplication` - Standard Spring Boot entry point
- **Security**: Permissive configuration with CORS enabled, CSRF disabled for API usage
- **Database**: PostgreSQL with JPA/Hibernate, manual schema management via SQL files
- **API Documentation**: OpenAPI/Swagger available (springdoc-openapi-starter-webmvc-ui)

### Layer Organization
- **Controllers** (`controller/`): REST API endpoints with OpenAPI annotations
- **Entities** (`entity/`): JPA entities with Swagger schema documentation
- **Repositories** (`repository/`): Spring Data JPA repositories

### Configuration Profiles
- **Default**: Base PostgreSQL configuration with environment variable substitution
- **Dev Profile**: Automatic SQL initialization enabled, loads schema and test data
- Database credentials sourced from environment variables (DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)

### Current API Endpoints
- `GET /hello` - Simple hello world endpoint
- `GET /api/users` - Returns all users from database

### Key Technical Details
- Java 21 with Spring Boot 3.5.0
- PostgreSQL driver with JPA/Hibernate
- Manual DDL management (ddl-auto=none) using schema.sql
- Japanese language used in entity documentation and sample data