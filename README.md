# Simple Booking System API

A beginner-friendly Spring Boot REST API for managing accommodation units and bookings. This version intentionally keeps the scope small for learning and interviews.

## Technology stack
- Java 21
- Spring Boot 3.5.8
- Spring Web (REST APIs)
- Spring Data JPA / Hibernate
- MySQL
- Bean Validation
- Gradle

No Docker, Redis, Liquibase, payment gateway, event system, scheduled jobs, or frontend is required.

## Features
- Create, view, update, delete accommodation units
- Search units by city
- Create and view bookings
- Search bookings by customer name
- Cancel a booking
- Prevent date ranges that overlap with an existing confirmed booking (safe under concurrent requests)
- Validate request data and return consistent JSON errors (`{"error": "..."}`) with proper status codes

## Requirements
- JDK 21
- MySQL 8 or newer
- Gradle (or add the Gradle wrapper from your local Gradle installation)

## Database setup
Create the database in MySQL:

```sql
CREATE DATABASE booking_system;
```

Set credentials as environment variables if needed (Spring does not read `.env` files automatically; export the variables in your shell or set them in your IDE run configuration):

```text
DB_URL=jdbc:mysql://localhost:3306/booking_system?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

Or edit `src/main/resources/application.properties`. Hibernate creates/updates tables on startup (`ddl-auto=update`), so no migration tool is needed for this learning project.

## Run

```bash
gradle bootRun
```

The API starts at `http://localhost:8080`.

## API endpoints

### Units
| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/units` | List all units |
| GET | `/api/units?city=Jaipur` | Filter units by city |
| GET | `/api/units/{id}` | Get a unit |
| POST | `/api/units` | Create a unit |
| PUT | `/api/units/{id}` | Update a unit |
| DELETE | `/api/units/{id}` | Delete a unit |

Example `POST /api/units`:

```json
{
  "name": "City View Apartment",
  "city": "Jaipur",
  "rooms": 2,
  "pricePerNight": 2500.00
}
```

### Bookings
| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/bookings` | List bookings |
| GET | `/api/bookings?customerName=Piyush` | Filter by customer name |
| GET | `/api/bookings/{id}` | Get a booking |
| POST | `/api/bookings` | Create a booking |
| PATCH | `/api/bookings/{id}/cancel` | Cancel a booking |

Example `POST /api/bookings`:

```json
{
  "unitId": 1,
  "customerName": "Piyush",
  "startDate": "2026-11-01",
  "endDate": "2026-11-04"
}
```

Dates use the format `YYYY-MM-DD`. The end date must be after the start date, and the start date cannot be in the past. A booking is rejected with `409 Conflict` when its dates overlap an existing confirmed booking for the same unit. Back-to-back stays are allowed (check-out day = next check-in day).

### Error responses
| Status | When |
|---|---|
| 400 | Validation failure, malformed JSON/date, bad path parameter, end date not after start date |
| 404 | Unit or booking not found |
| 409 | Overlapping booking, cancelling an already cancelled booking, deleting a unit that still has bookings |

## Simple architecture

`Controller -> Service -> Repository -> MySQL`

- **Controller:** receives HTTP requests and returns responses.
- **Service:** contains booking rules and business logic.
- **Repository:** uses Spring Data JPA to read and write data.
- **Entity:** represents a database table.

## Interview topics to prepare
1. REST API and HTTP methods/status codes
2. Dependency injection and `@RestController` / `@Service` / `@Repository`
3. JPA entities, repositories, and `@ManyToOne`
4. DTO/record for booking requests
5. Bean Validation
6. Date-overlap condition: `existing.startDate < requested.endDate` and `existing.endDate > requested.startDate`
7. Why service methods use `@Transactional`
8. Why cancelled bookings do not block future bookings
9. Concurrency: pessimistic locking on the unit row

## Concurrency
Booking creation first loads the unit with a pessimistic write lock (`SELECT ... FOR UPDATE`), so simultaneous requests for the same unit are processed one at a time and the overlap check cannot be bypassed by a race. Requests for different units do not block each other.

## Running tests
```bash
gradle test
```

## Scope note
This is an educational project. A production system would also need authentication/authorization, DTOs for responses instead of exposing entities, pagination, schema migrations (Flyway/Liquibase) instead of `ddl-auto=update`, and integration tests against a real database (e.g. Testcontainers).
