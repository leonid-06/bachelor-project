#!/bin/bash

echo "alias c='clear'" > /home/ubuntu/.bashrc

sudo apt update -y -qq

sudo curl -fsSL https://get.docker.com -o get-docker.sh
sudo chmod +x get-docker.sh
sudo ./get-docker.sh

sudo apt install -y -qq unzip

sudo curl -fsSL "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
sudo unzip -q awscliv2.zip
sudo ./aws/install > /dev/null 2>&1

sudo rm -rf aws awscliv2.zip get-docker.sh

REGION="eu-central-1"

TOKEN=$(curl -s -X PUT "http://169.254.169.254/latest/api/token" \
  -H "X-aws-ec2-metadata-token-ttl-seconds: 21600")

INSTANCE_ID=$(curl -s -H "X-aws-ec2-metadata-token: $TOKEN" \
  http://169.254.169.254/latest/meta-data/instance-id)

PARAMETER_PATH="/setup/${INSTANCE_ID}"

aws ssm put-parameter \
  --name "$PARAMETER_PATH" \
  --value "done" \
  --type String \
  --overwrite \
  --region "$REGION"