# Cortex

```
                         cortex      ,
              ,-.       _,---._ __  / \
             /  )    .-'       `./ /   \
            (  (   ,'            `/    /|
             \  `-"             \'\   / |
              `.              ,  \ \ /  |
               /`.          ,'-`----Y   |
              (            ;        |   '
              |  ,-.    ,-'         |  /
              |  | (   |     eth    | /
              )  |  \  `.___________|/
```

Smart contracts open up new attack surfaces.
Their machine-code, storage, and any interactions with them are public.
Cortex takes smart contracts apart, analyses them & exploits all of their weaknesses.

Smart contracts open up new attack surfaces. Their machine-code, storage, and any interactions with them are public. Cortex takes smart contracts apart, analyses them & tries to extract value from them.

## Hosting

### Server

See [install.sh](install.sh) for analysis host setup.

### CLI Interaction

- Add `cortex/cortex-processing/cortex` to your PATH.
- Start a local Geth instance & tunnel to the development MongoDB using `docker-compose up`

When these steps are completed, the `cortex` command can run against your blockchain and MongoDB of choice as such;

    # experiment with local barrier programs
    # (`cortex reset` if using a local database)
    cortex barriers-deploy
    cortex search --blocks 1000000
    cortex barriers-allocate --blocks 1000000
    cortex analyze --selection blocks --blocks 1000000
    cortex report --selection blocks --blocks 1000000

    # analyze the mainnet
    cortex search --block-network mainnet --block-start 3799000 --blocks 1000000 --threads 20
    cortex analyze --selection blocks --block-network mainnet --block-start 0 --blocks 100000
    
    # analyze a mainnet barrier barrier program
    cortex search --block-network mainnet --block-start 10809030
    cortex graph --selection address --program-network mainnet --program-address 0x41088ccccc467a384645794c54752e3f9d4a26fa
    cortex analyze --program-network mainnet --program-address 0x41088ccccc467a384645794c54752e3f9d4a26fa

    # analyze a mainnet program which could not be decompiled
    cortex graph --selection address --program-network mainnet --program-address 0xe28e72fcf78647adce1f1252f240bbfaebd63bcc
    cortex analyze --program-network mainnet --program-address 0xe28e72fcf78647adce1f1252f240bbfaebd63bcc

## Development

### New Instructions

Implementing an instruction is a fairly straightforward task.
To ensure an instruction is implemented as such that it can be used with;
- Concrete execution
- Symbolic execution
- Transpilers
- Barrier programs
Then refer to this guide.

First, ensure you have the correct specification for the instruction you would like to add.
Both https://ethereum.github.io/yellowpaper/paper.pdf and https://ethervm.io/ contain clear specifications.

To implement the instruction, follow these steps:
- Configure an instruction class in com.hileco.cortex.vm.instructions
- Implement the SMT solver translation as an com.hileco.cortex.symbolic.expressions.Expression
- Implement the mapping to an Expression in com.hileco.cortex.symbolic.ExpressionGenerator
- Implement the mapping to an Expression in com.hileco.cortex.symbolic.explore.SymbolicInstructionRunner
- Implement expression optimization in com.hileco.cortex.symbolic.ExpressionOptimizer (if possible)
- If it opens a new solving case with added complexity, implement it as a barrier program in Solidity. (See `barrier000.sol`)
- If the barrier program can be solved with symbolic explore, implement the solving in com.hileco.cortex.symbolic.explore.SymbolicProgramExplorerTest
- Register the instruction in com.hileco.cortex.ethereum.EthereumTranspiler

If needed you may locate unimplemented instructions which are used in the wild through the following command;

    cortex graph --selection blocks --block-network mainnet --block-start 0 --blocks 1000000000 | grep UNKNOWN_INSTRUCTION | grep -v '(UNKNOWN:' | sort -u

Note however that many contracts contain nonsense signatures at the end, these are currently graphed as instructions, as they may be JUMPable.
