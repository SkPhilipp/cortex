pragma solidity ^0.5.0;

/**
 * Matching hashed input with another hashed input.
 */
contract Barrier012 {
  function send(address payable receiver, uint256 amount, bytes32 inputA, bytes32 inputB) public payable {
    bytes32 hashA = keccak256(abi.encode(inputA));
    bytes32 hashB = keccak256(abi.encode(inputB));
    if(hashA == hashB) {
        receiver.send(amount);
    }
  }

  function setup() public payable returns (string memory thanks) {
    return "thanks";
  }
}
