### Quality

- Implement tests for Symbolic bitwise operations, they should be visible by in-code TODOs
- Implement optimizations for Symbolic bitwise operations, along with their tests
- Implement a result verifier for Symbolic Barrier Explore tests (and related Symbolic Barrier tests)
- Implement a barrier program that sends the full balance to the message sender (even simpler than barrier 000)
- Add assertions to verify that currently solved barrier programs stay as such
- Verify that Symbolic Explore Barriers that remain unsolved are also unsolved in other Barrier tests

### Performance

- Check if there are potential Z3 strategies we can add https://www.cs.tau.ac.il/~msagiv/courses/asv/z3py/strategies-examples.htm
- Verify whether soft assertions can help Z3 find better solutions

### Visualization

- Design and implement a new visualization scheme

### Cases

- Analyze & manually inspect `0xe28e72fcf78647adce1f1252f240bbfaebd63bcc`

### Processing

- Split up Cortex Processing
  - A component to only parse the chain and stay up to date for queries on it
  - A component to filter programs and mark those that are of interest
  - A component to solve programs marked by the previous stage, generating analysis reports & potential solutions
  - A component which submits verified solutions back into the chain
  Components would lock what they are working on, so that parallel work will become easy to implement

- Implement multi-width instructions to replace NOOP in optimization processing.
- Check https://www.agner.org/optimize/
- Check https://llvm.org/docs/Passes.html
- Implementation of InliningProcessor
- Implementation of InstructionHoistProcessor
- Implementation of DeadPathConstraintProcessor
- Implementation of DeadSaveProcessor
- Implementation of DeadLoadProcessor
- Implementation of KnownProcessor for multi-value results
- Implementation of KnownProcessor for multiple blocks

### Layer Structures Optimizations

- Implement a bloom filter in front of Layered Set and Layered Map and verify its performance
- Implement an optimal initial List size for Map-, Set- & Stack-Layers
- Implement an optimization strategy which Layered tree structures as such:
  - Layer "Upwards": A layer merges with its parent as such that it could reference it's grandparent as its parent instead
  - Layer "Downwards": A layer merges with all its children as such that they could reference it's parent as their parent instead
  - Entry "Upwards": A layer merges a single entry with its parent as such that the entry does not need to remain in the current layer
  - Entry "Downwards": A layer merges a single entry with all its children as such that the entry does not need to remain in the current layer
    (When an Entry is accessed often, it may be merged Downwards)
  These optimizations should be implemented separately from the Layered structures
  These optimizations may be dependent on minimum layer width (hard) which would merge layers on creation unless the parent is of a certain width
  These optimizations may be dependent on maximum depth (soft) which when reached could recalibrate the minimum layer size

### Cortex Explore Strategies

Currently Explore continues until certain limits are reached. Alternative exploration & expression construction methods should be evaluated for usefulness;
- Implementation of an explore using Z3's mkITE depending on path Layered structure
- Implementation of an explore solving before reaching a target to verify whether paths are possible before continuing further
- Implementation of an explore which filters out paths that we can know beforehand are not targeted

### Solver Tactics & Strategies

- Test variations of solver tactics and strategies and implement them to find effective strategies
- Implement a custom strategy for SELFDESTRUCT

### Tracking Expression Optimizer

- The expression optimizer can track variable constraints, simple inference may be possible for variables which are
  set to a fixed value. This allows for optimizations replacing any occurrence of the variable with the fixe value.
  Through these optimizations, we may optimize and evaluate constraints on JUMP_IF to verify whether a path is possible.
- The expression optimize should skip re-optimizing expression subtrees which have already been optimized.

### Stager Program

Implement fully a customizable Stager program, which should perform a call to a target smart contract, verifying afterwards that
the interactions was a success. An initial implementation can be found in stager.sol and stager-snippet.bin.

### Barrier Programs

Implement https://swcregistry.io/ as barrier programs.

### Ethereum Integration

* Complete the implementation of the following instructions and variables;
    - BALANCE
    - BLOCK_HASH
    - BYTE
    - CALL_CODE
    - COPY
    - EXTERNAL_COPY
    - SHIFT_LEFT
    - SIGN_EXTEND
    - signed GREATER_THAN
    - signed DIVIDE
    - signed LESS_THAN
    - signed MODULO
    - PROGRAM_MEMORY_SIZE
    - PROGRAM_CODE_SIZE
    - TRANSACTION_GAS_REMAINING
    - TRANSACTION_GAS_PRICE
    - BLOCK_COINBASE
    - BLOCK_TIMESTAMP
    - BLOCK_NUMBER
    - BLOCK_DIFFICULTY
    - BLOCK_GAS_LIMIT

* Implement options for DIVIDE and MODULO to allow for signed and unsigned operation.
    Configure these new instructions in EthereumTranspiler for `SDIV` and `SMOD`.
* Implement composite instruction ADD-MODULO and MULTIPLY_MODULO.
    Configure these new instructions in EthereumTranspiler for `ADDMOD` and `MULMOD`.
* Implement options for LESS_THAN and GREATER_THAN to allow for signed and unsigned operation.
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
* Barrier program which calls itself
