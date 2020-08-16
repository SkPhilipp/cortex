function run() {
	console.log(JSON.stringify(
		{
			"latestBlock": eth.blockNumber
		}
	));
	console.log("___END___");
}
