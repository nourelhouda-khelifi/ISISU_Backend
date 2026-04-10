#!/bin/bash
# Build script for Render deployment

echo "Building ISISU backend..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "Build successful!"
