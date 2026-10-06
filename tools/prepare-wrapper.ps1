# Downloads the official Gradle wrapper JAR, verifies it, and changes no system settings.
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$directory = Join-Path $root 'gradle\wrapper'
$destination = Join-Path $directory 'gradle-wrapper.jar'
$expected = '81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f'
$url = 'https://raw.githubusercontent.com/gradle/gradle/v8.13.0/gradle/wrapper/gradle-wrapper.jar'

if (Test-Path -LiteralPath $destination) {
    $actual = (Get-FileHash -LiteralPath $destination -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actual -ne $expected) {
        throw 'The existing Gradle wrapper checksum differs. Inspect the file before removing or replacing it.'
    }
    Write-Host 'Official Gradle wrapper already present and verified.'
    exit 0
}
New-Item -ItemType Directory -Force -Path $directory | Out-Null
$temp = Join-Path $directory ('wrapper-' + [Guid]::NewGuid().ToString('N') + '.tmp')
try {
    Write-Host 'Downloading the official Gradle wrapper...'
    Invoke-WebRequest -UseBasicParsing -Uri $url -OutFile $temp -TimeoutSec 120
    $actual = (Get-FileHash -LiteralPath $temp -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actual -ne $expected) { throw 'Gradle wrapper checksum verification failed. Nothing was installed.' }
    Move-Item -LiteralPath $temp -Destination $destination -Force
    Write-Host 'Ready. Open the ElanAndroid folder in Android Studio.'
} finally {
    if (Test-Path -LiteralPath $temp) { Remove-Item -LiteralPath $temp -Force }
}
