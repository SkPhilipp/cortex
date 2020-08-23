pragma solidity ^0.5.0;

/**
 * Math on multiple inputs.
 */
contract Barrier002 {
  function send(address payable receiver, uint256 amount, uint256 inputA, uint256 inputB) public payable {
    if(inputA / 2 == 12345) {
        if(inputB % 500 == 12) {
            receiver.send(amount);
        }
    }
  }

  function setup() public payable returns (string memory thanks) {
    return "thanks";
  }
}
