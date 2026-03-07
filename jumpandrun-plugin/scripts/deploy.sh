#!/usr/bin/env bash
set -euo pipefail

require_env() {
  local name="$1"
  if [[ -z "${!name:-}" ]]; then
    echo "Missing required environment variable: $name" >&2
    exit 1
  fi
}

required_vars=(
  JNR_SFTP_HOST
  JNR_SFTP_PORT
  JNR_SERVER_ID
  JNR_PANEL_URL
  JNR_SFTP_USER
  JNR_SFTP_SSH_KEY
  JNR_CLIENT_API_KEY
  JNR_PLUGIN_FILE_NAME
)

for var_name in "${required_vars[@]}"; do
  require_env "$var_name"
done

# Build plugin
chmod +x ./gradlew
./gradlew build

JAR_PATH=$(find build/libs -name "*.jar" ! -name "*sources.jar" ! -name "*javadoc.jar" | head -n 1)

if [[ -z "$JAR_PATH" ]]; then
  echo "No plugin jar found in build/libs after build" >&2
  exit 1
fi

echo "Built: $JAR_PATH"

# Prepare SSH key for SFTP
mkdir -p ~/.ssh
printf '%s\n' "$JNR_SFTP_SSH_KEY" > ~/.ssh/id_ed25519
chmod 600 ~/.ssh/id_ed25519

echo "Uploading via SFTP to ${JNR_SFTP_HOST}:${JNR_SFTP_PORT}"
sftp -v \
  -o StrictHostKeyChecking=accept-new \
  -o ConnectTimeout=20 \
  -o ConnectionAttempts=3 \
  -o KexAlgorithms=curve25519-sha256@libssh.org,diffie-hellman-group14-sha256 \
  -i ~/.ssh/id_ed25519 \
  -P "$JNR_SFTP_PORT" \
  "$JNR_SFTP_USER@$JNR_SFTP_HOST" <<EOF
put "$JAR_PATH" "/plugins/$JNR_PLUGIN_FILE_NAME"
bye
EOF

echo "Uploaded plugin"

# Restart server
HTTP_CODE=$(curl -sS -o /tmp/jnr_restart_response.json -w "%{http_code}" -X POST "$JNR_PANEL_URL/api/client/servers/$JNR_SERVER_ID/power" \
  -H "Authorization: Bearer $JNR_CLIENT_API_KEY" \
  -H "Accept: application/vnd.pterodactyl.v1+json" \
  -H "Content-Type: application/json" \
  -d '{"signal":"restart"}')

if [[ ! "$HTTP_CODE" =~ ^[0-9]{3}$ ]] || (( HTTP_CODE < 200 || HTTP_CODE >= 300 )); then
  echo "Restart request failed with HTTP $HTTP_CODE" >&2
  cat /tmp/jnr_restart_response.json >&2 || true
  exit 1
fi

echo "Deployment finished."
