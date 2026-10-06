@echo off
setlocal
set "APP_HOME=%~dp0"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%APP_HOME%tools\prepare-wrapper.ps1"
if errorlevel 1 exit /b 1
set "JAVA_EXE=java.exe"
if defined JAVA_HOME set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if not defined JAVA_HOME if exist "%ProgramFiles%\Android\Android Studio\jbr\bin\java.exe" set "JAVA_EXE=%ProgramFiles%\Android\Android Studio\jbr\bin\java.exe"
"%JAVA_EXE%" -version >NUL 2>&1
if errorlevel 1 (
    echo JDK 17 or 21 is required. Set JAVA_HOME or use Android Studio.
    exit /b 1
)
"%JAVA_EXE%" "-Dorg.gradle.appname=gradlew" -classpath "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
exit /b %ERRORLEVEL%
