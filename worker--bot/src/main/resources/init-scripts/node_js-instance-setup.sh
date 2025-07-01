#!/bin/bash

echo "alias c='clear'" > /home/ubuntu/.bashrc

# Update package list quietly
sudo apt update -y -qq

# Install curl if not installed
sudo apt install -y -qq curl

# Setup NodeSource repo for Node.js 22.x (LTS)
curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -

# Install Node.js and npm globally
sudo apt install -y -qq nodejs

# Now you can globally install pm2 without sudo issues
sudo npm install -g pm2




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

