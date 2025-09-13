# NextGen APK Development Instructions

NextGen APK is a comprehensive, multi-module voice-driven integration platform combining Android application development with backend microservices, database integration, and AI/ML capabilities. This repository contains a complete full-stack system with Docker infrastructure.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

- **CRITICAL Environment Setup**:
  - Use Gradle 8.4 (NOT the system default Gradle 9.0.0)
  - Download and use Gradle 8.4: `wget https://services.gradle.org/distributions/gradle-8.4-bin.zip`
  - Extract to `/tmp/gradle-8.4` and use `export GRADLE_HOME=/tmp/gradle-8.4 && export PATH=$GRADLE_HOME/bin:$PATH`
  - Java 17 is available at `/usr/bin/java`
  - Docker Compose is available as `docker compose` (NOT `docker-compose`)
  - Android SDK is available at `/usr/local/lib/android/sdk`

- **Always set these environment variables before working**:
  ```bash
  export GRADLE_HOME=/tmp/gradle-8.4
  export PATH=$GRADLE_HOME/bin:$PATH
  export ANDROID_HOME=/usr/local/lib/android/sdk
  export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/build-tools/35.0.0
  ```

- **Infrastructure Setup and Build Workflow**:
  - **NEVER CANCEL**: Infrastructure setup takes 7-10 seconds. Build tasks take 3-60 seconds. Set timeouts to 120+ seconds minimum.
  - Start infrastructure: `docker compose up -d postgres redis` -- takes ~7 seconds
  - Stop infrastructure: `docker compose down` -- takes ~0.5 seconds
  - Service status check: `docker compose ps`
  - **CRITICAL**: Full project build has compilation errors in some modules (normal for active development codebase)
  - Individual module builds work fine and are the recommended approach

- **Build Commands with Validated Timings**:
  - Download Gradle 8.4: ~1-2 seconds
  - Project configuration: `gradle tasks` -- takes ~52 seconds (NEVER CANCEL)
  - Shared module build: `gradle shared:build -x test` -- takes ~4.5 seconds  
  - Backend service build: `gradle backend-core:build -x test` -- takes ~10 seconds
  - Backend service startup: `gradle backend-core:bootRun` -- takes ~4 seconds to start
  - Tests (shared): `gradle shared:test` -- takes ~3.6 seconds
  - Android resource compilation: `aapt2 compile --dir app/src/main/res -o /tmp/compiled_res.zip` -- takes ~0.03 seconds
  - Docker infrastructure: `docker compose up -d postgres redis` -- takes ~7 seconds

