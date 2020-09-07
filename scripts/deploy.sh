#!/usr/bin/env sh

# Deploy Documentation
ssh root@$CORTEX_DOCUMENTATION "rm -rf /var/www/html/*"
scp -r cortex-vm/build/asciidoc/html5/* root@$CORTEX_DOCUMENTATION:/var/www/html
scp -r cortex-fuzzing/build/asciidoc/html5/* root@$CORTEX_DOCUMENTATION:/var/www/html
scp -r cortex-symbolic/build/asciidoc/html5/* root@$CORTEX_DOCUMENTATION:/var/www/html
scp -r cortex-analysis/build/asciidoc/html5/* root@$CORTEX_DOCUMENTATION:/var/www/html
scp -r cortex-ethereum/build/asciidoc/html5/* root@$CORTEX_DOCUMENTATION:/var/www/html
scp -r cortex-documentation/build/asciidoc/html5/* root@$CORTEX_DOCUMENTATION:/var/www/html
