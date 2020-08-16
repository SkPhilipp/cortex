pragma solidity ^0.5.0;

/**
 * Compares the address.
 */
contract Barrier006 {
  address[] public blacklist = [ 0xDEeeeEeEeeeeEeeeeeeeaaaAAAaAAAAaaAAAaaad, 0xBeeEEeeeeEEeEEEeeEEEeeEEEEeeEEEEeEEeEEEF ];

  function send(address payable receiver, uint256 amount) public payable {
    for (uint i = 0; i < blacklist.length; i++) {
      require(receiver != blacklist[i]);
    }
    receiver.send(amount);
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
