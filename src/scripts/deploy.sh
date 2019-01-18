#!/usr/bin/env bash

# Build Documentation
./gradlew asciidoctor

# Deploy Documentation
ssh root@cortex "rm -rf /var/www/html/*"
scp -r ./build/asciidoc/html5/. root@cortex:/var/www/html
