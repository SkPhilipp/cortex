#!/usr/bin/env bash
add-apt-repository ppa:openjdk-r/ppa -y
apt update
apt -y install openjdk-11-jre-headless unzip gcc nginx

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

# Configure Site
echo "User-agent: *" > /var/www/html/robots.txt
echo "Disallow: /" >> /var/www/html/robots.txt

# Configure SSL
yes "" | openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/ssl/private/nginx-selfsigned.key -out /etc/ssl/certs/nginx-selfsigned.crt
echo "ssl_certificate /etc/ssl/certs/nginx-selfsigned.crt;" > /etc/nginx/snippets/self-signed.conf
echo "ssl_certificate_key /etc/ssl/private/nginx-selfsigned.key;" >> /etc/nginx/snippets/self-signed.conf
