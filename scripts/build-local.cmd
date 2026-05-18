@echo off
REM Local build script for Hermes Android (Windows)
REM Supports: arm64/x86, debug/release, skip signing

setlocal EnableDelayedExpansion

set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%..\hermes-android

REM Default values
set BUILD_TYPE=release
set ABI=both
set SKIP_SIGNING=false
set CLEAN=false

REM Parse arguments
:parse_args
if "%~1"=="" goto :check_args
if "%~1"=="-t" (set BUILD_TYPE=%~2& shift & shift & goto :parse_args)
if "%~1"=="--type" (set BUILD_TYPE=%~2& shift & shift & goto :parse_args)
if "%~1"=="-a" (set ABI=%~2& shift & shift & goto :parse_args)
if "%~1"=="--abi" (set ABI=%~2& shift & shift & goto :parse_args)
if "%~1"=="-s" (set SKIP_SIGNING=true& shift & goto :parse_args)
if "%~1"=="--skip-signing" (set SKIP_SIGNING=true& shift & goto :parse_args)
if "%~1"=="-c" (set CLEAN=true& shift & goto :parse_args)
if "%~1"=="--clean" (set CLEAN=true& shift & goto :parse_args)
if "%~1"=="-h" goto :show_help
if "%~1"=="--help" goto :show_help
echo Unknown option: %~1
goto :show_help

:show_help
echo Usage: %~nx0 [options]
echo Options:
echo   -t, --type [debug|release]  Build type (default: release)
echo   -a, --abi [arm64|x86|both]  Target ABI (default: both)
echo   -s, --skip-signing         Skip signing, use debug signature
echo   -c, --clean                Clean before build
echo   -h, --help                 Show this help
echo.
echo Examples:
echo   %~nx0 -t debug -a arm64          # Build debug APK for arm64
echo   %~nx0 -t release -s              # Build release APKs with debug signing
echo   %~nx0 --clean --type release     # Clean and build release APKs
exit /b 1

:check_args
REM Validate build type
if not "%BUILD_TYPE%"=="debug" if not "%BUILD_TYPE%"=="release" (
    echo Error: Invalid build type '%BUILD_TYPE%'. Must be 'debug' or 'release'.
    exit /b 1
)

REM Validate ABI
if not "%ABI%"=="arm64" if not "%ABI%"=="x86" if not "%ABI%"=="both" (
    echo Error: Invalid ABI '%ABI%'. Must be 'arm64', 'x86', or 'both'.
    exit /b 1
)

echo ========================================
echo Hermes Android Local Build
echo ========================================
echo OS:         Windows
echo Build Type: %BUILD_TYPE%
echo ABI:        %ABI%
echo Skip Sign:  %SKIP_SIGNING%
echo Clean:      %CLEAN%
echo ========================================

cd /d "%PROJECT_DIR%"

REM Set environment variables
set SKIP_SIGNING=%SKIP_SIGNING%

REM Clean if requested
if "%CLEAN%"=="true" (
    echo Cleaning...
    call gradlew.bat clean --no-daemon
)

REM Build command (capitalize first letter)
set BUILD_CMD=assemble
if "%BUILD_TYPE%"=="debug" set BUILD_CMD=assembleDebug
if "%BUILD_TYPE%"=="release" set BUILD_CMD=assembleRelease

echo Building...
call gradlew.bat --no-daemon --no-configuration-cache :app:%BUILD_CMD%

REM List output APKs
set OUTPUT_DIR=app\build\outputs\apk\%BUILD_TYPE%
echo.
echo ========================================
echo Build Output:
echo ========================================

if exist "%OUTPUT_DIR%\*.apk" (
    for %%f in ("%OUTPUT_DIR%\*.apk") do (
        echo   %%~nxf
    )
) else (
    echo   No APKs found in %OUTPUT_DIR%
)

echo ========================================
echo Build completed successfully!
echo ========================================

endlocal