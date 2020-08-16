function logger(error, result) {
	if (error) {
		console.error(error);
	} else {
		console.log(JSON.stringify(result));
	}
}

var abi = [{
	"constant": true,
	"inputs": [],
	"name": "storedData",
	"outputs": [{"name": "", "type": "uint256"}],
	"payable": false,
	"type": "function"
}, {
	"constant": false,
	"inputs": [{"name": "x", "type": "uint256"}],
	"name": "set",
	"outputs": [],
	"payable": false,
	"type": "function"
}, {
	"constant": true,
	"inputs": [],
	"name": "get",
	"outputs": [{"name": "retVal", "type": "uint256"}],
	"payable": false,
	"type": "function"
}, {
	"inputs": [{"name": "initVal", "type": "uint256"}],
	"payable": false,
	"type": "constructor"
}];

var contractAddress = "0x9466a8f6a39f6af1c1aceb8a2f48d06c482044df";

var contract = web3.eth.contract(abi).at(contractAddress);

web3.eth.sendTransaction({
	to: contractAddress,
	from: web3.eth.accounts[0],
	data: contract.set.getData(2)
}, logger);

web3.eth.call({
	to: contractAddress,
	from: web3.eth.accounts[0],
	data: contract.get.getData()
}, logger);

web3.eth.call({
	to: contractAddress,
	from: web3.eth.accounts[0],
	data: contract.storedData.getData()
}, logger);
