@echo off
setlocal enabledelayedexpansion
:: setup.bat -- Student onboarding script for TaskFlow (Windows)
:: Run this once after cloning the repository.
::
:: What it does:
::   1. Checks that Java 21+ is available on PATH
::   2. Checks that Git is available (installs via winget if not)
::   3. Checks that Maven 3.x is available on PATH
::   4. Runs an initial build (compile) to confirm the project builds
::   5. Runs the full test suite to show your starting baseline
::   6. Prints a getting-started summary
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
:: Use java directly as the test -- more reliable than 'where java' since
:: some JDK installers set JAVA_HOME but don't update the WHERE search path.
java --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java not found. 'java' could not be executed.
    echo         Download and install Java 21+ from:
    echo           https://adoptium.net/temurin/releases/?version=21
    echo         After installing, open a NEW terminal and re-run this script.
    goto :fail
)

:: --- Parse Java version ---
:: Strategy 1: java --version  (Java 9+)  writes to stdout
::   Output:  openjdk 25 2025-09-16   ->  token 2 = 25
set "JAVA_VERSION_STR="
:: for /f iterates every output line; 'if not defined' keeps only the first match
for /f "tokens=2 delims= " %%v in ('java --version 2^>nul') do (
    if not defined JAVA_VERSION_STR set "JAVA_VERSION_STR=%%v"
)
:: Strategy 2: java -version  (all JDKs) writes to stderr
::   Output:  openjdk version "21.0.3" ...   ->  token 3, strip quotes
if not defined JAVA_VERSION_STR (
    for /f "tokens=3 delims= " %%v in ('java -version 2^>^&1') do (
        if not defined JAVA_VERSION_STR set "JAVA_VERSION_STR=%%~v"
    )
)
:: Extract major version: split on '.' AND '-' to handle 21.0.3, 25, 25-ea, etc.
for /f "tokens=1 delims=.-" %%m in ("!JAVA_VERSION_STR!") do set "JAVA_MAJOR=%%m"
:: Legacy: Java 8 reports "1.8.x" so major would be "1"
if "!JAVA_MAJOR!"=="1" (
    for /f "tokens=2 delims=." %%m in ("!JAVA_VERSION_STR!") do set "JAVA_MAJOR=%%m"
)

if !JAVA_MAJOR! LSS 21 (
    echo [WARN]  Java !JAVA_MAJOR! found -- this project requires Java 21 or later.
    echo         Download Java 21+ from: https://adoptium.net/temurin/releases/?version=21
    goto :fail
)
echo [OK]    Java !JAVA_MAJOR! found.

:: -------------------------------------------------------------------------
:: 2. Check / install Git
:: -------------------------------------------------------------------------
echo [INFO]  Checking Git...
where git >nul 2>&1
if errorlevel 1 (
    echo [WARN]  Git not found on PATH. Attempting to install via winget...
    where winget >nul 2>&1
    if errorlevel 1 (
        echo [ERROR] winget is not available on this machine.
        echo         Install Git manually from: https://git-scm.com/downloads
        echo         Then re-run this script.
        goto :fail
    )
    winget install --id Git.Git --silent --accept-package-agreements --accept-source-agreements
    if errorlevel 1 (
        echo [ERROR] Automatic Git install failed.
        echo         Install Git manually from: https://git-scm.com/downloads
        goto :fail
    )
    echo [OK]    Git installed successfully.
    echo [WARN]  You may need to close and reopen this terminal for git to be on PATH.
    echo         If the next steps fail, re-run setup.bat in a new terminal.
) else (
    for /f "tokens=3 delims= " %%v in ('git --version 2^>^&1') do (
        echo [OK]    Git %%v found.
        goto :git_ok
    )
)
:git_ok

:: -------------------------------------------------------------------------
:: 3. Check Maven  (falls back to bundled wrapper mvnw.cmd)
:: -------------------------------------------------------------------------
echo [INFO]  Checking Maven...
set "MVN=mvn"
mvn --version >nul 2>&1
if errorlevel 1 (
    if exist "%REPO_ROOT%\mvnw.cmd" (
        echo [WARN]  mvn not found on PATH -- using bundled Maven Wrapper ^(mvnw.cmd^).
        set "MVN=%REPO_ROOT%\mvnw.cmd"
    ) else (
        echo [ERROR] Maven not found and no wrapper present.
        echo         Install Maven from https://maven.apache.org/download.cgi
        echo         or re-clone the repo to restore the wrapper.
        goto :fail
    )
) else (
    for /f "tokens=3 delims= " %%v in ('mvn --version 2^>^&1') do (
        echo [OK]    Maven %%v found.
        goto :maven_ok
    )
)
:maven_ok

:: -------------------------------------------------------------------------
:: 4. Initial compile
:: -------------------------------------------------------------------------
echo.
echo [INFO]  Running initial compile...
cd /d "%REPO_ROOT%"
call "!MVN!" compile -q
if errorlevel 1 (
    echo [WARN]  Compilation had errors.
    echo         Some bugs in this project may prevent a clean compile.
    echo         That is expected -- fixing those bugs is part of the exercise.
) else (
    echo [OK]    Project compiled successfully.
)

:: -------------------------------------------------------------------------
:: 5. Run full test suite (failures expected -- do not exit on failure)
:: -------------------------------------------------------------------------
echo.
echo [INFO]  Running test suite to show your starting baseline...
echo [INFO]  (Many tests will FAIL -- that is intentional.)
echo.
call "!MVN!" test 2>&1
:: intentionally ignoring exit code here

:: -------------------------------------------------------------------------
:: 6. Instructions
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
echo     check.bat 15              check tests for issue #15
echo     !MVN! test                run the full test suite
echo     !MVN! compile             quick compile check
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
