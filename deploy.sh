#!/usr/bin/env bash

# Build Documentation
./gradlew clean build asciidoctor

# Deploy Documentation
ssh root@cortex-production "rm -rf /var/www/html/*"
scp -r cortex-vm/build/asciidoc/html5/* root@cortex-production:/var/www/html
scp -r cortex-fuzzing/build/asciidoc/html5/* root@cortex-production:/var/www/html
scp -r cortex-symbolic/build/asciidoc/html5/* root@cortex-production:/var/www/html
scp -r cortex-analysis/build/asciidoc/html5/* root@cortex-production:/var/www/html
scp -r cortex-ethereum/build/asciidoc/html5/* root@cortex-production:/var/www/html
scp -r cortex-documentation/build/asciidoc/html5/* root@cortex-production:/var/www/html
