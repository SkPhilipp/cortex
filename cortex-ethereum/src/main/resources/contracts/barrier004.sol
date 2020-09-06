pragma solidity ^0.5.0;

/**
 * Involves memory and loops, more complex data flow analysis.
 */
contract Barrier004 {
  function send(address payable receiver, uint256 amount, uint256 x) public payable {
    uint256 y = 0;
    while(--x > 0) {
        y++;
    }
    if(y == 5) {
      receiver.send(amount);
    }
  }

  function() external payable {
  }
}
