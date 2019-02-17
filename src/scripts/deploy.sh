#!/usr/bin/env bash

# Build Documentation
./gradlew clean asciidoctor

# Deploy Documentation
ssh root@cortex-production "rm -rf /var/www/html/*"
scp -r ./build/asciidoc/html5/index.html root@cortex-production:/var/www/html
