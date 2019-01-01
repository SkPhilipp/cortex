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

cortex attack --source src/test/resources/assembly/winner-basic.cxasm
cortex run --source src/test/resources/assembly/winner-basic.cxasm
cortex run --source src/test/resources/assembly/winner-basic.cxasm --call-data 1=24690
