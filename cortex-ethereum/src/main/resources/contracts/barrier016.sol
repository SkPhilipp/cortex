pragma solidity ^0.5.0;

/**
 * Comparing input with the hash of another input.
 */
contract Barrier016 {
  function send(address payable receiver, uint256 amount, bytes32 inputA, bytes32 inputB) public payable {
    bytes32 hashInputA = keccak256(abi.encode(inputA));
    if(hashInputA == inputB) {
        receiver.send(amount);
    }
  }

  function setup() public payable returns (string memory thanks) {
    return "thanks";
  }
}
