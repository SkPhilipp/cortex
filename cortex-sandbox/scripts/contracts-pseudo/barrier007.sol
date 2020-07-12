pragma solidity ^0.5.0;

contract Barrier000 {
  function send(address payable _receiver) public payable {
    _receiver.send(msg.value);                "Contains an infinite loop.",
                                              """ WHILE(CALL_DATA[1] / 2 == 12345) {
                                                |     WHILE(CALL_DATA[2] % 500 == 12) {
                                                |     }
                                                |     HALT(WINNER)
                                                | }""".trimMargin(),
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
