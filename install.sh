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

# Geth
add-apt-repository -y ppa:ethereum/ethereum
apt update
apt install -y ethereum
useradd -m --shell /bin/bash geth
cat <<EOF > /etc/systemd/system/geth.service
[Unit]
Description=Geth RPC Service
After=network.target
StartLimitIntervalSec=0

[Service]
Type=simple
Restart=always
RestartSec=1
User=geth
ExecStart=/usr/bin/env geth --datadir /home/geth/datadir --rpcapi personal,eth,net,web3 --rpc

[Install]
WantedBy=multi-user.target
EOF
systemctl start geth
systemctl enable geth

# Parity
apt install -y snapd
snap install parity
