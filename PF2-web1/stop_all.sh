#!/bin/bash

echo "Stopping all services..."

# Function to kill process on a specific port
kill_process_on_port() {
    local port=$1
    local service_name=$2
    
    # Find PID using lsof. -t returns only PID.
    local pid=$(lsof -t -i:$port)
    
    if [ -n "$pid" ]; then
        echo "Stopping $service_name on port $port (PID: $pid)..."
        # Kill the process
        kill -9 $pid
        echo "$service_name stopped."
    else
        echo "$service_name is not running on port $port."
    fi
}

# Stop Backend (Port 7084)
kill_process_on_port 7084 "Backend"

# Stop Language Server (Port 8030)
kill_process_on_port 8030 "Language Server"

# Stop Frontend (Port 5173)
kill_process_on_port 5173 "Frontend"

echo "All services stopped."
