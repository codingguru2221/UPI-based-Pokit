Write-Host "Starting UPI-Powered Smart Pocket Money Management System..." -ForegroundColor Green

Write-Host "`nStep 1: Creating database if it doesn't exist..." -ForegroundColor Yellow
try {
    & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -pCodex@123 -e "CREATE DATABASE IF NOT EXISTS upi_pokit;"
    Write-Host "Database setup completed." -ForegroundColor Green
} catch {
    Write-Host "Warning: Could not create database. It may already exist or MySQL is not accessible." -ForegroundColor Yellow
}

Write-Host "`nStep 2: Starting Backend Server (Spring Boot)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location 'c:\Users\hp5cd\Desktop\Java advance Projects\UPI-based-Pokit\backend'; mvn spring-boot:run" -WindowStyle Normal

Write-Host "Waiting 15 seconds for backend to initialize..." -ForegroundColor Cyan
Start-Sleep -Seconds 15

Write-Host "`nStep 3: Starting Frontend Server (React Vite)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location 'c:\Users\hp5cd\Desktop\Java advance Projects\UPI-based-Pokit\frontend'; npm run dev" -WindowStyle Normal

Write-Host "`nSystem startup initiated!" -ForegroundColor Green
Write-Host "Backend will be available at: http://localhost:8080" -ForegroundColor Cyan
Write-Host "Frontend will be available at: http://localhost:5173" -ForegroundColor Cyan
Write-Host "`nNote: Please wait for both servers to fully initialize." -ForegroundColor Yellow
Write-Host "Press any key to close this window..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")