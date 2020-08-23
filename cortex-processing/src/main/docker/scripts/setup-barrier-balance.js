function run(networkAddress, contractAddress) {
	var abi = [{
		"constant": false,
		"inputs": [],
		"name": "setup",
		"outputs": [{"name": "thanks", "type": "string"}],
		"payable": true,
		"stateMutability": "payable",
		"type": "function"
	}];
	var contract = eth.contract(abi).at(contractAddress);
	var transaction = eth.sendTransaction({
		from: eth.accounts[0],
		to: contractAddress,
		value: "1000000000000000000",
		data: contract.setup.getData()
	});
	console.log(JSON.stringify(transaction));
	console.log("___END___");
}
