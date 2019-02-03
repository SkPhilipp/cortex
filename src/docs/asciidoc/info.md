# Fuzzer Design

## Levels

Generating code should be in levels of detail, along with desired constraints as such:

Kind = What kind of thing it is, for example:
       - a Program may be a "Mathematics Library"
       - a Variable may be a "Map"
Scope = How long the thing lasts;
       - a Variable may only exist within a Function, or within a Program, or even remain on Disk
       - a Program may be created dynamically by anonther Program
Accessibility = How accessible the thing is;
       - a Function may be public, or private
       - a Program may be public, or private
       - a Variable may be directly writeable

| Level      | Defines    | Kind | Scope | Accessibility |
|------------|------------|------|-------|---------------|
| Atlas      | Programs   | x    | x     | x             |
| Programs   | Functions  | x    |       | x             |
| Programs   | Variables  | x    | x     | x             |
| Functions  | Statements | x    |       |               |
| Functions  | Variables  | x    | x     |               |
| Statements | Statements | x    |       |               |

Defining these allows for constraints to be added, where a certain Definition must be of a given Kind, Scope or Accessibility, for example;
- Require that only one Program must exist, which is of kind "WALLET" owning at least 5 amount of Currency
- Require that at least one "Publicly Accessible" Function must exist, containing a Statement of type "LOOP\_XL" followed by a Statement of type "VULNERABLE\_CALL"

These are essentially templates which can be expanded with additional definitions.

----

make a flow processor that maps better, as defined in this document
(for the current fuzzer this is however not yet relevant i believe)

from a block perspective one could have instructions as such:

BLOCK A JUMP_DESTINATION

BLOCK B JUMP_DESTINATION -->|
        LOAD CALL_DATA      |
        PUSH 10             |
        JUMP_IF <-- --------|

BLOCK C JUMP_DESTINATION -->|
        PUSH 20             |
        JUMP <-- -----------|

BLOCK D JUMP_DESTINATION -->|
               ...          |

currently this is mapped essentialy from either an implicit PROGRAM_START or explicit JUMP_DESTINATION
to any instruction which changes the flow of the code ie JUMP_IF, JUMP, EXIT, HALT, CALL, CALL_RETURN
however, blocks may "inherit" the flow of another block when they themselves do not end in an explicit
nonconditional instruction which changes the flow of the code ie. JUMP, EXIT, HALT, CALL_RETURN, which
would make the flow mapping look somewhat like such:

JUMP_DESTINATION -->|
JUMP_DESTINATION -->|
LOAD CALL_DATA      |
PUSH 10             |
JUMP_IF <-- --------|
JUMP_DESTINATION -->|
PUSH 20             |
JUMP <-- -----------|

JUMP_DESTINATION -->|
       ...          |

in this view:
- BLOCK A's start allows for flows to BLOCK B's jumping instructions (as BLOCK A contains NO unconditional flow instructions)
- BLOCK A's start allows for flows to BLOCK C's jumping instructions (as BLOCK A AND BLOCK B contain NO unconditional flow instructions)
- BLOCK B's start allows for flows to BLOCK C's jumping instructions (as BLOCK B contains NO unconditional flow instructions)

----

vi HelloWorld.java
javac HelloWorld.java
native-image HelloWorld
time java HelloWorld
time ./helloworld

----

Make processors pure instead of linking them to objects on the graph
Remove the need for EdgeUtility by applying Sequences and extension methods to sequences of GraphNode
Remove the need for an AtomicReference wrapper around a GraphNode instruction
Concept for instructions which modify the instruction position other than ++

----

cortex attack --program src/test/resources/assembly/winner-basic.cxasm
cortex attack --program @1002
cortex run --program src/test/resources/assembly/winner-basic.cxasm
cortex run --program @1001
cortex run --program src/test/resources/assembly/winner-basic.cxasm --call-data 1=24690

> cortex attack --program src/test/resources/assembly/winner-basic.cxasm
[{"possibleValues":{"CALL_DATA[1]":24690},"solvable":true}]
> cortex run --program src/test/resources/assembly/winner-basic.cxasm --call-data 1=24690
Exception at position 9, reason: WINNER
> cortex optimize --program src/test/resources/assembly/optimize-basic.cxasm

----

EXP(0x0a, "Exponential operation"),
ADDRESS(0x30, "Get address of currently executing account"),
BALANCE(0x31, "Get balance of the given account"),
ORIGIN(0x32, "Get execution origination address"),
CALLER(0x33, "Get caller address. This is the address of the account that is directly responsible for this execution"),
CALLVALUE(0x34, "Get deposited value by the instruction/transaction responsible for this execution"),
CALLDATALOAD(0x35, "Get input data of current environment"),
CALLDATASIZE(0x36, "Get size of input data in current environment"),
CALLDATACOPY(0x37, "Copy input data in current environment to memory This pertains to the input data passed with the message call instruction or transaction"),
CODESIZE(0x38, "Get size of code running in current environment"),
CODECOPY(0x39, "Copy code running in current environment to memory"),
GASPRICE(0x3a, "Get price of gas in current environment"),
EXTCODESIZE(0x3b, "Get size of an account's code"),
EXTCODECOPY(0x3c, "Copy an account's code to memory"),
RETURNDATASIZE(0x3d, "?"),
RETURNDATACOPY(0x3e, "?"),
BLOCKHASH(0x40, "Get the hash of one of the 256 most recent complete blocks"),
COINBASE(0x41, "Get the block's beneficiary address"),
TIMESTAMP(0x42, "Get the block's timestamp"),
NUMBER(0x43, "Get the block's number"),
DIFFICULTY(0x44, "Get the block's difficulty"),
GASLIMIT(0x45, "Get the block's gas limit"),
PC(0x58, "Get the value of the program counter prior to the increment"),
MSIZE(0x59, "Get the size of active memory in bytes"),
GAS(0x5a, "Get the amount of available gas, including the corresponding reduction"),
CREATE(0xf0, "Create a new account with associated code"),
CALL(0xf1, "Message-call into an account"),
CALLCODE(0xf2, "Message-call into this account with alternative account's code"),
DELEGATECALL(0xf4, "Message-call into this account with an alternative account's code, but persisting the current values for `sender` and `value`"),
STATICCALL(0xfa, "Static message-call into an account."),
REVERT(0xfd, " Halt execution reverting state changes but returning data and remaining gas."),
INVALID(0xfe, "Designated invalid instruction."),
SELFDESTRUCT(0xff, "Halt execution and register account for later deletion");

----

processor for extracting constants; replacing push instructions with their respective variable instructions
to allow for deduplication of contracts programs which have precalculated variables embedded in their code

----

{
    "runningPrograms": [
        {
            "programReference": "1001"
            "instructionsExecuted": 0,
            "instructionPosition": 0,
            "instructionsLimit": 0,
            "stack": ["0x00"],
            "memory": {"0x00": "0x00"},
            "callData": {"0x00": "0x00"},
            "returnDataOffset": "",
            "returnDataSize": ""
        }
    ],
    < multiple implementations here - one for in-memory analysis & one for on-database >
    "availablePrograms": {
        "<address>": {
            "storage": {"0x00": "0x00"},
            "transfers": []
        }
    },
    "overflowLimit": 0,
    "underflowLimit": 0,
    "stackLimit": 0,
    "instructionsExecuted": 0,
    "instructionLimit": 0
}
