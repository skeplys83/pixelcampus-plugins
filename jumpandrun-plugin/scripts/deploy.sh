#!/usr/bin/env bash
set -euo pipefail

# ─── Configuration ────────────────────────────────────────────────────────────

JNR_SFTP_HOST="${JNR_SFTP_HOST:-}"
JNR_SFTP_PORT="${JNR_SFTP_PORT:-2022}"
JNR_SFTP_USER="${JNR_SFTP_USER:-}"
JNR_SFTP_PASSWORD="${JNR_SFTP_PASSWORD:-}"

JNR_PANEL_URL="${JNR_PANEL_URL:-}"
JNR_SERVER_ID="${JNR_SERVER_ID:-}"
JNR_CLIENT_API_KEY="${JNR_CLIENT_API_KEY:-}"

JNR_PLUGIN_FILE_NAME="${JNR_PLUGIN_FILE_NAME:-JumpAndRun.jar}"

# ─── Validation ───────────────────────────────────────────────────────────────

require_var() {
    local name="$1"
    if [ -z "${!name:-}" ]; then
        echo "Error: required environment variable '$name' is not set." >&2
        exit 1
    fi
}

require_var JNR_SFTP_HOST
require_var JNR_SFTP_PORT
require_var JNR_SFTP_USER
require_var JNR_SFTP_PASSWORD
require_var JNR_PANEL_URL
require_var JNR_SERVER_ID
require_var JNR_CLIENT_API_KEY
require_var JNR_PLUGIN_FILE_NAME

for cmd in lftp curl; do
    if ! command -v "$cmd" &>/dev/null; then
        echo "Error: '$cmd' is not installed." >&2
        exit 1
    fi
done

# ─── Build ────────────────────────────────────────────────────────────────────

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.."

chmod +x ./gradlew
./gradlew build

JAR_PATH="$(
    find build/libs -maxdepth 1 -type f -name "*.jar" \
        ! -name "*-sources.jar" \
        ! -name "*-javadoc.jar" \
    | head -n 1
)"

if [ -z "$JAR_PATH" ]; then
    echo "Error: no plugin jar found in build/libs" >&2
    exit 1
fi

echo "Built: $JAR_PATH"

# ─── Upload ───────────────────────────────────────────────────────────────────

echo "Uploading plugin via SFTP..."

lftp -u "$JNR_SFTP_USER,$JNR_SFTP_PASSWORD" "sftp://$JNR_SFTP_HOST:$JNR_SFTP_PORT" <<EOF
set sftp:auto-confirm yes
put "$JAR_PATH" -o "/plugins/$JNR_PLUGIN_FILE_NAME"
bye
EOF

# ─── Restart ──────────────────────────────────────────────────────────────────

echo "Restarting server..."

curl -sSf -X POST "$JNR_PANEL_URL/api/client/servers/$JNR_SERVER_ID/power" \
    -H "Authorization: Bearer $JNR_CLIENT_API_KEY" \
    -H "Accept: application/vnd.pterodactyl.v1+json" \
    -H "Content-Type: application/json" \
    -d '{"signal": "restart"}'

echo
echo "Deployment finished."
