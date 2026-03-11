#!/usr/bin/env bash
set -euo pipefail

require_env() {
  local name="$1"
  if [[ -z "${!name:-}" ]]; then
    echo "Missing required environment variable: $name" >&2
    exit 1
  fi
}

# Non-secret deployment settings.
readonly PTERODACTYL_SFTP_HOST="node-01.pterodactyl.reutlingen.university"
readonly PTERODACTYL_SFTP_PORT="25022"
readonly JUMPANDRUN_SERVER_ID="138c081c"
readonly PTERODACTYL_PANEL_URL="https://panel.pterodactyl.reutlingen.university"
readonly JUMPANDRUN_PLUGIN_FILE_NAME="Pixelcampus-SMP.jar"

required_vars=(
  PTERODACTYL_TESTSERVER_SFTP_USER
  PTERODACTYL_SFTP_SSH_KEY
  PTERODACTYL_API_KEY
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
printf '%s\n' "$PTERODACTYL_SFTP_SSH_KEY" > ~/.ssh/id_ed25519
chmod 600 ~/.ssh/id_ed25519

echo "Uploading via SFTP to ${PTERODACTYL_SFTP_HOST}:${PTERODACTYL_SFTP_PORT}"
sftp -v \
  -o StrictHostKeyChecking=accept-new \
  -o ConnectTimeout=20 \
  -o ConnectionAttempts=3 \
  -o KexAlgorithms=curve25519-sha256@libssh.org,diffie-hellman-group14-sha256 \
  -i ~/.ssh/id_ed25519 \
  -P "$PTERODACTYL_SFTP_PORT" \
  "$PTERODACTYL_TESTSERVER_SFTP_USER@$PTERODACTYL_SFTP_HOST" <<EOF
put "$JAR_PATH" "/plugins/$JUMPANDRUN_PLUGIN_FILE_NAME"
bye
EOF

echo "Uploaded plugin"

# Notify all players that a new plugin version has been deployed
curl -sS -o /dev/null -X POST "$PTERODACTYL_PANEL_URL/api/client/servers/$JUMPANDRUN_SERVER_ID/command" \
  -H "Authorization: Bearer $PTERODACTYL_API_KEY" \
  -H "Accept: application/vnd.pterodactyl.v1+json" \
  -H "Content-Type: application/json" \
  -d '{"command":"tellraw @a {\"text\":\"A new plugin version for smp-plugin has been deployed. Please restart the server.\",\"color\":\"yellow\"}"}' || true

echo "Deployment finished. Please restart the server manually."