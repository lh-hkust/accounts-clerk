#!/bin/bash
# Local build script for Hermes Android
# Supports: Windows/Linux/macOS, arm64/x86, debug/release, skip signing

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")/hermes-android"

# Default values
BUILD_TYPE="release"
ABI="both"
SKIP_SIGNING=false
CLEAN=false

# Parse arguments
usage() {
    echo "Usage: $0 [options]"
    echo "Options:"
    echo "  -t, --type <debug|release>  Build type (default: release)"
    echo "  -a, --abi <arm64|x86|both>  Target ABI (default: both)"
    echo "  -s, --skip-signing         Skip signing, use debug signature"
    echo "  -c, --clean                Clean before build"
    echo "  -h, --help                 Show this help"
    echo ""
    echo "Examples:"
    echo "  $0 -t debug -a arm64          # Build debug APK for arm64"
    echo "  $0 -t release -s              # Build release APKs with debug signing"
    echo "  $0 --clean --type release     # Clean and build release APKs"
}

while [[ $# -gt 0 ]]; do
    case $1 in
        -t|--type)
            BUILD_TYPE="$2"
            shift 2
            ;;
        -a|--abi)
            ABI="$2"
            shift 2
            ;;
        -s|--skip-signing)
            SKIP_SIGNING=true
            shift
            ;;
        -c|--clean)
            CLEAN=true
            shift
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            usage
            exit 1
            ;;
    esac
done

# Validate build type
if [[ "$BUILD_TYPE" != "debug" && "$BUILD_TYPE" != "release" ]]; then
    echo "Error: Invalid build type '$BUILD_TYPE'. Must be 'debug' or 'release'."
    exit 1
fi

# Validate ABI
if [[ "$ABI" != "arm64" && "$ABI" != "x86" && "$ABI" != "both" ]]; then
    echo "Error: Invalid ABI '$ABI'. Must be 'arm64', 'x86', or 'both'."
    exit 1
fi

# Detect OS
OS_TYPE="unknown"
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    OS_TYPE="windows"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    OS_TYPE="macos"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    OS_TYPE="linux"
fi

echo "========================================"
echo "Hermes Android Local Build"
echo "========================================"
echo "OS:         $OS_TYPE"
echo "Build Type: $BUILD_TYPE"
echo "ABI:        $ABI"
echo "Skip Sign:  $SKIP_SIGNING"
echo "Clean:      $CLEAN"
echo "========================================"

cd "$PROJECT_DIR"

# Set environment variables
export SKIP_SIGNING=$SKIP_SIGNING

# Clean if requested
if [[ "$CLEAN" == true ]]; then
    echo "Cleaning..."
    ./gradlew clean --no-daemon
fi

# Build command
BUILD_CMD="assemble${BUILD_TYPE^}"

echo "Building..."
if [[ "$ABI" == "both" ]]; then
    # Build both ABIs (splits.abi will generate separate APKs)
    ./gradlew --no-daemon --no-configuration-cache :app:$BUILD_CMD
else
    # Build single ABI by modifying splits config temporarily
    # Note: For single ABI, we can use -PabiOverride=$ABI
    ./gradlew --no-daemon --no-configuration-cache :app:$BUILD_CMD -PabiOverride=$ABI
fi

# List output APKs
OUTPUT_DIR="app/build/outputs/apk/$BUILD_TYPE"
echo ""
echo "========================================"
echo "Build Output:"
echo "========================================"

if [[ -d "$OUTPUT_DIR" ]]; then
    for apk in "$OUTPUT_DIR"/*.apk; do
        if [[ -f "$apk" ]]; then
            SIZE=$(du -h "$apk" | cut -f1)
            NAME=$(basename "$apk")
            echo "  $NAME ($SIZE)"
        fi
    done
else
    echo "  No APKs found in $OUTPUT_DIR"
fi

echo "========================================"
echo "Build completed successfully!"
echo "========================================"