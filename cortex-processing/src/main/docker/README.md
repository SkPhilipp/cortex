# Cortex Processing Sandbox

Cortex Processing Sandbox is a basic setup to allow interaction with a local Ethereum blockchain through Geth.

## Setup

The Docker Compose configuration starts `bootnode` instance and a `geth` instance configured to mine for `eth.accounts[0]`.

    docker build . -t cortex-sandbox
    docker-compose up

The first time this is executed a new boot key, account key, and `geth` `datadir` will be set up in the local `./volume` directory.
Subsequent runs will reuse existing the keys & blockchain. To reset this simply clear out the `volume` directory.
The password for the local Ethereum blockchain account is `i laugh`.

## Run

To interact with the chain enter the `docker-compose` miner service with `geth` as such;

    docker exec -it miner geth attach --exec 'eth.accounts'

Scripts in the `./scripts` directory can be launched as such;

    docker exec -it miner geth attach --preload '/scripts/test/barriers-bytecode.js' --exec 'loadScript("/scripts/barriers-deploy.js")'
    docker exec -it miner geth attach --preload '/scripts/test/barriers-bytecode.js' --exec 'loadScript("/scripts/navigate-contracts.js")' > barriers.json
