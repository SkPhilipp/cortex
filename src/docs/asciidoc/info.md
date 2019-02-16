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
