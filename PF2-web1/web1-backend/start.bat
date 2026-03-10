@echo off
setlocal enabledelayedexpansion

:: Configuration
set "PORT=7084"
set "JAR_PATH=target\PF2-web1-0.0.1-SNAPSHOT.jar"
set "JAVA_OPTS=-Xmx512m -Xms256m"

:: Ensure we are in the correct directory
cd /d "%~dp0"
echo [INFO] Working directory: %CD%

echo [INFO] Checking port %PORT%...


:: Find and kill process occupying the port
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%PORT%') do (
    if "%%a" neq "0" (
        echo [WARN] Port %PORT% is occupied by PID %%a, attempting to kill...
        taskkill /F /PID %%a >nul 2>&1
    )
)

:: Wait for process release
timeout /t 1 >nul

:: Check if jar exists
if not exist "%JAR_PATH%" (
    echo [ERROR] File not found: %JAR_PATH%
    echo [HINT] Please ensure you are in web1-backend directory and have run maven build.
    pause
    exit /b 1
)

:: Start backend
echo [INFO] Starting web1-backend...
echo [INFO] Command: java %JAVA_OPTS% -jar %JAR_PATH%
echo.

java %JAVA_OPTS% -jar %JAR_PATH%

if errorlevel 1 (
    echo.
    echo [ERROR] Start failed, please check logs above.
    pause
)

endlocal
