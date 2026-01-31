@echo off
REM Local Development Startup Script for Windows
REM This script sets up environment variables and runs the Spring Boot application

echo.
echo ================================
echo  Satellite Backend - Local Test
echo ================================
echo.

REM Load environment variables from .env file
if exist .env (
    echo Loading environment variables from .env...
    for /f "tokens=1,2 delims==" %%a in (.env) do (
        if not "%%a"=="" if not "%%b"=="" (
            set %%a=%%b
        )
    )
) else (
    echo ERROR: .env file not found!
    echo Please create .env file with your database credentials.
    echo You can copy .env.example as a template.
    exit /b 1
)

echo.
echo Environment loaded:
echo   Database: %DB_NAME%
echo   Host: %DB_HOST%
echo   Port: %DB_PORT%
echo.

cd satellite

echo Building application...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed! Please check the errors above.
    exit /b 1
)

echo.
echo Starting Spring Boot application...
echo.

java -jar target\satellite-backend-0.0.1-SNAPSHOT.jar

cd ..
