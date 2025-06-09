# Sample API

Spring Boot REST API with PostgreSQL integration.

## Setup

1. Copy `.env.example` to `.env` and set your database credentials:
```bash
cp .env.example .env
```

2. Edit `.env` with your database configuration:
```
DATABASE_URL=jdbc:postgresql://localhost:5432/sampledb
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_actual_password
SPRING_PROFILES_ACTIVE=dev
```

3. Run the application:
```bash
export $(cat .env | xargs) && ./gradlew bootRun
```

## Endpoints

- `GET /hello` - Returns "Hello World" JSON
- `GET /api/users` - Returns user list from database

## Development

The `dev` profile automatically:
- Creates tables from `schema.sql`
- Loads test data from `data-dev.sql`