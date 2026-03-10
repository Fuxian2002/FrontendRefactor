# PowerShell Script to convert .java files back from UTF-8 to GBK (reverse the damage)
# Usage: Run this script in the directory you want to scan.

$extensions = @("*.java")
$currentDir = Get-Location
Write-Host "Scanning directory: $currentDir"

# Get all matching files recursively
$files = Get-ChildItem -Path . -Recurse -Include $extensions

# Define encodings
$utf8 = [System.Text.Encoding]::UTF8
$gbk = [System.Text.Encoding]::GetEncoding(936)

foreach ($file in $files) {
    # Skip directories
    if ($file.PSIsContainer) { continue }

    # Skip node_modules and target directories
    if ($file.FullName -match "node_modules" -or $file.FullName -match "\\target\\") { continue }

    try {
        # Read content as UTF-8 (the damaged encoding)
        $content = [System.IO.File]::ReadAllText($file.FullName, $utf8)

        # Write content as GBK (restore original)
        [System.IO.File]::WriteAllText($file.FullName, $content, $gbk)

        Write-Host "Reverted: $($file.Name)" -ForegroundColor Green
    }
    catch {
        Write-Host "Error processing $($file.Name): $_" -ForegroundColor Red
    }
}

Write-Host "Reversion completed." -ForegroundColor Cyan
