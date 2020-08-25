:doctype: book
:icons: font
:source-highlighter: highlightjs
:snippets: ../../../build/generated-snippets
:nofooter:

= Cortex Roadmap

== Improvements

* Implement options for DIVIDE and MODULO to allow for signed and unsigned operation,
    specifically for 32 byte integers.
    Configure these new instructions in EthereumTranspiler for `SDIV` and `SMOD`.
* Implement composite instruction ADD-MODULO and MULTIPLY_MODULO, as operation-then-modulo.
    Configure these new instructions in EthereumTranspiler for `ADDMOD` and `MULMOD`.
* Implement instruction EXPONENT, to operate on unsigned 32 byte integers.
* Implement options for LESS_THAN and GREATER_THAN to allow for signed and unsigned operation,
    specifically for 32 byte integers.
    Configure these new instructions in EthereumTranspiler for `SLT` and `SGT`.
* Implement BYTE instruction, for retrieving a single byte from a 32 byte value.
* Implement SHIFT instruction, with options for logical or arithmetic, left or right bit shift.
    Configure this new instructions in EthereumTranspiler for `SHL`, `SHR` and `SAR`.
* Implement instruction BALANCE to retrieve the available funds of a given address.
    Configure this new instruction in EthereumTranspiler for `BALANCE`.
* Implement variable CALL_FUNDS containing the value of funds transferred by the current call.
    Configure this new variable in EthereumTranspiler for `CALLVALUE`.
* Implement variable CALL_DATA_SIZE containing the total size in bytes of the CALL_DATA program store zone.
    Configure this new variable in EthereumTranspiler for `CALLDATASIZE`.
* Implement instruction LOCAL_PROGRAM_COPY, as loads-then-saves for a given source offset, target offset, and length.
    Configure this new instruction in EthereumTranspiler for `CALLDATACOPY`.
* Implement composite instruction PROGRAM_COPY, as LOCAL_PROGRAM_COPY for a given address, source offset, target offset, and length.
    Configure this new instruction in EthereumTranspiler for `EXTCODECOPY`.
* Implement instruction PROGRAM_SIZE, to retrieve the size of a program at a given address.
    Configure this new instruction in EthereumTranspiler for `EXTCODESIZE`.
* Implement composite variable LOCAL_PROGRAM_SIZE, as PROGRAM_SIZE of the execution-local program.
    Configure this new variable in EthereumTranspiler for `CODESIZE`.
* Implement variable CALL_CYCLES containing the conceptually reserved execution cycles.
    Configure this new variable in EthereumTranspiler for `GASPRICE`.
* Implement RETURN_DATA as a new program-execution-local program store zone (such as MEMORY).
    RETURN_DATA can only be written to using CALL_RETURN, and is only available for reading
    from the context of the calling program, post-CALL.
    Configure COPY RETURN_DATA in EthereumTranspiler for `RETURNDATACOPY`.
* Implement instruction SIZE to retrieve the current size of a given program store zone.
    Configure SIZE RETURN_DATA in EthereumTranspiler for `RETURNDATASIZE`.
    Configure SIZE MEMORY in EthereumTranspiler for `MSIZE`.
* Implement variable INTEGRATION_ETHEREUM_BLOCK_HASH as an ethereum-specific variable.
    Configure this new variable in EthereumTranspiler for `BLOCKHASH`.
* Implement variable INTEGRATION_ETHEREUM_COINBASE as an ethereum-specific variable.
    Configure this new variable in EthereumTranspiler for `COINBASE`.
* Implement variable INTEGRATION_ETHEREUM_NUMBER as an ethereum-specific variable.
    Configure this new variable in EthereumTranspiler for `NUMBER`.
* Implement variable INTEGRATION_ETHEREUM_DIFFICULTY as an ethereum-specific variable.
    Configure this new variable in EthereumTranspiler for `DIFFICULTY`.
* Implement variable INTEGRATION_ETHEREUM_GASLIMIT as an ethereum-specific variable.
    Configure this new variable in EthereumTranspiler for `GASLIMIT`.
* Implement variable INTEGRATION_ETHEREUM_GAS as an ethereum-specific variable.
    Configure this new variable in EthereumTranspiler for `GAS`.
* Implement options for SAVE to allow for writing values smaller than the default size.
    Configure this new option in EthereumTranspiler for `MSTORE8`.
* Proper representation of Hash functions in solving layer
* A customizable strategy for finding successful SymbolicVirtualMachines, to mark them as such during execution
   - This should allow for further constraints to be applied (To for example control a CALLs'; `Equals(callAddressStackElement, Value(1234))`)
* Barrier program which calls itself
* Refactor symbolic references to allow named variables (such as start time), as well as program-context bound named variables (such as caller address)
