@echo off
chcp 65001 > nul
echo ============================================================
echo   Smart Sports Tournament Management System
echo   Compile and Run Script
echo ============================================================
echo.

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile all Java files
echo [1/2] Compiling...
javac -d bin -sourcepath src src/Main.java src/sports/*.java src/player/*.java src/team/*.java src/match/*.java src/tournament/*.java src/exceptions/*.java src/admin/*.java src/filehandler/*.java

IF %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed! Please check your Java installation.
    pause
    exit /b 1
)

echo [SUCCESS] Compilation complete!
echo.
echo [2/2] Running program...
echo ============================================================
java -cp bin Main

pause
