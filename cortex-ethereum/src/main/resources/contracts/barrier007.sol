pragma solidity ^0.5.0;

/**
 * Contains infinite loops.
 */
contract Barrier007 {
  function send(address payable receiver, uint256 amount, uint256 inputA, uint256 inputB) public payable {
    if(inputA / 2 == 12345) {
      while(inputB % 500 == 8) {
      }
      while(inputB % 500 == 4) {
      }
      while(inputB % 500 == 2) {
      }
      receiver.send(amount);
   }
  }

  function setup() public payable returns (string memory thanks) {
    return "thanks";
  }
}
