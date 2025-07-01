#!/bin/bash

# script will be end if at least one status code will be not equal 0
set -e

APP_NAME="myapp"
SERVICE_FILE="/etc/systemd/system/${APP_NAME}.service"
ZIP_FILE="/home/ubuntu/app.zip"
DEPLOY_DIR="/home/ubuntu/app"
JAR_OUTPUT="$DEPLOY_DIR/app.jar"

# Перенаправление порта 80 на 8080 (если правило не добавлено)
iptables -t nat -C PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080 2>/dev/null || \
iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080

# Очистка старой папки
rm -rf "$DEPLOY_DIR"
mkdir -p "$DEPLOY_DIR"

# Распаковка архива
unzip -q "$ZIP_FILE" -d "$DEPLOY_DIR"

cd "$DEPLOY_DIR" || { echo "App folder not found"; exit 1; }

# Поиск папки проекта
INNER_PROJECT_FOLDER=$(find . -mindepth 1 -maxdepth 1 -type d | head -n 1)
cd "$INNER_PROJECT_FOLDER" || { echo "Failed to cd into project folder"; exit 1; }

# Загрузка .env (если есть)
ENV_FILE=$(find . -name "*.env" | head -n 1)
if [[ -n "$ENV_FILE" ]]; then
    echo "Loading environment from $ENV_FILE"
    set -a
    source "$ENV_FILE"
    set +a
fi

# Сборка и копирование .jar
if [[ -f "pom.xml" ]]; then
    echo "Maven project detected"
    mvn package -DskipTests
    cp target/*.jar "$JAR_OUTPUT"
elif [[ -f "build.gradle" || -f "build.gradle.kts" ]]; then
    echo "Gradle project detected"
    ./gradlew build -x test || ./gradle build -x test
    JAR_PATH=$(find build/libs -name "*.jar" | head -n 1)
    cp "$JAR_PATH" "$JAR_OUTPUT"
else
    echo "No recognizable build file found"
    exit 1
fi

chown -R ubuntu:ubuntu /home/ubuntu/app


ENV_DIRECTIVE=""
if [[ -n "$ENV_FILE" ]]; then
    ABS_ENV_PATH="$(realpath "$ENV_FILE")"
    ENV_DIRECTIVE="EnvironmentFile=$ABS_ENV_PATH"
fi

# Создание systemd unit файла
echo "Creating systemd service..."

sudo tee "$SERVICE_FILE" > /dev/null <<EOF
[Unit]
Description=Java App Service
After=network.target

[Service]
User=ubuntu
WorkingDirectory=$DEPLOY_DIR
ExecStart=/usr/bin/java -jar $JAR_OUTPUT
SuccessExitStatus=143
Restart=always
RestartSec=5
$ENV_DIRECTIVE

[Install]
WantedBy=multi-user.target
EOF

# Применение systemd
sudo systemctl daemon-reexec
sudo systemctl daemon-reload
sudo systemctl enable "$APP_NAME"
sudo systemctl restart "$APP_NAME"

echo "Deployment completed and service started."





