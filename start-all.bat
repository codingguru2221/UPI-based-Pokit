@echo off
setlocal enabledelayedexpansion

echo ========================================
echo UPI Pocket Money Application Startup Script
echo ========================================

REM Kill any existing processes on port 8080
echo Checking for processes on port 8080...
set process_found=0
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    echo Killing process %%a on port 8080
    taskkill /f /pid %%a 2>nul
    set process_found=1
)
if !process_found! equ 0 (
    echo No processes found on port 8080
)

echo.
echo Starting UPI Pocket Money Application...

REM Start the backend in a new window
start "Backend - UPI Pocket Money" /D "backend" cmd /k "mvn spring-boot:run"

REM Wait a moment for backend to start
echo Waiting for backend to start (15 seconds)...
timeout /t 15 /nobreak >nul

REM Start the frontend in a new window
start "Frontend - UPI Pocket Money" /D "frontend" cmd /k "npm run dev"

echo.
echo ========================================
echo APPLICATION STARTUP COMPLETE
echo ========================================
echo Backend API:     http://localhost:8080
echo Frontend App:    http://localhost:5173
echo.
echo IMPORTANT: Do NOT access the backend directly!
echo - The backend (port 8080) returns HTTP 403 because of Spring Security
echo - Always use the frontend (port 5173) to access the application
echo - The frontend communicates with the backend through API calls
echo.
echo If you see a blank page:
echo  1. Check browser console for errors (F12)
echo  2. Ensure both backend and frontend started successfully
echo  3. Refresh the frontend page
echo  4. Try accessing http://localhost:5173/login directly
echo.
echo Close this window when finished (applications will continue running)
echo ========================================
pause >nul
exit