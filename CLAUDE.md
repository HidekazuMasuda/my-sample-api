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

## Testing Infrastructure

### Test Strategy
- **Integration Tests**: Full Controller-to-Database testing using Testcontainers
- **Database Isolation**: Each test runs with independent PostgreSQL containers
- **Parallel Execution**: Tests designed to run concurrently without interference
- **No External Dependencies**: Tests do not rely on existing database state

### Test Framework Components
- **Testcontainers**: Docker-based PostgreSQL containers for isolated testing
- **Spring Boot Test**: Web environment with random ports for API testing
- **TestRestTemplate**: HTTP client for endpoint testing
- **JUnit 5**: Test framework with parameterized and lifecycle management

### Test Configuration
- **Container Management**: Automatic PostgreSQL 16 container creation and cleanup
- **Schema Setup**: Dynamic schema creation using Hibernate DDL (create mode)
- **Data Management**: @BeforeEach setup ensures clean, predictable test data
- **Port Assignment**: Random port allocation prevents test conflicts

### Test Coverage
- **UserControllerIntegrationTest**: Complete /api/users endpoint testing
  - Database connectivity verification
  - JSON response structure validation
  - Data consistency between database and API responses
  - Test data isolation and cleanup

### Build Configuration
- **Gradle Test Logging**: Enhanced console output with detailed test results
- **Test Result Summary**: Formatted display showing passed/failed/skipped counts
- **Deprecation Warnings**: Resolved Gradle syntax issues for future compatibility
- **Build Validation**: Clean builds without warnings or deprecated syntax

## Development History

### Testing Infrastructure Development
1. **Initial Setup**: Basic Spring Boot application with PostgreSQL integration
2. **Test Framework Addition**: Implemented Testcontainers for database isolation
3. **Integration Testing**: Created comprehensive Controller-to-Database tests
4. **Test Independence**: Ensured tests run without relying on existing data
5. **Build Enhancement**: Added detailed test logging and result summaries
6. **Deprecation Resolution**: Fixed Gradle syntax warnings for maintainability

### Key Decisions
- **Testcontainers over H2**: Chose PostgreSQL containers for production parity
- **Test Data Management**: BeforeEach setup ensures consistent test environment
- **Gradle Configuration**: Enhanced test output for better development experience
- **Japanese Language Support**: Maintained UTF-8 compatibility throughout testing