#!/bin/bash
# Restore keystore from base64 encoded secret
# Usage: restore-keystore.sh <base64_encoded_keystore> <output_path>
#
# This script decodes a base64-encoded keystore and saves it to the specified path.
# Used by CI/CD pipelines to restore signing keystore from secrets.
#
# Example:
#   ./restore-keystore.sh "$KEYSTORE_BASE64" keystore.jks

set -e

KEYSTORE_BASE64="${1:-}"
OUTPUT_PATH="${2:-keystore.jks}"

if [ -z "$KEYSTORE_BASE64" ]; then
    echo "Error: KEYSTORE_BASE64 is empty or not provided"
    echo "Usage: restore-keystore.sh <base64_encoded_keystore> <output_path>"
    exit 1
fi

# Decode and save keystore
echo "Restoring keystore to $OUTPUT_PATH..."
echo "$KEYSTORE_BASE64" | base64 -d > "$OUTPUT_PATH"

# Verify file was created
if [ -f "$OUTPUT_PATH" ]; then
    SIZE=$(wc -c < "$OUTPUT_PATH")
    echo "Keystore restored successfully (size: $SIZE bytes)"
else
    echo "Error: Failed to create keystore file"
    exit 1
fi

# Set restrictive permissions (only owner can read)
chmod 600 "$OUTPUT_PATH"
echo "Permissions set to 600 (owner read/write only)"