@echo off
setlocal
cd /d "%~dp0"

set "GSON_VERSION=2.14.0"
set "GSON_JAR=lib\gson-%GSON_VERSION%.jar"

if not exist "lib" mkdir "lib"

if not exist "%GSON_JAR%" (
    echo Gson JSON library was not found. Downloading it now...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "try { Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/com/google/code/gson/gson/%GSON_VERSION%/gson-%GSON_VERSION%.jar' -OutFile '%GSON_JAR%' -UseBasicParsing } catch { Write-Error $_; exit 1 }"
    if errorlevel 1 (
        echo.
        echo Could not download Gson. Check your internet connection and try again.
        echo You can also open the project in VS Code after installing Java support.
        pause
        exit /b 1
    )
)

echo Compiling Weather App...
if not exist "out" mkdir "out"
javac -encoding UTF-8 -cp "%GSON_JAR%" -d out src\main\java\Main.java
if errorlevel 1 (
    echo.
    echo Compilation failed. Check that a JDK is installed and javac works.
    pause
    exit /b 1
)

echo Starting Weather App...
java -cp "out;%GSON_JAR%" Main
if errorlevel 1 (
    echo.
    echo The app exited with an error.
    pause
)
endlocal
