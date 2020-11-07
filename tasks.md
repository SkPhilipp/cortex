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

Some interesting early cases;
- `0xe28e72fcf78647adce1f1252f240bbfaebd63bcc`
- `0x258c09146b7a28dde8d3e230030e27643f91115f`
- `0x020522bf9b8ed6ff41e2fa6765a17e20e2767d64`
- `0x7153fdeef24e488af4dc001f620407764ee47747`
- `0xc4c51de1abf5d60dbd329ec0f999fd8f021ae9fc`
- `0xd79b4c6791784184e2755b2fc1659eaab0f80456`
- `0x7d692b829cb5a81b9e17066504143fc1b75e0c15`
- `0x2adfc2febf51d75d195ccd903251c099fdd22f20`

### Processing

- Implement multi-width instructions to replace NOOP in optimization processing.
- Check https://www.agner.org/optimize/
- Check https://llvm.org/docs/Passes.html
- Implementation of KnownProcessor and KnownJumpIfProcessor using Symbolic runner
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
- Verify runtime and potential of `LayeredVMOptimizer` and implement it for `BranchedMap` and `BranchedStack`

### Cortex Explore Strategies

Currently Explore continues until certain limits are reached. Alternative exploration & expression construction methods should be evaluated for usefulness;
- Implementation of an explore using Z3's mkITE depending on path Layered structure
- Implementation of an explore solving before reaching a target to verify whether paths are possible before continuing further
- Implementation of an explore which filters out paths that we can know beforehand are not targeted
- Make Expression references compatible with multiple program contexts

A model of paths through a program could be generated before exploring, filtering out uninteresting paths.

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

### Corpus

- Implement a Blueprint-style fuzzer
- Obtain a top N of common contracts and make them available for testing
