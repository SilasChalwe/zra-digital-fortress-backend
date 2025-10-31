#!/bin/bash

PORT=8080
APP_DIR=~/zra-digital-fortress-backend
APP_CMD="./mvnw spring-boot:run"

# Check if port is in use
PID=$(sudo lsof -t -i:$PORT)

if [ -n "$PID" ]; then
    echo "🔴 Spring Boot app is running on port $PORT (PID: $PID). Stopping it..."
    sudo kill -9 $PID
    echo "✅ Process terminated."
else
    echo "🟢 No Spring Boot app running on port $PORT. Starting it..."
    cd "$APP_DIR" || exit
    nohup $APP_CMD > springboot.log 2>&1 &
    echo "✅ Application started in background. Logs -> springboot.log"
fi
