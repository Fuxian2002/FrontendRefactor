# PowerShell Script to convert .vue, .ts, .js files from GBK to UTF-8
# Usage: Run this script in the directory you want to scan.

$extensions = @("*.vue", "*.ts", "*.js", "*.java")
$currentDir = Get-Location
Write-Host "Scanning directory: $currentDir"

# Get all matching files recursively
$files = Get-ChildItem -Path . -Recurse -Include $extensions

# Define encodings
$gbk = [System.Text.Encoding]::GetEncoding(936)
$utf8NoBom = New-Object System.Text.UTF8Encoding $false

foreach ($file in $files) {
    # Skip directories (Get-ChildItem -Include can sometimes return directories)
    if ($file.PSIsContainer) { continue }
    
    # Skip node_modules and target directories to save time and avoid issues
    if ($file.FullName -match "node_modules" -or $file.FullName -match "\\target\\") { continue }

    try {
        # Read content as GBK
        # Note: This blindly assumes the file IS GBK. If it is already UTF-8, this might corrupt non-ASCII characters.
        # However, since you requested this specific conversion, we proceed.
        $content = [System.IO.File]::ReadAllText($file.FullName, $gbk)

        # Write content as UTF-8 (No BOM)
        [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
        
        Write-Host "Converted: $($file.Name)" -ForegroundColor Green
    }
    catch {
        Write-Host "Error processing $($file.Name): $_" -ForegroundColor Red
    }
}

Write-Host "Conversion completed." -ForegroundColor Cyan
