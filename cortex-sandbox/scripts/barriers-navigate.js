var costTransactionCreate = 21000;
var costContractCreate = 32000;
console.log("[");
var firstElement = true;
for (var blockIndex = 0; blockIndex <= eth.blockNumber; blockIndex++) {
	var block = eth.getBlock(blockIndex, true);
	block.transactions.forEach(function(transaction) {
		if (transaction.gas > costContractCreate + costTransactionCreate) {
			var receipt = eth.getTransactionReceipt(transaction.hash);
			var contractCode = eth.getCode(receipt.contractAddress);
			var contractStorage = [];
			for (var storageIndex = 0; storageIndex < 5; storageIndex++) {
				var contractStorageElement = eth.getStorageAt(receipt.contractAddress, storageIndex);
				contractStorage.push(contractStorageElement);
			}
			var contractBalanceWei = eth.getBalance(receipt.contractAddress);
			var logData = {
				contractAddress: receipt.contractAddress,
				contractCode: contractCode,
				contractStorage: contractStorage,
				contractBalanceWei: contractBalanceWei,
				transactionInput: transaction.input,
				transactionIdentifiedAs: BarriersBytecode.identify(transaction.input)
			};
			if(!firstElement) {
				console.log(",");
			}
			console.log(JSON.stringify(logData, null, 2));
			firstElement = false;
		}
	});
}
console.log("]");
console.log("__FILE_END__");
