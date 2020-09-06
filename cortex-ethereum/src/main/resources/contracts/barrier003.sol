pragma solidity ^0.5.0;

/**
 * Integer overflow.
 */
contract Barrier003 {
  function send(address payable receiver, uint256 amount, uint256 input) public payable {
    uint256 max = 2**256-1;
    if(input + max == 12345) {
      receiver.send(amount);
    }
  }

  function() external payable {
  }
}
