#!/usr/bin/env bash
apt update
apt -y install openjdk-11-jre-headless unzip gcc

# Port Forwarding
iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080

# User Management
useradd -m --shell /bin/bash cortex-host
mkdir /home/cortex-host/.ssh
cp ~/.ssh/authorized_keys /home/cortex-host/.ssh/authorized_keys
chown -R cortex-host:cortex-host /home/cortex-host/.ssh

# Microsoft Z3
curl -L https://github.com/Z3Prover/z3/releases/download/z3-4.7.1/z3-4.7.1-x64-ubuntu-16.04.zip --output z3.zip
unzip z3.zip
cp z3-4.7.1-x64-ubuntu-16.04/bin/libz3* /usr/lib/x86_64-linux-gnu/
rm z3.zip
rm -rf z3-4.7.1-x64-ubuntu-16.04
