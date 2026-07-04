# Deploy

REAL RETRO uses a **dual** deploy on the existing EC2, fronted by Caddy:

- **API** (this repo) → native container, bound to `127.0.0.1:8084`, serves `/api/*` + `/q/health`.
- **UI** (`real-retro-ui`) → static build rsync'd to `/srv/retro-ui`, served by Caddy.
- Both under `real-retro.howarth.eu` → same-origin, no CORS in production.

`deploy.yml` runs on push to `main`: native build (+ native IT) → ECR → SSH deploy.

## One-time prerequisites

1. **ECR repo** — `aws ecr create-repository --repository-name real-retro --region eu-west-2` (not yet created — needs `marcus-cli`).
2. **GitHub Actions secrets/variables — required on this repo** (repo-level; no Environment gating). **Values are not in this repo** — they live in private ops notes.

   | Name | Kind |
   |---|---|
   | `AWS_ACCESS_KEY_ID` | secret |
   | `AWS_SECRET_ACCESS_KEY` | secret |
   | `EC2_HOST` | secret |
   | `EC2_SSH_KEY` | secret |
   | `PACKAGES_TOKEN` | secret (`read:packages` — native build pulls `fin-model` from GitHub Packages) — **already added** |
   | `AWS_ACCOUNT_ID` | **variable** (read as `${{ vars.AWS_ACCOUNT_ID }}`) |

   (`real-retro-ui` needs only `EC2_HOST` + `EC2_SSH_KEY` for its rsync deploy.)
3. **On the EC2:**
   - `sudo mkdir -p /srv/retro-ui && sudo chown ec2-user /srv/retro-ui`
   - Add `deploy/Caddyfile-retro.snippet` to the Caddyfile, reload Caddy.
4. **DNS** — `real-retro.howarth.eu` A record → the EC2 — **done** (ANAME added 2026-07-04).

## Ports on the EC2
`8080` kanban-mcp · `8081` mediawiki-mcp · `8082` fin-optics-api · `8083` vaultwarden · **`8084` real-retro-api** (this).

## Build versions
`GET /api/version` → `{version, gitSha, builtAt}`. The deploy injects `APP_GIT_SHA`
(commit) and `APP_BUILT_AT` (ISO timestamp) as env; the UI footer reads this endpoint.
