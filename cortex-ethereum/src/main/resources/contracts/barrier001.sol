pragma solidity ^0.5.0;

/**
 * Math on a single input.
 */
contract Barrier001 {
  function send(address payable receiver, uint256 amount, uint256 input) public payable {
    if(input / 2 == 12345) {
        receiver.send(amount);
    }
  }

  function setup() public payable returns (string memory thanks) {
    return "thanks";
  }
}
