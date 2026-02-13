# Scalable Chat AI (Spring WebFlux + Redis)

This service is a horizontally scalable chat backend designed for high concurrency:

- Non-blocking API layer (`Spring WebFlux`)
- Stateless app nodes (safe to run multiple replicas)
- Redis-backed conversation history and distributed rate limiting
- SSE token streaming endpoint for chat UX
- Pluggable LLM provider (`mock` or OpenAI-compatible API)

## Why this scales

- App instances do not keep chat state in memory.
- Shared Redis handles session history + global request throttling.
- Reactive request handling supports high concurrent connections with fewer threads.
- You can add replicas behind a load balancer without sticky sessions.

## Run locally

### Option 1: Docker Compose

```bash
docker compose up --build
```

### Option 2: Maven + local Redis

1. Start Redis on `localhost:6379`.
2. Run:

```bash
mvn spring-boot:run
```

Service URL: `http://localhost:8080`

## API

### Chat completion

`POST /api/v1/chat`

Headers:
- `Content-Type: application/json`
- `X-Client-Id: client-123` (required for rate limiting identity)

Body:

```json
{
  "sessionId": "s1",
  "message": "Explain event-driven architecture"
}
```

### Streaming chat (SSE)

`POST /api/v1/chat/stream`

Same headers/body as above.

Produces `text/event-stream` with:
- `event: token` for incremental chunks
- `event: done` when stream completes

## Environment variables

- `PORT` (default: `8080`)
- `REDIS_HOST` (default: `localhost`)
- `REDIS_PORT` (default: `6379`)
- `CHAT_HISTORY_LIMIT` (default: `30`)
- `CHAT_PER_MINUTE_LIMIT` (default: `60`)
- `LLM_PROVIDER` (`mock` or `openai-compatible`, default: `mock`)
- `LLM_BASE_URL` (default: `https://api.openai.com/v1`)
- `LLM_MODEL` (default: `gpt-4o-mini`)
- `LLM_API_KEY` (required when not using `mock`)
- `LLM_TIMEOUT_SECONDS` (default: `20`)

## Production scaling notes

1. Put app replicas behind a load balancer or API gateway.
2. Use managed Redis with persistence and replica failover.
3. Add autoscaling based on CPU + p95 latency + queue depth.
4. Add centralized observability (logs, metrics, distributed tracing).
5. Add per-tenant quotas and auth (JWT/API key) before public exposure.
