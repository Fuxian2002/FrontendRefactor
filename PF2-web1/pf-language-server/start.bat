@echo off
chcp 65001 >nul
echo ========================================
echo PF Language Server Startup
echo ========================================

echo [1/3] Starting TypeScript watch compilation...
start "TS Watch" cmd /c "npx tsc -b -w"

echo Waiting for initial compilation...
timeout /t 3 /nobreak >nul

echo [2/3] Starting language server from example/lib...
start "PF Language Server" cmd /c "node example/lib/startserver.js"

echo [3/3] Server process started!
echo ========================================
echo Language server is running!
echo - Watch window: compiling TypeScript
echo - Server window: example/lib/startserver.js
echo ========================================
echo.
echo To test the server, create a .pf file and see if it works
echo Press any key to close this window...
pause >nul