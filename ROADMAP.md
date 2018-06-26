optimizations:
- downwards-NOOP-removal optimization strategy
- upwards-NOOP-removal optimization strategy
- bitwise operations optmimization strategy
- hash precalculation optimization strategy
- unused jump destination strategy

immediate:
- rename current tree-building classes to instruction-optimization
- better support for editing of streams
- allow optimizers to filter on noop-equivalent instructions
- allow optimizers to get an instruction's line number
- allow optimizers to receive only instructions of a specific type, no typeof
- EVM-based overflow configuration in ProgramBuilderFactory as well as related testcases
- EVM-based signed & unsigned math configuration in ProgramBuilderFactory as well as related testcases
- check for issues with Java's signed byte math (ie byte = 127 should be an unsigned int of 255)
- implement SHA3 (or a lazy-loaded proxy of it)
- ProgramBuilder with higher level instructions (such as FOR, WHILE, IF, ELSE.)
- try to find branchless methods of doing branched instructions, ideally, a generalized solution
- a fuzzer, for compiler as well as optimizer
- jump takes its destination parameter from the stack(edited)

post-tree-implementation:
- write-into-read simplifications where read is known to return the same results
- fixed-amount loop unrolling optimization
- mark self-contained instructions as being such; (example; [PUSH])
- mark self-contained tree nodes as being such; (example; [PUSH, PUSH, EQUALS], [PUSH, POP])
- implement a generic solving method for self-contained tree nodes (and remove obsolete'd instruction list optimizers)
- exit-only optimization strategies (code only performing stack math, reading, and logging ending in an EXIT or EXIT-equivalent is not interesting)
- optimization strategy to inline instructions from elsewhere in the code, allowing for further optimization passes (post-tree-implementation, this could be done transparently without memory overhead)
- optimization stratety to inline instructions from external codebases, allowing for further optimization passes (post-tree implementation, this could be done transparently without memory overhead)

constraint-implementation:
- described in detail at bytecode-analysis.git/README.md under "Backpropagation example"
- could have a cache or rainbow-table of instructions|nodes to their respective constraints & ideally their solution
- constraint solving mechanism; to convert a given a tree representation along with its execution context, known variables (&ranges) to all possible outcomes

post-constraint-implementation:
- constraint-based load-known optimization (much like value-based load-known optimization)
- constraint-based complexity
