#!/bin/bash

# Get absolute path to script directory
BASE_DIR=$(cd "$(dirname "$0")" && pwd)

# Start Backend
echo "Starting Backend (Port 7084)..."
cd "$BASE_DIR/web1-backend"
mvn spring-boot:run -Dserver.port=7084 -Dmaven.test.skip=true &
BACKEND_PID=$!

# Start Language Server
echo "Starting Language Server (Port 8030)..."
cd "$BASE_DIR/pf-language-server"
export NODE_OPTIONS=--openssl-legacy-provider
yarn start &
LS_PID=$!

# Start Frontend
echo "Starting Frontend..."
cd "$BASE_DIR/PF2-web1-new"
yarn dev &
FRONTEND_PID=$!

echo "All services started."
echo "Backend PID: $BACKEND_PID"
echo "Language Server PID: $LS_PID"
echo "Frontend PID: $FRONTEND_PID"

# Wait for all background processes
wait
