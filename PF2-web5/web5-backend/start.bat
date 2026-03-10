@echo off
chcp 65001 > nul

echo.
echo ===============================================
echo     Web5 Backend Starter
echo ===============================================
echo.

REM Kill any existing process on port 7086
echo [1] Checking for existing process on port 7086...
for /f "tokens=5" %%a in ('netstat -ano ^| find "7086"') do (
    echo Found PID: %%a - Killing process...
    taskkill /pid %%a /f >nul 2>&1
)

timeout /t 2 /nobreak

REM Check if target directory and JAR exist
if not exist "target\PF2-web5-0.0.1-SNAPSHOT.jar" (
    echo.
    echo [2] JAR file not found! Building project...
    call mvn clean package -DskipTests -q
    if errorlevel 1 (
        echo ERROR: Build failed!
        pause
        exit /b 1
    )
    echo [2] Build completed successfully
) else (
    echo [2] JAR file found: target\PF2-web5-0.0.1-SNAPSHOT.jar
)

REM Get JAR file info
for %%F in (target\PF2-web5-0.0.1-SNAPSHOT.jar) do (
    echo     Size: %%~zF bytes
    echo     Modified: %%~tF
)

echo.
echo [3] Starting Web5 backend on port 7086...
echo ===============================================
echo.

REM Run the JAR
java -jar target\PF2-web5-0.0.1-SNAPSHOT.jar

echo.
echo Backend stopped.
pause