#!/usr/bin/env bash
set -euo pipefail

# Build plugin
chmod +x ./gradlew
./gradlew build

JAR_PATH=$(find build/libs -name "*.jar" ! -name "*sources.jar" ! -name "*javadoc.jar" | head -n 1)

echo "Built: $JAR_PATH"

# Prepare SSH key
mkdir -p ~/.ssh
echo "$JNR_SFTP_SSH_KEY" > ~/.ssh/id_ed25519
chmod 600 ~/.ssh/id_ed25519

# Add server host key
ssh-keyscan -p "$JNR_SFTP_PORT" "$JNR_SFTP_HOST" >> ~/.ssh/known_hosts 2>/dev/null

# Upload plugin
scp -i ~/.ssh/id_ed25519 \
    -P "$JNR_SFTP_PORT" \
    "$JAR_PATH" \
    "$JNR_SFTP_USER@$JNR_SFTP_HOST:/plugins/$JNR_PLUGIN_FILE_NAME"

echo "Uploaded plugin"

# Restart server
curl -s -X POST "$JNR_PANEL_URL/api/client/servers/$JNR_SERVER_ID/power" \
  -H "Authorization: Bearer $JNR_CLIENT_API_KEY" \
  -H "Accept: application/vnd.pterodactyl.v1+json" \
  -H "Content-Type: application/json" \
  -d '{"signal":"restart"}'

echo "Deployment finished."