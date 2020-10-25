#!/usr/bin/env bash

apt install -y curl

# MongoDB
wget -qO - https://www.mongodb.org/static/pgp/server-4.4.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/4.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.4.list
apt update
apt install -y mongodb-org
systemctl enable mongod
systemctl start mongod

# Parity
useradd -m --shell /bin/bash owner
apt install -y snapd
snap install parity
cat <<EOF > /etc/systemd/system/parity.service
[Unit]
Description=Parity RPC Service
After=network.target
StartLimitIntervalSec=0

[Service]
Type=simple
Restart=always
RestartSec=1
User=owner
ExecStart=/usr/bin/env parity --jsonrpc-apis=all --jsonrpc-interface='127.0.0.1' --jsonrpc-hosts=all --db-compaction=ssd --cache-size=4096 --warp-barrier=11000000 --no-serve-light --min-peers=100 --max-peers=250 --snapshot-peers=100

[Install]
WantedBy=multi-user.target
EOF
systemctl enable --now parity
