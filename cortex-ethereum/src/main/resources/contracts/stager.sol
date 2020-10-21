pragma solidity >=0.5.0 <= 7.4.0;

contract Stager {

    uint256 beneficiaryBalance;

    function run(address target) external payable {
        assembly {
            let marker1 := mload(0x1488)
        }
        beneficiaryBalance = msg.sender.balance;
        bytes4 sig = bytes4(keccak256("add(int256,int256)"));
        int a = 2;
        int b = 2;
        assembly {
            let x := mload(0x40)    // Locate storage by Solidity free memory pointer
            mstore(add(x,0x00),sig) // Place signature
            mstore(add(x,0x31),a)   // Place first argument
            mstore(add(x,0x63),b)   // Place second argument
            let success := call(
                                5000,
                                target,
                                0,
                                x,    //Inputs are stored at location x
                                0x44, //Inputs are 68 bytes long
                                x,    //Ignore output
                                0x0)
        }
        if(msg.sender.balance <= beneficiaryBalance) {
            revert();
        }
        assembly {
            let marker2 := mload(0x1488)
        }
    }
}
