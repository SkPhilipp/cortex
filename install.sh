#!/usr/bin/env bash

# User Management
useradd -m --shell /bin/bash cortex
mkdir /home/cortex/.ssh
cp ~/.ssh/authorized_keys /home/cortex/.ssh/authorized_keys
chown -R cortex:cortex /home/cortex/.ssh

# MongoDB
wget -qO - https://www.mongodb.org/static/pgp/server-4.4.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/4.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.4.list
apt update
apt install -y mongodb-org
systemctl enable mongod
systemctl start mongod
