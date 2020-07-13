pragma solidity ^0.5.0;

/**
 * Matching hashed input with an known hash.
 */
contract Barrier014 {
  function send(address payable receiver, uint256 amount, bytes32 input) public payable {
    bytes32 hashKnown = 0x83779bec9f11eb8eac6af7420d17b34344be78d2c0f07a473110d2de17d44737; // keccak256('i laugh')
    bytes32 hashInput = keccak256(abi.encode(input));
    if(hashKnown == hashInput) {
        receiver.send(amount);
    }
  }

  function setup() public returns (string memory thanks) {
    return "thanks";
  }
}
