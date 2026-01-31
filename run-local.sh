#!/bin/bash
# Local Development Startup Script for Linux/Mac
# This script sets up environment variables and runs the Spring Boot application

echo ""
echo "================================"
echo " Satellite Backend - Local Test"
echo "================================"
echo ""

# Load environment variables from .env file
if [ -f .env ]; then
    echo "Loading environment variables from .env..."
    export $(cat .env | grep -v '^#' | xargs)
else
    echo "ERROR: .env file not found!"
    echo "Please create .env file with your database credentials."
    echo "You can copy .env.example as a template."
    exit 1
fi

echo ""
echo "Environment loaded:"
echo "  Database: $DB_NAME"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo ""

cd satellite

echo "Building application..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo ""
    echo "Build failed! Please check the errors above."
    exit 1
fi

echo ""
echo "Starting Spring Boot application..."
echo ""

java -jar target/satellite-backend-0.0.1-SNAPSHOT.jar

cd ..
