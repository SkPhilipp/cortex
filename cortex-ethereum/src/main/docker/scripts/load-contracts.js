function run(networkAddress, blockNumberStart, blockNumberEnd) {
	var costTransactionCreate = 21000;
	var costContractCreate = 32000;
	console.log("[");
	var firstElement = true;
	for (var blockNumber = blockNumberStart; blockNumber <= blockNumberEnd; blockNumber++) {
		var block = eth.getBlock(blockNumber, true);
		block.transactions.forEach(function(transaction) {
			if (transaction.gas > costContractCreate + costTransactionCreate) {
				var receipt = eth.getTransactionReceipt(transaction.hash);
				var contractCode = eth.getCode(receipt.contractAddress);
				var contractBalanceWei = eth.getBalance(receipt.contractAddress);
				var logData = {
					transactionHash: transaction.hash,
					bytecode: contractCode,
					address: receipt.contractAddress,
					balance: contractBalanceWei
				};
				if (!firstElement) {
					console.log(",");
				}
				console.log(JSON.stringify(logData));
				firstElement = false;
			}
		});
	}
	console.log("]");
	console.log("___END___");
}
