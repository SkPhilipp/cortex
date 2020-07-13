# Cortex Sandbox

Cortex Sandbox is a basic setup to allow interaction with a local Ethereum blockchain through Geth.
The concept for this sandbox is to allow barrier programs to be deployed and interacted with.
This will allow Cortex to run against this local Ethereum blockchain, discover active contracts and perform verified attacks against them. 

## Setup

The Docker Compose configuration starts `bootnode` instance and a `geth` instance configured to mine for `eth.accounts[0]`.

    docker build . -t cortex-sandbox
    docker-compose up

The first time this is executed a new boot key, account key, and `geth` `datadir` will be set up in the local `./volume` directory.
Subsequent runs will reuse existing the keys & blockchain. To reset this simply clear out the `volume` directory.
The password for the local Ethereum blockchain account is `i laugh`.

## Run

To interact with the chain enter the `docker-compose` miner service with `geth` as such;

    docker-compose exec miner geth attach --exec 'eth.accounts'

Scripts in the `./scripts` directory can be launched as such;

    docker-compose exec miner geth attach --preload '/scripts/barriers-bytecode.js' --exec 'loadScript("/scripts/barriers-deploy.js")'
    docker-compose exec miner geth attach --preload '/scripts/barriers-bytecode.js' --exec 'loadScript("/scripts/barriers-navigate.js")' > barriers.json

Note that barrier programs can be compiled with `solc`:

    cd scripts
    docker run -v "$(pwd):/volume" -w /volume ethereum/solc:0.5.7 --bin contracts/barrier000.sol
