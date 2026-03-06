@echo off
setlocal enabledelayedexpansion
:: setup.bat -- Student onboarding script for TaskFlow (Windows)
:: Run this once after cloning the repository.
::
:: What it does:
::   1. Checks that Java 21+ and Maven 3.x are available on PATH
::   2. Runs an initial build (compile) to confirm the project builds
::   3. Runs the full test suite to show your starting baseline
::   4. Prints a getting-started summary
::
:: Usage (from project root, Command Prompt or PowerShell):
::   setup.bat

set "REPO_ROOT=%~dp0"
:: Trim trailing backslash
if "%REPO_ROOT:~-1%"=="\" set "REPO_ROOT=%REPO_ROOT:~0,-1%"

echo.
echo ======================================================================
echo   TaskFlow -- Setup
echo ======================================================================
echo.

:: -------------------------------------------------------------------------
:: 1. Check Java
:: -------------------------------------------------------------------------
echo [INFO]  Checking Java...
where java >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java not found on PATH.
    echo         Download and install Java 21 from:
    echo           https://adoptium.net/temurin/releases/?version=21
    echo         Then re-run this script.
    goto :fail
)

:: Read major version from "java -version" output  (goes to stderr)
for /f "tokens=3 delims= " %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set "JAVA_VERSION_STR=%%~v"
)
:: Version string is like  21.0.3  or  1.8.0_xxx
for /f "tokens=1 delims=." %%m in ("!JAVA_VERSION_STR!") do set "JAVA_MAJOR=%%m"
:: Older Java reported "1.8" so major=1 means Java 8
if "!JAVA_MAJOR!"=="1" (
    for /f "tokens=2 delims=." %%m in ("!JAVA_VERSION_STR!") do set "JAVA_MAJOR=%%m"
)

if !JAVA_MAJOR! LSS 21 (
    echo [WARN]  Java !JAVA_MAJOR! found -- this project requires Java 21+.
    echo         Download Java 21 from: https://adoptium.net/temurin/releases/?version=21
    goto :fail
)
echo [OK]    Java !JAVA_MAJOR! found.

:: -------------------------------------------------------------------------
:: 2. Check Maven
:: -------------------------------------------------------------------------
echo [INFO]  Checking Maven...
where mvn >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven not found on PATH.
    echo         Option A -- Install Maven manually:
    echo           https://maven.apache.org/download.cgi
    echo         Option B -- Use the Maven Wrapper bundled with the project:
    echo           Replace  "mvn"  with  "mvnw.cmd"  in every command below.
    goto :fail
)
for /f "tokens=3 delims= " %%v in ('mvn --version 2^>^&1 ^| findstr "Apache Maven"') do (
    echo [OK]    Maven %%v found.
    goto :maven_ok
)
:maven_ok

:: -------------------------------------------------------------------------
:: 3. Initial compile
:: -------------------------------------------------------------------------
echo.
echo [INFO]  Running initial compile...
cd /d "%REPO_ROOT%"
call mvn compile -q
if errorlevel 1 (
    echo [WARN]  Compilation had errors.
    echo         Some bugs in this project may prevent a clean compile.
    echo         That is expected -- fixing those bugs is part of the exercise.
) else (
    echo [OK]    Project compiled successfully.
)

:: -------------------------------------------------------------------------
:: 4. Run full test suite (failures expected -- do not exit on failure)
:: -------------------------------------------------------------------------
echo.
echo [INFO]  Running test suite to show your starting baseline...
echo [INFO]  (Many tests will FAIL -- that is intentional.)
echo.
call mvn test 2>&1
:: intentionally ignoring exit code here

:: -------------------------------------------------------------------------
:: 5. Instructions
:: -------------------------------------------------------------------------
echo.
echo ======================================================================
echo   TaskFlow -- Bug-fixing Exercise
echo ======================================================================
echo.
echo   Repository layout:
echo     src\main\java\     -- application source  (this is where the bugs are)
echo     src\test\java\     -- automated tests      (do NOT modify these)
echo     issues\            -- your assigned issue  (e.g. issues\issue-001.md)
echo.
echo   Workflow:
echo     1. Read the issue assigned to you  (e.g. issues\issue-001.md)
echo     2. Find and fix the bug in src\main\java\
echo     3. Verify your fix:    check.bat <issue-number>
echo        Example:            check.bat 1
echo     4. Open a Pull Request -- do NOT merge it yourself
echo.
echo   Useful commands:
echo     check.bat 15           check tests for issue #15
echo     mvn test               run the full test suite
echo     mvn compile            quick compile check
echo.
echo   Rules:
echo     - Fix only the bug described in your issue
echo     - Do not modify test files
echo     - One commit per issue is preferred
echo.
echo ======================================================================
echo.
goto :eof

:fail
echo.
echo [ERROR] Setup could not complete. Fix the issues above and re-run setup.bat
echo.
exit /b 1
endlocal
