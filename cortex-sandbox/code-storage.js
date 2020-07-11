web3.personal.unlockAccount(eth.accounts[0], "i laugh", 15000);

var contractAddress = "0x7e30a7a21cd6e10787410a4e96f12d8f905ad1ed";
var result = web3.eth.getCode(contractAddress);
console.log(result);

for (var index = 0; index < 10; index++) {
	console.log(index + ': ' + web3.eth.getStorageAt(contractAddress, index))
}
