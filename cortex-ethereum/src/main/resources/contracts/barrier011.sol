pragma solidity ^0.5.0;

/**
 * Matching hashed input with a hashed value.
 */
contract Barrier011 {
  function send(address payable receiver, uint256 amount, bytes32 input) public payable {
    bytes32 hashKnown = keccak256(abi.encode(1234));
    bytes32 hashInput = keccak256(abi.encode(input));
    if(hashKnown == hashInput) {
      receiver.send(amount);
    }
  }

  function() external payable {
  }
}
