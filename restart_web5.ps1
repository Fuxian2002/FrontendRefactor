# Kill existing process on port 7086
Write-Host "Stopping Web5 backend on port 7086..."
$process = Get-NetTCPConnection -LocalPort 7086 -ErrorAction SilentlyContinue | Get-Process -ErrorAction SilentlyContinue
if ($process) {
    Stop-Process -Id $process.Id -Force
    Write-Host "Process killed"
} else {
    Write-Host "No process running on 7086"
}

Start-Sleep -Seconds 2

# Start new JAR
Write-Host "Starting Web5 backend with latest JAR..."
cd "D:\前端重构\项目版本1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\前端重构\code\PF2-web5\web5-backend"

# Verify JAR exists and show its timestamp
Write-Host "JAR file info:"
Get-Item "target\PF2-web5-0.0.1-SNAPSHOT.jar" | Format-List LastWriteTime, Length

# Run the JAR
Write-Host "Launching JAR..."
java -jar target/PF2-web5-0.0.1-SNAPSHOT.jar
