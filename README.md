# real-retro-api

The REST layer for **REAL RETRO** — a thin, stateless Quarkus-native service
that wraps the [`fin-model`](https://github.com/marcushowarth/fin-model) `rpi`
module. It serves the ONS RPI CHAW series, aggregated to one index value per
calendar year, so the front end can rebase any historical price/value series
into real terms.

No database, no session, no dataset persistence — datasets and data points
live client-side in the UI's localStorage. Paired with the
[React front end](https://github.com/marcushowarth/real-retro-ui).

## Stack

- **Quarkus 3.33.1 LTS** on **JDK 25**, compiled to a **native image** (GraalVM/Mandrel, ~20 MiB)
- `fin-model-rpi` (consumed from GitHub Packages) for the RPI data + adjustment math

## Endpoints

### `GET /api/rpi`

Returns the full annual series:

```json
[
  { "year": 1987, "index": 100.0 },
  { "year": 1988, "index": 104.9 }
]
```

### `GET /api/rpi/latest-year`

```json
{ "year": 2026 }
```

### `GET /api/version`

Returns `{ "version", "gitSha", "builtAt" }` — the UI footer reads this.

### `GET /q/health`

Quarkus's built-in liveness/readiness check.

## Run

Requires **JDK 25** and `fin-model` available (from GitHub Packages or
`mvn install`d locally).

```bash
mvn quarkus:dev -Dquarkus.http.port=8090    # dev mode, live reload, http://localhost:8090
```

Native build:

```bash
mvn package -Dnative -Dquarkus.native.container-build=true \
  -Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-25
./target/real-retro-api-*-runner
```

CORS is open in dev so the Vite front end (`localhost:5173`) can call the API
directly; the front end also proxies `/api` to `:8090`.

## Deploy

GitHub Actions builds the native image, pushes it to ECR, and restarts the
container on EC2 (`127.0.0.1:8084`). Caddy fronts it at
[real-retro.howarth.eu](https://real-retro.howarth.eu), serving the UI as
static files and reverse-proxying `/api` to this service — same origin, no
CORS needed in production. See `deploy/README.md` for the one-time setup
checklist (ECR repo, GH secrets, EC2 dir, Caddy snippet).

## Privacy

No database, no session, no request-body logging.

## License

[MIT](LICENSE)
