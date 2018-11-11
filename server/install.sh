#!/usr/bin/env bash
apt update
apt -y install openjdk-10-jre-headless unzip gcc nginx

# User Management
useradd -m --shell /bin/bash cortex
mkdir /home/cortex/.ssh
cp ~/.ssh/authorized_keys /home/cortex/.ssh/authorized_keys
chown -R cortex:cortex /home/cortex/.ssh

# Microsoft Z3
curl -L https://github.com/Z3Prover/z3/releases/download/z3-4.7.1/z3-4.7.1-x64-ubuntu-16.04.zip --output z3.zip
unzip z3.zip
cp z3-4.7.1-x64-ubuntu-16.04/bin/libz3* /usr/lib/x86_64-linux-gnu/
rm z3.zip
rm -rf z3-4.7.1-x64-ubuntu-16.04
