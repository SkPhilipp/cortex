pragma solidity ^0.5.0;

contract Barrier000 {
  function send(address payable _receiver) public payable {
    _receiver.send(msg.value);                "Matching hashed input with an known hash.",
                                              """ VAR hash = 0x....5678
                                                | if (hash == HASH(CALL_DATA[1])) {
                                                |     HALT(WINNER)
                                                | }""".trimMargin(),
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
