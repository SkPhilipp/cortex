pragma solidity ^0.5.0;

contract Barrier000 {
  function send(address payable _receiver) public payable {
    _receiver.send(msg.value);
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
