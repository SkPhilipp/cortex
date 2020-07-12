pragma solidity ^0.5.0;

//"Vulnerable to integer overflow.",
contract Barrier000 {
  function send(address payable _receiver) public payable {
    _receiver.send(msg.value);                """ IF(CALL_DATA[1] + ONE_BELOW_OVERFLOW_LIMIT == 12345) {
                                                |     HALT(WINNER)
                                                | }""".trimMargin(),
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
