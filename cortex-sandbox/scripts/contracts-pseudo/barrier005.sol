pragma solidity ^0.5.0;

//"Requires more complex memory data flow analysis to solve functions.",
contract Barrier000 {
  function send(address payable _receiver) public payable {
    _receiver.send(msg.value);                """ FUNCTION square(N) {
                                                |     RETURN N * N
                                                | }
                                                | FUNCTION cube(N) {
                                                |     RETURN N * square(N)
                                                | }
                                                | IF(cube(CALL_DATA[1]) == 27) {
                                                |     HALT(WINNER)
                                                | }""".trimMargin(),
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
