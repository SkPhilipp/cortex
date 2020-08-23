pragma solidity ^0.5.0;

/**
 * Requires understanding of preconfigured `DISK` state.
 */
contract Barrier009 {
  uint256 private data = 12345;

  function send(address payable receiver, uint256 amount, uint256 input) public payable {
    if(data == input) {
      receiver.send(amount);
   }
  }

  function setup() public payable returns (string memory thanks) {
    return "thanks";
  }
}
