# StolotoAPI - Spring REST API with SSE

This is a Spring Boot REST API with Server-Sent Events (SSE) endpoint and JPA (H2 database) for Stoloto.

## Features

- RESTful CRUD operations for Room and Participant entities
- Room management with participant registration
- Participant balance management (add, subtract, block, unblock)
- SSE endpoint for real-time event streaming
- H2 in-memory database with console
- Spring Data JPA for ORM
- Lombok for reducing boilerplate

## Prerequisites

- Java 17+
- Maven 3.6+

## Build and Run

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Endpoints

### REST API (Rooms)

- `GET /api/rooms` - Get all rooms
- `GET /api/rooms/{id}` - Get room by ID
- `POST /api/rooms` - Create new room
- `PUT /api/rooms/{id}` - Update room
- `DELETE /api/rooms/{id}` - Delete room
- `POST /api/rooms/{roomId}/participants/{participantId}` - Add participant to room
- `DELETE /api/rooms/{roomId}/participants/{participantId}` - Remove participant from room
- `GET /api/rooms/with-boost` - Get rooms with boost
- `GET /api/rooms/upcoming` - Get upcoming rooms
- `GET /api/rooms/by-price/{maxPrice}` - Get rooms by maximum entry price
- `GET /api/rooms/by-prize/{minPrize}` - Get rooms by minimum prize fund

### REST API (Participants)

- `GET /api/participants` - Get all participants
- `GET /api/participants/{id}` - Get participant by ID
- `POST /api/participants` - Create new participant
- `PUT /api/participants/{id}` - Update participant
- `DELETE /api/participants/{id}` - Delete participant
- `POST /api/participants/{id}/balance/add` - Add balance to participant
- `POST /api/participants/{id}/balance/subtract` - Subtract balance from participant
- `POST /api/participants/{id}/balance/block` - Block participant balance
- `POST /api/participants/{id}/balance/unblock` - Unblock participant balance
- `GET /api/participants/with-min-balance/{minBalance}` - Get participants with minimum balance
- `GET /api/participants/with-blocked-balance` - Get participants with blocked balance

### REST API (Events) - Legacy

- `GET /api/events` - Get all events
- `GET /api/events/{id}` - Get event by ID
- `POST /api/events` - Create new event
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event

### SSE Endpoints

- `GET /api/sse/subscribe` - Subscribe to SSE events (Content-Type: text/event-stream)
- `GET /api/sse/trigger` - Manually trigger a test event

## Database Console

H2 database console is available at `http://localhost:8080/h2-console`

Connection settings:

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## Example Usage

### Create a room

```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -d '{"seatsCount": 10, "entryPrice": 100, "prizeFund": 1000, "hasBoost": true, "startTime": "2026-04-19T15:00:00"}'
```

### Create a participant

```bash
curl -X POST http://localhost:8080/api/participants \
  -H "Content-Type: application/json" \
  -d '{"balance": 500, "blockedBalance": 0}'
```

### Add participant to room

```bash
curl -X POST http://localhost:8080/api/rooms/1/participants/1
```

### Add balance to participant

```bash
curl -X POST http://localhost:8080/api/participants/1/balance/add \
  -H "Content-Type: application/json" \
  -d '{"amount": 200}'
```

### Subscribe to SSE events

```bash
curl -N http://localhost:8080/api/sse/subscribe
```

### Trigger test event

```bash
curl http://localhost:8080/api/sse/trigger
```

## Project Structure

```text
src/main/java/com/stoloto/api/
├── StolotoApiApplication.java   # Main application class
├── model/
│   ├── Event.java                 # JPA entity (legacy)
│   ├── Room.java                  # Room entity
│   └── Participant.java           # Participant entity
├── repository/
│   ├── EventRepository.java       # Spring Data JPA repository (legacy)
│   ├── RoomRepository.java        # Room repository
│   └── ParticipantRepository.java # Participant repository
├── service/
│   ├── EventService.java          # Business logic (legacy)
│   ├── RoomService.java           # Room management service
│   ├── ParticipantService.java    # Participant management service
│   └── SseEventService.java       # SSE emitter management
└── controller/
    ├── EventController.java       # REST API controller (legacy)
    ├── RoomController.java        # Room REST API controller
    ├── ParticipantController.java # Participant REST API controller
    └── SseController.java         # SSE endpoint controller
```

## Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- H2 Database
- Lombok
- Spring Boot Starter Test (for testing)
