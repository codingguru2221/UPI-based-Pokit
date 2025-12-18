@echo off
echo Starting UPI-Powered Smart Pocket Money Management System...

echo.
echo Step 1: Creating database if it doesn't exist...
echo.
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -pCodex@123 -e "CREATE DATABASE IF NOT EXISTS upi_pokit;"

echo.
echo Step 2: Starting Backend Server (Spring Boot)...
echo.
start "Backend Server" cmd /k "cd /d c:\Users\hp5cd\Desktop\Java advance Projects\UPI-based-Pokit\backend && mvn spring-boot:run"

timeout /t 15

echo.
echo Step 3: Starting Frontend Server (React Vite)...
echo.
start "Frontend Server" cmd /k "cd /d c:\Users\hp5cd\Desktop\Java advance Projects\UPI-based-Pokit\frontend && npm run dev"

echo.
echo System startup initiated!
echo Backend will be available at: http://localhost:8080
echo Frontend will be available at: http://localhost:5173
echo.
echo Note: Please wait for both servers to fully initialize.
echo Press any key to close this window...
pause >nul