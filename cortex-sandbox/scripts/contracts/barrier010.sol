pragma solidity ^0.5.0;

/**
 * Requires multiple calls and understanding of `DISK` state throughout the multiple calls.
 */
contract Barrier010 {
  uint256 private data = 0;

  function send(address payable receiver, uint256 amount) public payable {
    if(data == 12345) {
      receiver.send(amount);
   }
  }

  function configure(uint256 input) public {
    data = input;
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
