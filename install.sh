#!/usr/bin/env bash
apt update
apt -y install nginx

# User Management
useradd -m --shell /bin/bash cortex
mkdir /home/cortex/.ssh
cp ~/.ssh/authorized_keys /home/cortex/.ssh/authorized_keys
chown -R cortex:cortex /home/cortex/.ssh

# Configure Site
echo "User-agent: *" > /var/www/html/robots.txt
echo "Disallow: /" >> /var/www/html/robots.txt

# Configure SSL
yes "" | openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/ssl/private/nginx-selfsigned.key -out /etc/ssl/certs/nginx-selfsigned.crt
echo "ssl_certificate /etc/ssl/certs/nginx-selfsigned.crt;" > /etc/nginx/snippets/self-signed.conf
echo "ssl_certificate_key /etc/ssl/private/nginx-selfsigned.key;" >> /etc/nginx/snippets/self-signed.conf
