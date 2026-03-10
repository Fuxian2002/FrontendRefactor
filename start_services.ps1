Set-Location "D:\??????\??????1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\code\PF2-web1\web1-backend"
Start-Process "java" -ArgumentList "-jar target/PF2-web1-0.0.1-SNAPSHOT.jar"

Set-Location "D:\??????\??????1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\code\PF2-web1\PF2-web1-new"
Start-Process "node" -ArgumentList "node_modules/vite/bin/vite.js"

Set-Location "D:\??????\??????1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\code\PF2-web2\web2-backend"
Start-Process "mvn.cmd" -ArgumentList "spring-boot:run"

Set-Location "D:\??????\??????1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\code\PF2-web2\web2-frontend"
Start-Process "npm.cmd" -ArgumentList "start"

Set-Location "D:\??????\??????1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\code\PF2-web5\web5-backend"
Start-Process "mvn.cmd" -ArgumentList "spring-boot:run"

Set-Location "D:\??????\??????1.0\https---github.com-Fuxian2002-StoreHouseMurasame-main\??????\code\PF2-web5\web5-frontend"
Start-Process "npm.cmd" -ArgumentList "start"
