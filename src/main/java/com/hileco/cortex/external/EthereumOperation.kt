package com.hileco.cortex.external

enum class EthereumOperation(val code: Byte?, val inputBytes: Int = 0) {
    STOP("00".deserializeByte()),
    ADD("01".deserializeByte()),
    MUL("02".deserializeByte()),
    SUB("03".deserializeByte()),
    DIV("04".deserializeByte()),
    SDIV("05".deserializeByte()),
    MOD("06".deserializeByte()),
    SMOD("07".deserializeByte()),
    ADDMOD("08".deserializeByte()),
    MULMOD("09".deserializeByte()),
    EXP("0a".deserializeByte()),
    SIGNEXTEND("0b".deserializeByte()),
    LT("10".deserializeByte()),
    GT("11".deserializeByte()),
    SLT("12".deserializeByte()),
    SGT("13".deserializeByte()),
    EQ("14".deserializeByte()),
    ISZERO("15".deserializeByte()),
    AND("16".deserializeByte()),
    OR("17".deserializeByte()),
    XOR("18".deserializeByte()),
    NOT("19".deserializeByte()),
    BYTE("1a".deserializeByte()),
    SHL("1b".deserializeByte()),
    SHR("1c".deserializeByte()),
    SAR("1d".deserializeByte()),
    ROL("1e".deserializeByte()),
    ROR("1f".deserializeByte()),
    SHA3("20".deserializeByte()),
    ADDRESS("30".deserializeByte()),
    BALANCE("31".deserializeByte()),
    ORIGIN("32".deserializeByte()),
    CALLER("33".deserializeByte()),
    CALLVALUE("34".deserializeByte()),
    CALLDATALOAD("35".deserializeByte()),
    CALLDATASIZE("36".deserializeByte()),
    CALLDATACOPY("37".deserializeByte()),
    CODESIZE("38".deserializeByte()),
    CODECOPY("39".deserializeByte()),
    GASPRICE("3a".deserializeByte()),
    EXTCODESIZE("3b".deserializeByte()),
    EXTCODECOPY("3c".deserializeByte()),
    RETURNDATASIZE("3d".deserializeByte()),
    RETURNDATACOPY("3e".deserializeByte()),
    BLOCKHASH("40".deserializeByte()),
    COINBASE("41".deserializeByte()),
    TIMESTAMP("42".deserializeByte()),
    NUMBER("43".deserializeByte()),
    DIFFICULTY("44".deserializeByte()),
    GASLIMIT("45".deserializeByte()),
    POP("50".deserializeByte()),
    MLOAD("51".deserializeByte()),
    MSTORE("52".deserializeByte()),
    MSTORE8("53".deserializeByte()),
    SLOAD("54".deserializeByte()),
    SSTORE("55".deserializeByte()),
    JUMP("56".deserializeByte()),
    JUMPI("57".deserializeByte()),
    PC("58".deserializeByte()),
    MSIZE("59".deserializeByte()),
    GAS("5a".deserializeByte()),
    JUMPDEST("5b".deserializeByte()),
    LOG0("a0".deserializeByte()),
    LOG1("a1".deserializeByte()),
    LOG2("a2".deserializeByte()),
    LOG3("a3".deserializeByte()),
    LOG4("a4".deserializeByte()),
    SLOADBYTES("e1".deserializeByte()),
    SSTOREBYTES("e2".deserializeByte()),
    SSIZE("e3".deserializeByte()),
    CREATE("f0".deserializeByte()),
    CALL("f1".deserializeByte()),
    CALLCODE("f2".deserializeByte()),
    RETURN("f3".deserializeByte()),
    DELEGATECALL("f4".deserializeByte()),
    CALLBLACKBOX("f5".deserializeByte()),
    STATICCALL("fa".deserializeByte()),
    REVERT("fd".deserializeByte()),
    STOP2("fe".deserializeByte()),
    SUICIDE("ff".deserializeByte()),
    PUSH1("60".deserializeByte(), 1),
    PUSH2("61".deserializeByte(), 2),
    PUSH3("62".deserializeByte(), 3),
    PUSH4("63".deserializeByte(), 4),
    PUSH5("64".deserializeByte(), 5),
    PUSH6("65".deserializeByte(), 6),
    PUSH7("66".deserializeByte(), 7),
    PUSH8("67".deserializeByte(), 8),
    PUSH9("68".deserializeByte(), 9),
    PUSH10("69".deserializeByte(), 10),
    PUSH11("6a".deserializeByte(), 11),
    PUSH12("6b".deserializeByte(), 12),
    PUSH13("6c".deserializeByte(), 13),
    PUSH14("6d".deserializeByte(), 14),
    PUSH15("6e".deserializeByte(), 15),
    PUSH16("6f".deserializeByte(), 16),
    PUSH17("70".deserializeByte(), 17),
    PUSH18("71".deserializeByte(), 18),
    PUSH19("72".deserializeByte(), 19),
    PUSH20("73".deserializeByte(), 20),
    PUSH21("74".deserializeByte(), 21),
    PUSH22("75".deserializeByte(), 22),
    PUSH23("76".deserializeByte(), 23),
    PUSH24("77".deserializeByte(), 24),
    PUSH25("78".deserializeByte(), 25),
    PUSH26("79".deserializeByte(), 26),
    PUSH27("7a".deserializeByte(), 27),
    PUSH28("7b".deserializeByte(), 28),
    PUSH29("7c".deserializeByte(), 29),
    PUSH30("7d".deserializeByte(), 30),
    PUSH31("7e".deserializeByte(), 31),
    PUSH32("7f".deserializeByte(), 32),
    DUP1("80".deserializeByte()),
    DUP2("81".deserializeByte()),
    DUP3("82".deserializeByte()),
    DUP4("83".deserializeByte()),
    DUP5("84".deserializeByte()),
    DUP6("85".deserializeByte()),
    DUP7("86".deserializeByte()),
    DUP8("87".deserializeByte()),
    DUP9("88".deserializeByte()),
    DUP10("89".deserializeByte()),
    DUP11("8a".deserializeByte()),
    DUP12("8b".deserializeByte()),
    DUP13("8c".deserializeByte()),
    DUP14("8d".deserializeByte()),
    DUP15("8e".deserializeByte()),
    DUP16("8f".deserializeByte()),
    SWAP1("90".deserializeByte()),
    SWAP2("91".deserializeByte()),
    SWAP3("92".deserializeByte()),
    SWAP4("93".deserializeByte()),
    SWAP5("94".deserializeByte()),
    SWAP6("95".deserializeByte()),
    SWAP7("96".deserializeByte()),
    SWAP8("97".deserializeByte()),
    SWAP9("98".deserializeByte()),
    SWAP10("99".deserializeByte()),
    SWAP11("9a".deserializeByte()),
    SWAP12("9b".deserializeByte()),
    SWAP13("9c".deserializeByte()),
    SWAP14("9d".deserializeByte()),
    SWAP15("9e".deserializeByte()),
    SWAP16("9f".deserializeByte()),
    UNKNOWN(null);

    companion object {
        private val mapping: Map<Byte, EthereumOperation> = values().asSequence()
                .filter { it != UNKNOWN }
                .map { if (it.code == null) null else it.code to it }
                .filterNotNull()
                .toMap()

        fun ofCode(code: Byte): EthereumOperation? {
            return mapping[code]
        }
    }
}