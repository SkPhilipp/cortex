# Cortex Sandbox

## Setup

Install Geth from https://gethstore.blob.core.windows.net/builds/geth-windows-amd64-1.9.15-0f77f34b.exe

Setup using:

    #!/usr/bin/env bash
    ls geth-installer.exe || curl https://gethstore.blob.core.windows.net/builds/geth-windows-amd64-1.9.15-0f77f34b.exe --output geth-installer.exe
    mkdir -p datadir
    # install Geth manually through ./geth-installer.exe
    geth --datadir datadir init genesis.json
    bootnode --genkey=boot.key

## Run

    #!/usr/bin/env bash
    bootnode --nodekey=boot.key
    BOOTNODE_URL=enode://832465276f89e0c4d1503c4b31f4815bbbef6d4f7cd25715b496ff1320440f7003bc1dad482e61212333c025efd3541b2bbeb714db3dea463c50827791319061@127.0.0.1:0?discport=30301
    PUBLIC_ADDRESS=0xe9f5df98462b9da8cc4707fca3bd1367562fbc5f
    geth --datadir datadir --networkid 1488 --bootnodes $BOOTNODE_URL --syncmode full --mine --minerthreads=1 --etherbase=$PUBLIC_ADDRESS

    geth attach 'ipc:\\.\pipe\geth.ipc'
    # > personal.newAccount("i laugh")
    # > web3.fromWei(eth.getBalance(eth.accounts[0]), "ether")
    # > loadScript("./storage-deploy.js")
    # > loadScript("./storage-call.js")
    # > loadScript("./storage-inspect.js")
    # > eth.getTransactionReceipt("0xcddeac3319fef65f5956c80d26fd1fbe120da691dff409a1875d19d03a0c4866")
    # {
    #   blockHash: "0xe733efdc38e24db444179dcfb0a3cdd920c3d347a513050b175fe7886f018009",
    #   blockNumber: 218,
    #   contractAddress: "0x7e30a7a21cd6e10787410a4e96f12d8f905ad1ed",
    #   cumulativeGasUsed: 144806,
    #   from: "0xe9f5df98462b9da8cc4707fca3bd1367562fbc5f",
    #   gasUsed: 144806,
    #   logs: [],
    #   logsBloom: "0x0000000000000000000000000...",
    #   root: "0x1788a357e392fca454313e1aa43d139592fd39c1056c0703a33b96c34116384f",
    #   to: null,
    #   transactionHash: "0xcddeac3319fef65f5956c80d26fd1fbe120da691dff409a1875d19d03a0c4866",
    #   transactionIndex: 0
    # }
