function run(networkAddress, contractAddress) {
	// call using method id (;the first 4 bytes of SHA256 KECCAK of the ASCII signature setup())

	// current:
	// com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'Error': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')
	//  at [Source: (String)"Error: execution reverted
	// 	at web3.js:6347:37(47)
	// 	at web3.js:5081:62(37)
	// 	at run (/scripts/setup-barrier-balance.js:3:39(14))
	// 	at <eval>:1:52(9)
	var transaction = eth.sendTransaction({from: eth.accounts[0], to: contractAddress, value: "1000000000000000000"})
	console.log(JSON.stringify(transaction));
	console.log("___END___");
}
