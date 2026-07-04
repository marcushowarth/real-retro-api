#!/usr/bin/env bash
#
# Local dev orchestration for the REAL RETRO stack.
#
#   fin-model  →  real-retro-api  →  real-retro-ui
#
# Builds + installs the model into ~/.m2 (so the API picks up local model
# changes, not a stale SNAPSHOT), frees any stale listeners on the dev ports,
# then runs the API (Quarkus dev) and UI (Vite) together. Ctrl-C stops both.
#
# Assumes the three repos are siblings under the same parent directory.
set -euo pipefail

# GraalVM 25 keeps local builds deterministic (matches the native build chain).
export JAVA_HOME="${JAVA_HOME:-/Library/Java/JavaVirtualMachines/graalvm-25.jdk/Contents/Home}"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"   # parent of real-retro-api
MODEL="$ROOT/fin-model"
API="$ROOT/real-retro-api"
UI="$ROOT/real-retro-ui"

API_PORT=8090
UI_PORT=5173

API_PID=""
UI_PID=""

free_port() {   # $1=port $2=label
  local pids
  pids=$(lsof -nP -iTCP:"$1" -sTCP:LISTEN -t 2>/dev/null || true)
  if [ -n "$pids" ]; then
    echo "→ freeing :$1 ($2) — killing stale $(echo "$pids" | tr '\n' ' ')"
    # shellcheck disable=SC2086
    kill $pids 2>/dev/null || true
    sleep 1
  fi
}

cleanup() {
  echo
  echo "→ stopping stack..."
  [ -n "$API_PID" ] && kill "$API_PID" 2>/dev/null || true
  [ -n "$UI_PID" ]  && kill "$UI_PID"  2>/dev/null || true
}
trap cleanup EXIT INT TERM

echo "→ [1/3] building fin-model → ~/.m2"
mvn -q -f "$MODEL/pom.xml" install -DskipTests

free_port "$API_PORT" api
free_port "$UI_PORT" ui

echo "→ [2/3] starting real-retro-api (Quarkus dev) on :$API_PORT"
( cd "$API" && exec mvn -q quarkus:dev -Dquarkus.http.port="$API_PORT" ) &
API_PID=$!

echo "→ [3/3] starting real-retro-ui (Vite) on :$UI_PORT"
( cd "$UI" && exec npm run dev -- --port "$UI_PORT" --strictPort ) &
UI_PID=$!

cat <<EOF

stack up:
  API  → http://localhost:$API_PORT   (health: /q/health · version: /api/version)
  UI   → http://localhost:$UI_PORT   (proxies /api → :$API_PORT)
  Ctrl-C to stop both.
EOF

wait
