pragma solidity ^0.5.0;

//"Requires constraints on the stack to solve, as to `CALL` the proper address.",
contract Barrier000 {
  function send(address payable _receiver) public payable {
    _receiver.send(msg.value);                """ IF(CALL_DATA[1] / 2 == 12345) {
                                                |     CALL(RECIPIENT_ADDRESS=CALL_DATA[2], VALUE_TRANSFERRED=1, 0, 0, 0, 0)
                                                | }""".trimMargin(),
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
