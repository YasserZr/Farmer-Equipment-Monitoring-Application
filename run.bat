@echo off
REM Windows batch script to run the application

echo ================================================
echo Starting Farmer Equipment Monitoring Application
echo ================================================

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker is not running. Please start Docker and try again.
    exit /b 1
)

REM Parse command line arguments
set REBUILD=false
set DETACHED=true

:parse_args
if "%1"=="--rebuild" (
    set REBUILD=true
    shift
    goto parse_args
)
if "%1"=="--foreground" (
    set DETACHED=false
    shift
    goto parse_args
)

REM Rebuild if requested
if "%REBUILD%"=="true" (
    echo.
    echo Rebuilding Docker images...
    call build.bat
    if errorlevel 1 (
        echo Build failed. Exiting.
        exit /b 1
    )
)

REM Stop any running containers
echo.
echo Stopping any running containers...
docker-compose down

REM Start services
echo.
echo Starting services...
if "%DETACHED%"=="true" (
    docker-compose up -d
) else (
    docker-compose up
)

if "%DETACHED%"=="true" (
    echo.
    echo ================================================
    echo Application Started Successfully!
    echo ================================================

    echo.
    echo Service URLs:
    echo Frontend:            http://localhost:3000
    echo API Gateway:         http://localhost:8080
    echo Eureka Server:       http://localhost:8761
    echo Config Server:       http://localhost:8888
    echo RabbitMQ Management: http://localhost:15672 (guest/guest)
    echo Farmers Service:     http://localhost:8081
    echo Equipment Service:   http://localhost:8082
    echo Supervision Service: http://localhost:8083

    echo.
    echo Database Connections:
    echo Farmers DB:     localhost:5432
    echo Equipment DB:   localhost:5433
    echo Supervision DB: localhost:5434

    echo.
    echo Useful Commands:
    echo View logs:        docker-compose logs -f [service-name]
    echo Stop services:    docker-compose down
    echo Restart service:  docker-compose restart [service-name]
    echo View status:      docker-compose ps

    echo.
    echo Checking service health...
    timeout /t 5 /nobreak >nul
    docker-compose ps
)

pause
