pragma solidity ^0.5.0;

/**
 * Requires more complex memory data flow analysis to solve functions.
 */
contract Barrier005 {
  function square(uint256 value) private returns (uint256 output) {
    return value * value;
  }

  function cube(uint256 value) private returns (uint256 output) {
    return value * square(value);
  }

  function send(address payable receiver, uint256 amount, uint256 input) public payable {
    if(cube(input) == 27) {
      receiver.send(amount);
    }
  }

  function() external payable {
  }
}
