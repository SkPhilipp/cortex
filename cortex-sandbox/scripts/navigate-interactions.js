for (var blockIndex = 0; blockIndex <= eth.blockNumber; blockIndex++) {
	var block = eth.getBlock(blockIndex, true);
	block.transactions.forEach(function(transaction) {
		if (transaction.gas > 0) {
			var receipt = eth.getTransactionReceipt(transaction.hash);
			if (receipt.contractAddress == null) {
				var logData = {
					transactionInput: transaction.input,
					trace: debug.traceTransaction(transaction.hash)
				};
				console.log(JSON.stringify(logData, null, 2));
			}
		}
	});
}
