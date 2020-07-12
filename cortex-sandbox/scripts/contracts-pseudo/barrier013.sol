pragma solidity ^0.5.0;

contract Barrier000 {
  function send(address payable _receiver) public payable {
    _receiver.send(msg.value);                "Matching input with a hashed value.",
                                              """ VAR hash = HASH(1234)
                                                | if (hash == CALL_DATA[1]) {
                                                |     HALT(WINNER)
                                                | }""".trimMargin(),
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
