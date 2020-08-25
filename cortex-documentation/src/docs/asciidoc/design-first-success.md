- balance wasnt allocated for a barrier program
    ideally we should do getBalance until its positive

// TODO: Compare with setup-trace.json from here (0xba0bba40 is the setup function id)
programContext.callData[BigInteger.ZERO] = Expression.Value(BigInteger("ba0bba4000000000000000000000000000000000000000000000000000000000".deserializeBytes()).toLong())
SymbolicVirtualMachine(programContext)

- values are always stored as 64 bytes, for example PUSH 80 is
    0000000000000000000000000000000000000000000000000000000000000080

- the input data appears to must be ba0bba4
  this is loaded effectively as
    push ba0bba4000000000000000000000000000000000000000000000000000000000
  then it does
    push 30
    shift right
  the result is
    push 00000000000000000000000000000000000000000000000000000000ba0bba40

-       "op": "SHR",
        "stack": [
          "ba0bba4000000000000000000000000000000000000000000000000000000000",
          "00000000000000000000000000000000000000000000000000000000000000e0"
        ] (most recent value on the bottom)

- the post shift right call data is
    00000000000000000000000000000000000000000000000000000000ba0bba40
  currently this is displayed as a negative value and the prefix 0's are lost
  this is not correct
  the jump which compares this value is done at pc: 40

- these values are all higher than max long.. meaning Long should be entirely eliminated
    this includes optimizations in Expression, and pushing values should be done differently
    BigInteger is potentially not enough to work with this and it should be looked into
    how to translate this to Z3 (probably bit vectors)

[299] PUSH2 0x3039                                                              # 12345
[301] PUSH1 0x02                                                                # 2

[329] PUSH32 0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffcfc7 # -12345
[362] PUSH32 0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe # -2

the bytes cannot be interpreted as integer... it depends on how they are used
some operations will result in the same whether they'd been unsigned or signed (+, -)
which is why sdiv and div exist separately; from the bytes only we can't know whether something is signed or unsigned
which is why generally we should be treating things as unsigned, as only very specific instructions
indicate stuff to be signed

also during testing when compiling the signed versions of barrier contracts the
ending hash or nonsense added by the compilation tool contained _multiple_ immediately visible 5b's or better known as JUMP_DESTINATION
