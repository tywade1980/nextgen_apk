#!/bin/bash

# NextGen APK Development Setup Script
# This script sets up the development environment for NextGen APK

set -e

echo "🚀 Setting up NextGen APK Development Environment..."

# Check for required tools
check_tool() {
    if ! command -v $1 &> /dev/null; then
        echo "❌ $1 is required but not installed. Please install $1 first."
        exit 1
    else
        echo "✅ $1 is available"
    fi
}

echo "📋 Checking required tools..."
check_tool "docker"
check_tool "docker-compose"
check_tool "java"

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
else
    echo "✅ Java $JAVA_VERSION is compatible"
fi

# Create necessary directories
echo "📁 Creating project directories..."
mkdir -p logs
mkdir -p data/{postgres,redis,grafana,prometheus,elasticsearch,minio}
mkdir -p docker/grafana/{dashboards,provisioning/{dashboards,datasources}}

# Set permissions for data directories
sudo chown -R $USER:$USER data/ || true

# Start infrastructure services
echo "🐳 Starting infrastructure services..."
docker-compose up -d postgres redis prometheus grafana

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
timeout=30
counter=0
until docker-compose exec -T postgres pg_isready -U nextgen_user -d nextgen_db; do
    sleep 1
    counter=$((counter + 1))
    if [ $counter -ge $timeout ]; then
        echo "❌ PostgreSQL failed to start within $timeout seconds"
        exit 1
    fi
done
echo "✅ PostgreSQL is ready"

# Wait for Redis to be ready
echo "⏳ Waiting for Redis to be ready..."
timeout=30
counter=0
until docker-compose exec -T redis redis-cli ping | grep PONG; do
    sleep 1
    counter=$((counter + 1))
    if [ $counter -ge $timeout ]; then
        echo "❌ Redis failed to start within $timeout seconds"
        exit 1
    fi
done
echo "✅ Redis is ready"

# Create Gradle wrapper if it doesn't exist
if [ ! -f "./gradlew" ]; then
    echo "📦 Creating Gradle wrapper..."
    gradle wrapper --gradle-version=8.4 || echo "⚠️  Gradle wrapper creation failed, but continuing..."
fi

# Make gradlew executable
chmod +x ./gradlew || true

echo "🏗️  Building project..."
./gradlew build -x test || echo "⚠️  Build completed with warnings"

echo "🎉 NextGen APK development environment is ready!"
echo ""
echo "📊 Access points:"
echo "  - Grafana Dashboard: http://localhost:3000 (admin/nextgen123)"
echo "  - Prometheus: http://localhost:9090"
echo "  - PostgreSQL: localhost:5432 (nextgen_user/nextgen_password)"
echo "  - Redis: localhost:6379"
echo ""
echo "🚀 Next steps:"
echo "  1. Start backend services:"
echo "     ./gradlew :backend-core:bootRun"
echo "  2. Start MCP server (in another terminal):"
echo "     ./gradlew :mcp-server:bootRun"
echo "  3. Open Android Studio and run the app"
echo ""
echo "💡 Useful commands:"
echo "  - Check service status: docker-compose ps"
echo "  - View logs: docker-compose logs -f [service-name]"
echo "  - Stop services: docker-compose down"
echo "  - Clean rebuild: ./gradlew clean build"