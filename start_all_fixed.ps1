# ??????? start_all_fixed.ps1
$baseDir = Get-Location

# ????????????
$web1Backend = "$baseDir\PF2-web1\web1-backend"
$web2Backend = "$baseDir\PF2-web2\web2-backend"
$web5Backend = "$baseDir\PF2-web5\web5-backend"
$lsDir = "$baseDir\PF2-web1\pf-language-server"
$web1Frontend = "$baseDir\PF2-web1\PF2-web1-new"
$web2Frontend = "$baseDir\PF2-web2\web2-vue"
$web5Frontend = "$baseDir\PF2-web5\web5-vue"

# ???? Web1 Backend (Port 7084)
Write-Host "Starting Web1 Backend on Port 7084..."
Start-Process cmd -ArgumentList "/k cd /d ""$web1Backend"" && java -jar target/PF2-web1-0.0.1-SNAPSHOT.jar"

# ???? Web2 Backend (Port 7085)
Write-Host "Starting Web2 Backend on Port 7085..."
Start-Process cmd -ArgumentList "/k cd /d ""$web2Backend"" && java -jar target/web2-backend-0.0.1-SNAPSHOT.jar"

# ???? Web5 Backend (Port 7086)
Write-Host "Starting Web5 Backend on Port 7086..."
Start-Process cmd -ArgumentList "/k cd /d ""$web5Backend"" && java -jar target/web5-backend-0.0.1-SNAPSHOT.jar"

# ???? Language Server (Port 8030)
# ??????? NODE_OPTIONS ??????? OpenSSL ????????????
Write-Host "Starting Language Server on Port 8030..."
Start-Process cmd -ArgumentList "/k cd /d ""$lsDir"" && set NODE_OPTIONS=--openssl-legacy-provider && yarn start"

# ???????????????????????????????????
Start-Sleep -Seconds 5

# ???? Web1 Frontend
Write-Host "Starting Web1 Frontend..."
Start-Process cmd -ArgumentList "/k cd /d ""$web1Frontend"" && npm run dev"

# ???? Web2 Frontend
Write-Host "Starting Web2 Frontend..."
Start-Process cmd -ArgumentList "/k cd /d ""$web2Frontend"" && npm run dev"

# ???? Web5 Frontend
Write-Host "Starting Web5 Frontend..."
Start-Process cmd -ArgumentList "/k cd /d ""$web5Frontend"" && npm run dev"

Write-Host "All services launch commands issued."
