function run(networkAddress, contractAddress) {
	var transaction = eth.sendTransaction({
		from: eth.accounts[0],
		to: contractAddress,
		value: "1000000000000000000"
	});
	console.log(JSON.stringify(transaction));
	console.log("___END___");
}
