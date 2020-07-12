pragma solidity ^0.5.0;

//"Basic math on multiple inputs.",
contract Barrier000 {
  function send(address payable _receiver) public payable {
    _receiver.send(msg.value);                """ IF(CALL_DATA[1] / 2 == 12345) {
                                                |     IF(CALL_DATA[2] % 500 == 12) {
                                                |         HALT(WINNER)
                                                |     }
                                                | }""".trimMargin(),
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
