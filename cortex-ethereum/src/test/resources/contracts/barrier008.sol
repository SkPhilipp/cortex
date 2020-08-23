pragma solidity ^0.5.0;

/**
 * Represents the mechanics of a primitive PRNG.
 */
contract Barrier008 {
  function send(address payable receiver, uint256 amount, bytes32 input) public payable {
    bytes32 addressHash = keccak256(abi.encode(address(this)));
    bytes32 timestampHash = keccak256(abi.encode(block.timestamp));
    bytes32 combinedHash = keccak256(abi.encode(addressHash, timestampHash));
    if(combinedHash == input) {
      receiver.send(amount);
   }
  }

  function setup() public payable returns (string memory thanks) {
    return "thanks";
  }
}
