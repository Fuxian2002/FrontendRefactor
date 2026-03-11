@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo.
echo ========== Web1 Debug Startup ==========
echo.

REM Kill process on port 7084
echo [1] Killing process on port 7084...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":7084 " ^| findstr "LISTENING"') do (
    echo Found process PID: %%a
    taskkill /PID %%a /F >nul 2>&1
    if !errorlevel! equ 0 (
        echo [OK] Process killed successfully
    ) else (
        echo [WARN] Could not kill process
    )
)

REM Wait for port to release
echo.
echo [2] Waiting 3 seconds for port to release...
timeout /t 3 /nobreak >nul

REM Start Web1 backend
echo.
echo [3] Starting Web1 backend with mvn clean spring-boot:run...
echo ========================================
echo.

mvn clean spring-boot:run

endlocal
pause
