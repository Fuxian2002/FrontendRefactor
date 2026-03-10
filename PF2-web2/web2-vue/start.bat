@echo off
chcp 65001 > nul
echo ========================================
echo    Vue 前端项目启动工具
echo ========================================
echo.

REM 检查是否有 node_modules
if not exist "node_modules" (
    echo 检测到 node_modules 不存在，正在安装依赖...
    echo 这可能需要几分钟时间，请稍候...
    echo.
    npm install
    if errorlevel 1 (
        echo.
        echo ❌ 依赖安装失败！
        pause
        exit /b 1
    )
    echo ✅ 依赖安装完成！
    echo.
)

echo 正在启动 Vue 开发服务器...
echo.
echo 🔧 使用技术栈：Vue 3 + TypeScript + Vite
echo 📦 项目信息：
echo   - 入口文件：index.html
echo   - 配置文件：vite.config.ts
echo.
echo ⏳ 启动中，请稍候...
echo.
echo ========================================
echo 启动成功后，你将看到：
echo   - 本地访问地址：http://localhost:5173
echo   - 按 Ctrl+C 可以停止服务
echo ========================================
echo.

npm run dev

pause