package com.hileco.cortex.processing.setup

// docker exec miner geth attach --preload '/scripts/test/barriers-bytecode.js' --exec 'loadScript("/scripts/test/navigate-contracts.js")'
// # eth.getBalance("0xce609b6da065748c98aa24481dffd88ba2242256") // contract   0
// # eth.getBalance(eth.accounts[0])                              // developer  1.15e+77
// eth.sendTransaction({from: eth.accounts[0], to: "0xce609b6da065748c98aa24481dffd88ba2242256", value: "1000000000000000000"})
// Error: execution reverted
//        at web3.js:6347:37(47)
//        at web3.js:5081:62(37)
//        at <eval>:1:20(13)