- **Available Modules and Status**:
  - **app/**: Android application (Jetpack Compose + Kotlin) - builds with Android SDK
  - **shared/**: Common utilities and constants - BUILDS SUCCESSFULLY
  - **backend-core/**: Spring Boot backend service - BUILDS AND RUNS SUCCESSFULLY  
  - **voice-engine/**: Voice processing service - partial build (depends on shared module Android refs)
  - **database-layer/**: Database abstraction layer - has dependency resolution issues
  - **mcp-server/**: Model Context Protocol server - has compilation errors
  - **integration-hub/**: Cross-app integration - builds with warnings

## Testing and Validation

- **Unit Testing**: 
  - Shared module: `gradle shared:test` -- takes ~3.6 seconds, 5 tests pass
  - Backend: `gradle backend-core:test` -- takes ~1.6 seconds (cached)
  - Test infrastructure is working and validated
- **Resource Validation**: `aapt2 compile --dir app/src/main/res -o /tmp/compiled_res.zip` validates Android resources
- **Service Validation**: Start backend with `gradle backend-core:bootRun` and verify startup logs
- **Infrastructure Validation**: Check services with `docker compose ps`

## Environment Capabilities and Limitations

- **Full Docker Infrastructure**: PostgreSQL with pgvector, Redis, Prometheus, Grafana, MinIO, Elasticsearch
- **Spring Boot Services**: Can build and run backend microservices successfully
- **Android Development**: Resource compilation works, full APK build requires additional Android configuration
- **Build System**: Gradle 8.4 works perfectly, Gradle 9.0.0 has compatibility issues
- **Network Access**: Limited - dependency downloads may fail, use offline-first approaches
- **Testing**: Unit test infrastructure is working and validated

## Development Commands

**Setup Gradle 8.4 (Required)**:
```bash
cd /tmp && wget https://services.gradle.org/distributions/gradle-8.4-bin.zip
unzip -q gradle-8.4-bin.zip
export GRADLE_HOME=/tmp/gradle-8.4
export PATH=$GRADLE_HOME/bin:$PATH
gradle --version
```

**Start Infrastructure Services (7 seconds)**:
```bash
docker compose up -d postgres redis
docker compose ps
```

**Build Individual Modules (Recommended)**:
```bash
# Shared module (4.5 seconds)
gradle shared:build -x test

# Backend service (10 seconds)  
gradle backend-core:build -x test

# Run tests (3.6 seconds)
gradle shared:test
```

**Run Backend Service (4 seconds startup)**:
```bash
# Start backend (runs on port 8081)
gradle backend-core:bootRun
# Verify at http://localhost:8081/actuator/health
```

**Android Resource Validation (0.03 seconds)**:
```bash
export ANDROID_HOME=/usr/local/lib/android/sdk
export PATH=$PATH:$ANDROID_HOME/build-tools/35.0.0
aapt2 compile --dir app/src/main/res -o /tmp/compiled_res.zip
```

**Docker Infrastructure Management**:
```bash
# Check service status
docker compose ps

# View logs  
docker compose logs -f postgres

# Stop services (0.5 seconds)
docker compose down
```

## Project Structure

This is a comprehensive multi-module project:
```
/
├── README.md                    # Comprehensive project documentation  
├── docker-compose.yml           # Full infrastructure stack
├── build.gradle.kts             # Root build configuration
├── settings.gradle.kts          # Module configuration  
├── setup-dev.sh                 # Development setup script (needs docker-compose fix)
├── app/                         # Android application (Jetpack Compose)
│   ├── build.gradle.kts         # Android app build configuration
│   └── src/main/                # Android source code
├── shared/                      # Common utilities - BUILDS SUCCESSFULLY
│   ├── src/main/kotlin/         # Shared data models and constants  
│   └── src/test/kotlin/         # Unit tests (5 tests pass)
├── backend-core/                # Spring Boot backend - BUILDS AND RUNS
│   ├── src/main/kotlin/         # REST APIs, WebSocket, database integration
│   └── src/main/resources/      # Application configuration
├── voice-engine/                # Voice command processing
├── database-layer/              # PostgreSQL with pgvector integration  
├── mcp-server/                  # Model Context Protocol server
├── integration-hub/             # Cross-application integration
└── docker/                     # Docker configuration and scripts
```

## Common Tasks

**Initialize Development Environment**:
1. Download Gradle 8.4: `wget https://services.gradle.org/distributions/gradle-8.4-bin.zip`
2. Extract and set PATH: `export GRADLE_HOME=/tmp/gradle-8.4 && export PATH=$GRADLE_HOME/bin:$PATH`
3. Start infrastructure: `docker compose up -d postgres redis` (7 seconds)
4. Build shared module: `gradle shared:build -x test` (4.5 seconds)
5. Test backend: `gradle backend-core:bootRun` (starts in 4 seconds)

**Validate Changes**:
1. Always build individual modules rather than full project build
2. Test shared module: `gradle shared:test` (3.6 seconds, 5 tests)
3. Validate Android resources: `aapt2 compile --dir app/src/main/res -o /tmp/compiled_res.zip` (0.03 seconds)
4. Check Docker services: `docker compose ps`
5. Verify backend health: visit http://localhost:8081/actuator/health

**Build and Test Individual Modules**:
- Shared: `gradle shared:build shared:test` 
- Backend: `gradle backend-core:build backend-core:test`
- Android resources: `aapt2 compile --dir app/src/main/res -o /tmp/compiled_res.zip`

## Important Notes

- **NEVER CANCEL**: All commands complete quickly. Max timeout needed is 120 seconds for initial setup.
- **Use Individual Module Builds**: Full project build fails due to compilation errors in some modules
- **Gradle 8.4 Required**: System Gradle 9.0.0 has compatibility issues with the build scripts
- **Infrastructure Works**: PostgreSQL, Redis, and full Docker stack operational  
- **Spring Boot Services**: Backend services build and run successfully with database connectivity
- **Android Development**: Resource compilation works, full Android builds may need additional configuration
- **Testing Validated**: Unit test infrastructure is working with 5 passing tests in shared module

## Measured Timing Results

| Task | Time | Status |
|------|------|---------|
| Download Gradle 8.4 | 1-2 seconds | ✅ Working |
| Docker infrastructure startup | ~7 seconds | ✅ Working |
| Project configuration (gradle tasks) | ~52 seconds | ✅ Working |
| Shared module build | ~4.5 seconds | ✅ Working |
| Backend-core build | ~10 seconds | ✅ Working |
| Backend service startup | ~4 seconds | ✅ Working |
| Shared module tests | ~3.6 seconds | ✅ Working (5 tests pass) |
| Android resource compilation | ~0.03 seconds | ✅ Working |
| Docker services shutdown | ~0.5 seconds | ✅ Working |

## Validation Scenarios

After making changes, always run these validation steps:

1. **Basic Module Build**: `gradle shared:build -x test` - should complete in ~4.5 seconds
2. **Test Execution**: `gradle shared:test` - should pass 5 tests in ~3.6 seconds  
3. **Backend Service**: `gradle backend-core:bootRun` - should start in ~4 seconds with PostgreSQL connection
4. **Infrastructure Health**: `docker compose ps` - should show postgres and redis running
5. **Android Resources**: `aapt2 compile --dir app/src/main/res -o /tmp/compiled_res.zip` - should complete in ~0.03 seconds

## Frequently Used Command Outputs

### Project Structure
```bash
$ ls -la /home/runner/work/nextgen_apk/nextgen_apk
total 100
drwxr-xr-x 13 runner runner  4096 Sep 13 12:39 .
-rw-rw-r--  1 runner runner 12206 Sep 13 12:39 README.md
drwxrwxr-x  3 runner runner  4096 Sep 13 12:39 app
drwxrwxr-x  3 runner runner  4096 Sep 13 12:39 backend-core
-rw-rw-r--  1 runner runner   857 Sep 13 12:39 build.gradle.kts
drwxrwxr-x  3 runner runner  4096 Sep 13 12:39 database-layer
-rw-rw-r--  1 runner runner  4228 Sep 13 12:39 docker-compose.yml
-rwxrwxr-x  1 runner runner  4974 Sep 13 12:39 gradlew
drwxrwxr-x  3 runner runner  4096 Sep 13 12:39 integration-hub
drwxrwxr-x  3 runner runner  4096 Sep 13 12:39 mcp-server
-rwxrwxr-x  1 runner runner  3230 Sep 13 12:39 setup-dev.sh
drwxrwxr-x  3 runner runner  4096 Sep 13 12:39 shared  
drwxrwxr-x  3 runner runner  4096 Sep 13 12:39 voice-engine
```

### Docker Services Status
```bash
$ docker compose ps
NAME               IMAGE                    COMMAND                  SERVICE    CREATED         STATUS                   PORTS
nextgen-postgres   pgvector/pgvector:pg15   "docker-entrypoint.s…"   postgres   7 seconds ago   Up 5 seconds (healthy)   0.0.0.0:5432->5432/tcp
nextgen-redis      redis:7-alpine           "docker-entrypoint.s…"   redis      7 seconds ago   Up 7 seconds             0.0.0.0:6379->6379/tcp
```

### Available Gradle Tasks
- `backend-core:bootRun` - Run Spring Boot backend service
- `mcp-server:bootRun` - Run MCP server service  
- `shared:build` - Build shared utilities module
- `shared:test` - Run shared module tests
- `backend-core:build` - Build backend service
- Android resource compilation with `aapt2`