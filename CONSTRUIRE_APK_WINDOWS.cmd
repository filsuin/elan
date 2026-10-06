@echo off
setlocal
cd /d "%~dp0"
call gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
set "RESULT=%ERRORLEVEL%"
if "%RESULT%"=="0" (
    echo.
    echo APK: %CD%\app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo Build failed. Read the errors above. Open and sync the project in Android Studio first.
)
pause
exit /b %RESULT%
