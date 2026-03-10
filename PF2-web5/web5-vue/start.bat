@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

set "PORT=5175"

echo ========================================
echo    Web5 Vue 前端启动工具
echo ========================================
echo.

:: 切换到脚本所在目录
cd /d "%~dp0"

:: 检查并清理端口占用
echo [INFO] 检查端口 %PORT% 是否被占用...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%PORT% "') do (
    if "%%a" neq "0" (
        echo [WARN] 端口 %PORT% 被 PID %%a 占用，正在关闭...
        taskkill /F /PID %%a >nul 2>&1
    )
)
timeout /t 1 >nul

:: 检查是否有 node_modules
if not exist "node_modules" (
    echo [INFO] 未检测到 node_modules，正在安装依赖...
    npm install
    if errorlevel 1 (
        echo [ERROR] 依赖安装失败！
        pause
        exit /b 1
    )
    echo [INFO] 依赖安装完成！
    echo.
)

echo [INFO] 启动 Vue 开发服务器（端口 %PORT%）...
echo [INFO] 启动成功后访问：http://localhost:%PORT%
echo [INFO] 按 Ctrl+C 停止服务
echo.

npm run dev

if errorlevel 1 (
    echo.
    echo [ERROR] 启动失败，请检查上方日志。
    pause
)

endlocal
