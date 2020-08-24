var abi = [{
	"constant": false,
	"inputs": [],
	"name": "setup",
	"outputs": [{"name": "thanks", "type": "string"}],
	"payable": true,
	"stateMutability": "payable",
	"type": "function"
}, {
	"constant": false,
	"inputs": [{"name": "receiver", "type": "address"}, {"name": "amount", "type": "uint256"}],
	"name": "send",
	"outputs": [],
	"payable": true,
	"stateMutability": "payable",
	"type": "function"
}]

contractAddress = "0xece48018904ae7be77671d553a8a94662a0c4549";
console.log("current balance: " + eth.getBalance(contractAddress))
var contract = eth.contract(abi).at(contractAddress);

eth.sendTransaction({
	from: eth.accounts[0],
	to: contractAddress,
	value: "1000000000000000000",
	data: contract.send.getData(eth.accounts[0], 1000000000000000000),
	gas: 0x400000
});
