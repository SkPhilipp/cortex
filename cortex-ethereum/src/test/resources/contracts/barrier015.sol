pragma solidity ^0.5.0;

/**
 * Accidental use of a hash as a condition.
 */
contract Barrier015 {
  function send(address payable receiver, uint256 amount, uint256 inputA, uint256 inputB) public payable {
    if(keccak256(abi.encode(inputA == inputB)) > 0) {
        receiver.send(amount);
    }
  }

  function setup() public payable returns (string memory thanks) {
    return "thanks";
  }
}
