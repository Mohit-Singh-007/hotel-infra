# Hotel Booking System — Microservices
 
A production-ready microservices project focused on inter-service communication patterns including REST (Feign), Kafka, Redis caching, idempotency, and distributed transactions.
 
---
 
## Architecture
 
```
Client
  ↓
API Gateway (8080)         ← single entry point, routes to services
  ↓
┌─────────────────────────────────────────────┐
│  Eureka Server (8761)   ← service registry  │
└─────────────────────────────────────────────┘
  ↓
┌──────────────┐   ┌──────────────┐   ┌──────────────────────┐   ┌──────────────────────┐
│ User Service │   │ Hotel Service│   │   Booking Service    │   │ Notification Service │
│   (8081)     │   │   (8082)     │   │       (8083)         │   │       (8084)         │
│              │   │              │   │                      │   │                      │
│ - Users      │   │ - Hotels     │   │ - Bookings           │   │ - Kafka Consumer     │
│ - Roles      │   │ - Rooms      │   │ - Feign → User       │   │ - Notification Logs  │
│              │   │ - Redis Cache│   │ - Feign → Hotel      │   │                      │
└──────────────┘   └──────────────┘   │ - Kafka Producer     │   └──────────────────────┘
                                      │ - Idempotency        │
                                      │ - @Transactional     │
                                      └──────────────────────┘
```
 
---
 
## Inter-Service Communication
 
| Pattern | Services | Purpose |
|---------|----------|---------|
| Feign (sync REST) | booking → user | Validate user exists |
| Feign (sync REST) | booking → hotel | Check room availability |
| Feign (sync REST) | booking → hotel | Update room availability |
| Kafka (async) | booking → notification | booking.confirmed event |
| Kafka (async) | booking → notification | booking.cancelled event |
| Redis cache | hotel | Cache room availability |
| Redis idempotency | booking | Prevent duplicate bookings |
 
---
 
## Tech Stack
 
| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Service Discovery | Eureka (Spring Cloud) |
| Gateway | Spring Cloud Gateway |
| Sync Comm | OpenFeign |
| Async Comm | Apache Kafka |
| Caching | Redis (Lettuce) |
| Database | PostgreSQL |
| Messaging | RabbitMQ (infra only) |
| Containerization | Docker Compose |
 
---
 
## Services
 
### Eureka Server — `8761`
Central service registry. All services register here and discover each other by name.
 
### API Gateway — `8080`
Single entry point. Routes:
- `/users/**` → user-service
- `/hotels/**` → hotel-service
- `/bookings/**` → booking-service
### User Service — `8081`
Manages users. Exposes endpoints used by booking-service via Feign.
 
**Endpoints:**
```
POST   /users/register
GET    /users/{id}
GET    /users/email/{email}
```
 
### Hotel Service — `8082`
Manages hotels and rooms. Caches room availability in Redis.
 
**Endpoints:**
```
POST   /hotels
GET    /hotels/{id}
GET    /hotels/location/{location}
POST   /hotels/rooms
GET    /hotels/rooms/{roomId}/availability    ← cached in Redis
PUT    /hotels/rooms/{roomId}/availability    ← evicts cache
```
 
**Redis Caching:**
- `@Cacheable` on `checkAvailability` — first call hits DB, subsequent calls served from Redis (TTL: 10 min)
- `@CacheEvict` on `updateAvailability` — evicts cache when room status changes
### Booking Service — `8083`
Core service. Orchestrates the entire booking flow.
 
**Endpoints:**
```
POST   /bookings                 ← supports Idempotency-Key header
GET    /bookings/{id}
GET    /bookings/user/{userId}
PUT    /bookings/{id}/cancel
```
 
**Booking Flow:**
```
POST /bookings
  1. Validate user via Feign → user-service
  2. Check room availability via Feign → hotel-service (Redis cached)
  3. Calculate total price
  4. Save booking (PENDING)
  5. Update room availability via Feign → hotel-service
  6. Confirm booking (CONFIRMED)
  7. Publish booking.confirmed → Kafka (after transaction commits)
```
 
**Patterns Implemented:**
- `@Transactional` — atomic DB operations, rolls back on Feign failure
- `@TransactionalEventListener(AFTER_COMMIT)` — Kafka published only after DB commits
- Idempotency via Redis — duplicate requests return same result
### Notification Service — `8084`
Pure Kafka consumer. Listens to booking events and stores notification logs.
 
