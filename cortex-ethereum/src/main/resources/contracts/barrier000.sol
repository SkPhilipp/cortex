pragma solidity ^0.5.0;

/**
 * Unconditional.
 */
contract Barrier000 {
  function send(address payable receiver, uint256 amount) public payable {
    receiver.send(amount);
  }

  function() external payable {
  }
}
