function run(networkAddress, bytecode) {
	var abi = [];
	var contract = eth.contract(abi);
	var output = contract.new(42, {
		from: eth.accounts[0],
		data: bytecode,
		gas: 0x400000
	});
	console.log(JSON.stringify(output));
	console.log("___END___");
}
