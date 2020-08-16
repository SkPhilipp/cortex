function logger(error, result) {
	if (error) {
		console.error(error);
	} else {
		console.log(JSON.stringify(result));
	}
}

var abi = [];

BarriersBytecode.bytecodes.forEach(function(bytecode) {
	var contract = eth.contract(abi);
	contract.new(42, {
		from: eth.accounts[0],
		data: bytecode,
		gas: 0x400000
	}, logger);
})
