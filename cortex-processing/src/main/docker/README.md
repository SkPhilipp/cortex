# Cortex Processing Sandbox

Cortex Processing Sandbox is a basic setup to allow interaction with a local Ethereum blockchain through Geth.

## Run

    docker-compose up

To interact with the chain enter the `miner` docker service with `geth` as such;

    docker exec -it miner geth attach --exec 'eth.accounts'

Scripts in the `./scripts` directory can be launched as such;

    docker exec -it miner geth attach --preload '/scripts/test/barriers-bytecode.js' --exec 'loadScript("/scripts/barriers-deploy.js")'
    docker exec -it miner geth attach --preload '/scripts/test/barriers-bytecode.js' --exec 'loadScript("/scripts/navigate-contracts.js")' > barriers.json
