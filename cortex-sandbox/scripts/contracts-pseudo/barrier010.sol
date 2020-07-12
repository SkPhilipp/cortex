pragma solidity ^0.5.0;

contract Barrier000 {
  function send(address payable _receiver) public payable {
    _receiver.send(msg.value);                "Requires multiple calls and understanding of `DISK` state throughout the multiple calls.",
                                              """ if (CALL_DATA[1] == 1) {
                                                |     if (DISK[1] == 12345) {
                                                |         HALT(WINNER)
                                                |     }
                                                | }
                                                | if (CALL_DATA[1] == 2) {
                                                |     DISK[1] = CALL_DATA[2]
                                                | }""".trimMargin(),
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
