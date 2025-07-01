#!/bin/bash

apt update -y

curl -fsSL https://get.docker.com -o get-docker.sh
./get-docker.sh

sudo apt install -y unzip

curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
./aws/install

rm -rf aws
rm -rf awscliv2.zip

touch /home/ubuntu/ble


#
TOKEN=$(curl -s -X PUT "http://169.254.169.254/latest/api/token" \
  -H "X-aws-ec2-metadata-token-ttl-seconds: 21600")

INSTANCE_ID=$(curl -s -H "X-aws-ec2-metadata-token: $TOKEN" \
  http://169.254.169.254/latest/meta-data/instance-id)

#touch $INSTANCE_ID
#
#aws s3 cp $INSTANCE_ID s3://ec2-s3-put-only
