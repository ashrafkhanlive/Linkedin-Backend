# Large-Scale Report Generation Service

Asynchronous report generation service built with Spring Boot 3.5 / Java 21, using
Kafka for decoupled processing, Redis for request deduplication/caching, and
PostgreSQL for durable state.

## Architecture

```
Client
  │
  ▼
ReportController          POST /api/reports, GET /api/reports/{id}
  │
  ▼
ReportService
  ├── Check Redis Cache  (dedupe identical requests)
  ├── Check DB           (fallback dedupe on cache miss)
  ├── Save Report (PENDING)
  └── Publish Kafka Event
        │
        ▼
   Kafka Topic (report-generation-topic)
        │
        ▼
   KafkaConsumer
        │
        ▼
   FileGenerationService  → generates CSV / Excel / PDF
        │
        ▼
   FileStorageService     → local disk (dev) or S3 (prod)
        │
        ▼
   Update Report row (COMPLETED / FAILED) + evict cache
```

Failed messages retry 3× with backoff, then route to `report-generation-topic.DLT`
via Spring Kafka's `DeadLetterPublishingRecoverer`.

## Tech stack

- Java 21 (virtual threads for `@Async` and the embedded Tomcat connector)
- Spring Boot 3.5, Spring Data JPA, Spring Kafka, Spring Data Redis
- PostgreSQL, Redis, Apache Kafka
- Apache POI (Excel), Apache PDFBox (PDF), OpenCSV (CSV)
- AWS SDK v2 (optional S3 storage backend)
- springdoc-openapi (Swagger UI), Actuator + Micrometer/Prometheus
- Structured JSON logging (logstash-logback-encoder)
- Lombok

## Running locally

1. Start infrastructure:
   ```bash
   cd docker
   docker compose up -d
   ```

2. Run the app:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Submit a report:
   ```bash
   curl -X POST http://localhost:8080/api/reports \
     -H "Content-Type: application/json" \
     -d '{
           "reportType": "PDF",
           "requestedBy": "alice@example.com",
           "parameters": { "region": "APAC", "year": 2026 }
         }'
   ```
   Returns `202 Accepted` with the report `id` and `status: PENDING`.

4. Poll for status:
   ```bash
   curl http://localhost:8080/api/reports/{id}
   ```
   Once `status` is `COMPLETED`, `downloadUrl` points to the generated file
   (served from `./generated-reports` by default in local storage mode).

5. Swagger UI: http://localhost:8080/swagger-ui.html
   Actuator health: http://localhost:8080/actuator/health
   Kafka UI (from docker-compose): http://localhost:8090

## Switching to S3 storage

Set in `application.yml`:
```yaml
report:
  storage:
    mode: s3
    s3:
      bucket: my-reports-bucket
      region: us-east-1
```
AWS credentials are resolved via the default provider chain (env vars, instance
profile, `~/.aws/credentials`) — nothing is hardcoded in the app.

## Design notes

- **Caching**: the cache key is a SHA-256 hash of `reportType + requestedBy + parameters`.
  Identical concurrent requests short-circuit to the existing PENDING/PROCESSING/COMPLETED
  report instead of triggering duplicate generation.
- **Ordering**: Kafka messages are keyed by `reportId` so retries for the same report
  stay on one partition.
- **At-least-once processing**: the consumer uses manual ack, only acknowledging after
  the DB row and cache are updated — a crash mid-processing results in redelivery, not
  a silently lost report.
- **Backpressure**: consumer concurrency (3 by default) and Hikari pool size are both
  tunable via `application.yml` for scaling out consumers horizontally with partitions.

## Project layout

```
src/main/java/com/example/report/
├── config/       Kafka, Redis, Async (virtual threads) configuration
├── controller/   REST API
├── service/      Business logic: orchestration, caching, file generation
├── kafka/        Producer / consumer
├── repository/   Spring Data JPA
├── entity/       JPA entities + enums
├── dto/          Request/response/event payloads
├── exception/    Custom exceptions + global handler
└── util/         Storage abstraction (local disk / S3)
```
