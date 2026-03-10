#!/bin/bash

# Kill existing process on port 7086
echo "Stopping Web5 backend on port 7086..."
lsof -i :7086 | grep -v COMMAND | awk '{print $2}' | xargs kill -9 2>/dev/null || echo "No process running on 7086"

sleep 2

# Start new JAR
echo "Starting Web5 backend with latest JAR..."
cd "D:/前端重构/项目版本1.0/https---github.com-Fuxian2002-StoreHouseMurasame-main/前端重构/code/PF2-web5/web5-backend"

# Verify JAR exists and show its timestamp
ls -lh target/PF2-web5-0.0.1-SNAPSHOT.jar

# Run the JAR
java -jar target/PF2-web5-0.0.1-SNAPSHOT.jar

