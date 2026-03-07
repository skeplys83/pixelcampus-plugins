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

# Prepare SSH key
mkdir -p ~/.ssh
printf '%s\n' "$JNR_SFTP_SSH_KEY" > ~/.ssh/id_ed25519
chmod 600 ~/.ssh/id_ed25519

# Add server host key
if ! ssh-keyscan -p "$JNR_SFTP_PORT" "$JNR_SFTP_HOST" >> ~/.ssh/known_hosts 2>/dev/null; then
  echo "Warning: ssh-keyscan failed for $JNR_SFTP_HOST:$JNR_SFTP_PORT; continuing with strict host key accept-new" >&2
fi

# Upload plugin
scp -o StrictHostKeyChecking=accept-new -i ~/.ssh/id_ed25519 \
    -P "$JNR_SFTP_PORT" \
    "$JAR_PATH" \
    "$JNR_SFTP_USER@$JNR_SFTP_HOST:/plugins/$JNR_PLUGIN_FILE_NAME"

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