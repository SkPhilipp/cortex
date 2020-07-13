pragma solidity ^0.5.0;

/**
 * Matching input with a hashed value.
 */
contract Barrier013 {
  function send(address payable receiver, uint256 amount, bytes32 input) public payable {
    bytes32 hashKnown = keccak256(abi.encode(1234));
    if(hashKnown == input) {
      receiver.send(amount);
    }
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
