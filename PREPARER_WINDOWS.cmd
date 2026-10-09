@echo off
setlocal
cd /d "%~dp0"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\prepare-wrapper.ps1"
set "RESULT=%ERRORLEVEL%"
if not "%RESULT%"=="0" echo Preparation failed. Read the error above; do not disable checksum verification.
pause
exit /b %RESULT%