**Kafka Topics Consumed:**
- `booking.confirmed`
- `booking.cancelled`
---
 
## Infrastructure (Docker Compose)
 
```yaml
services:
  redis       → localhost:6379
  kafka       → localhost:9092
  zookeeper   → localhost:2181
  rabbitmq    → localhost:5672  (dashboard: localhost:15672)
```
 
**Start infrastructure:**
```bash
docker-compose up -d
```
 
**Verify:**
```bash
docker exec -it hotel-redis redis-cli ping     # → PONG
docker exec -it hotel-kafka kafka-topics --bootstrap-server localhost:9092 --list
```
 
---
 
## Running Locally
 
**Start in this order:**
 
```bash
# 1. Infrastructure
docker-compose up -d
 
# 2. Services (in order)
eureka-server     → port 8761
user-service      → port 8081
hotel-service     → port 8082
booking-service   → port 8083
notification-service → port 8084
api-gateway       → port 8080
```
 
**Verify Eureka:** `http://localhost:8761`
 
---
 
## Testing the Flow
 
```bash
# 1. Register user
POST http://localhost:8080/users/register
{
  "name": "John",
  "email": "john@example.com",
  "password": "123456"
}
 
# 2. Create hotel
POST http://localhost:8080/hotels
{
  "name": "Grand Hyatt",
  "location": "Mumbai",
  "description": "Luxury hotel",
  "rating": 4.5
}
 
# 3. Create room
POST http://localhost:8080/hotels/rooms
{
  "hotelId": 1,
  "roomNumber": "101",
  "type": "DOUBLE",
  "pricePerNight": 5000.0,
  "maxOccupancy": 2
}
 
# 4. Create booking (with idempotency)
POST http://localhost:8080/bookings
Idempotency-Key: booking-unique-001
{
  "userId": 1,
  "roomId": 1,
  "hotelId": 1,
  "checkIn": "2026-07-01",
  "checkOut": "2026-07-05"
}
 
# 5. Verify availability is now false
GET http://localhost:8080/hotels/rooms/1/availability
 
# 6. Cancel booking
PUT http://localhost:8080/bookings/1/cancel
 
# 7. Verify availability restored
GET http://localhost:8080/hotels/rooms/1/availability
```
 
**Test idempotency:** Send request 4 twice with the same `Idempotency-Key` — only one booking created.
 
**Test Redis caching:** Hit availability endpoint twice — second call returns in ~20ms (no SQL in logs).
 
---
 
## Key Patterns Learned
 
### 1. Feign Client (Sync)
```java
@FeignClient(name = "hotel-service", path = "/hotels")
public interface HotelClient {
    @GetMapping("/rooms/{roomId}/availability")
    AvailabilityRes checkAvailability(@PathVariable Long roomId);
}
```
 
### 2. Redis Caching (Declarative)
```java
@Cacheable(value = "availability", key = "#roomId")
public AvailabilityRes checkAvailability(Long roomId) { ... }
 
@CacheEvict(value = "availability", key = "#roomId")
public void updateAvailability(Long roomId, boolean available) { ... }
```
 
### 3. Transactional + Event Publishing
```java
@Transactional
public BookingRes createBooking(BookingReq req) {
    // DB operations
    eventPublisher.publishEvent(new BookingConfirmedEvent(saved));
}
 
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onBookingConfirmed(BookingConfirmedEvent event) {
    kafkaProducer.publish(event); // fires only after DB commits
}
```
 
### 4. Idempotency
```java
// Controller
POST /bookings
Header: Idempotency-Key: <uuid>
 
// Service checks Redis before processing
// Stores result in Redis after processing
// Returns cached result on duplicate request
```
 
### 5. Kafka Producer with Callbacks
```java
kafkaTemplate.send(topic, key, event)
    .whenComplete((result, ex) -> {
        if (ex != null) log.error("Failed: {}", ex.getMessage());
        else log.info("Published to partition {}", result.getRecordMetadata().partition());
    });
```
 
---
 
## Database Schema
 
Each service has its own tables in the shared `hotel-infra` PostgreSQL database (in production these would be separate DBs):
 
```
users              → user-service
hotels, rooms      → hotel-service
bookings           → booking-service
notification_logs  → notification-service
```
 
---
 
## What's Not Included (Next Steps)
 
- Circuit breaker (Resilience4j) on Feign calls
- JWT authentication via gateway
- Saga pattern for payment
- Distributed tracing (Zipkin/Micrometer)
- Separate DB per service
- Docker images for services
 
