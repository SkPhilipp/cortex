pragma solidity ^0.5.0;

//"Involves memory and loops, more complex data flow analysis.",
contract Barrier000 {
  function send(address payable _receiver) public payable {
    _receiver.send(msg.value);                """ VAR x = CALL_DATA[1]
                                                | VAR y = 0
                                                | WHILE(--x) {
                                                |     y++
                                                | }
                                                | IF(y == 5) {
                                                |     HALT(WINNER)
                                                | }""".trimMargin(),
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
