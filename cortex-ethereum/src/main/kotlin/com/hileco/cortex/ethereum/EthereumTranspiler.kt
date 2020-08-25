package com.hileco.cortex.ethereum

import com.hileco.cortex.collections.serialize
import com.hileco.cortex.vm.ProgramException.Reason.REVERT
import com.hileco.cortex.vm.ProgramException.Reason.UNKNOWN_INSTRUCTION
import com.hileco.cortex.vm.ProgramStoreZone.*
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.bits.*
import com.hileco.cortex.vm.instructions.calls.CALL
import com.hileco.cortex.vm.instructions.calls.CALL_RETURN
import com.hileco.cortex.vm.instructions.conditions.EQUALS
import com.hileco.cortex.vm.instructions.conditions.GREATER_THAN
import com.hileco.cortex.vm.instructions.conditions.IS_ZERO
import com.hileco.cortex.vm.instructions.conditions.LESS_THAN
import com.hileco.cortex.vm.instructions.debug.DROP
import com.hileco.cortex.vm.instructions.debug.HALT
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.io.SAVE
import com.hileco.cortex.vm.instructions.jumps.EXIT
import com.hileco.cortex.vm.instructions.jumps.JUMP
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.math.*
import com.hileco.cortex.vm.instructions.stack.*
import com.hileco.cortex.vm.instructions.stack.ExecutionVariable.*

class EthereumTranspiler {
    private fun map(ethereumInstruction: EthereumInstruction): Instruction {
        return when (ethereumInstruction.operation) {
            EthereumOperation.STOP -> EXIT()
            EthereumOperation.ADD -> ADD()
            EthereumOperation.MUL -> MULTIPLY()
            EthereumOperation.SUB -> SUBTRACT()
            EthereumOperation.DIV -> DIVIDE()
            EthereumOperation.SDIV -> HALT(UNKNOWN_INSTRUCTION, "SDIV")
            EthereumOperation.MOD -> MODULO()
            EthereumOperation.SMOD -> HALT(UNKNOWN_INSTRUCTION, "SMOD")
            EthereumOperation.ADDMOD -> HALT(UNKNOWN_INSTRUCTION, "ADDMOD")
            EthereumOperation.MULMOD -> HALT(UNKNOWN_INSTRUCTION, "MULMOD")
            EthereumOperation.EXP -> EXPONENT()
            EthereumOperation.SIGNEXTEND -> HALT(UNKNOWN_INSTRUCTION, "SIGNEXTEND")
            EthereumOperation.LT -> LESS_THAN()
            EthereumOperation.GT -> GREATER_THAN()
            EthereumOperation.SLT -> HALT(UNKNOWN_INSTRUCTION, "SLT")
            EthereumOperation.SGT -> HALT(UNKNOWN_INSTRUCTION, "SGT")
            EthereumOperation.EQ -> EQUALS()
            EthereumOperation.ISZERO -> IS_ZERO()
            EthereumOperation.AND -> BITWISE_AND()
            EthereumOperation.OR -> BITWISE_OR()
            EthereumOperation.XOR -> BITWISE_XOR()
            EthereumOperation.NOT -> BITWISE_NOT()
            EthereumOperation.BYTE -> HALT(UNKNOWN_INSTRUCTION, "BYTE")
            EthereumOperation.SHL -> HALT(UNKNOWN_INSTRUCTION, "SHL")
            EthereumOperation.SHR -> SHIFT_RIGHT()
            EthereumOperation.SAR -> HALT(UNKNOWN_INSTRUCTION, "SAR")
            EthereumOperation.SHA3 -> HASH("SHA-256")
            EthereumOperation.ADDRESS -> VARIABLE(ADDRESS_SELF)
            EthereumOperation.BALANCE -> HALT(UNKNOWN_INSTRUCTION, "BALANCE")
            EthereumOperation.ORIGIN -> VARIABLE(ADDRESS_ORIGIN)
            EthereumOperation.CALLER -> VARIABLE(ADDRESS_CALLER)
            EthereumOperation.CALLVALUE -> VARIABLE(TRANSACTION_FUNDS)
            EthereumOperation.CALLDATALOAD -> LOAD(CALL_DATA)
            EthereumOperation.CALLDATASIZE -> VARIABLE(CALL_DATA_SIZE)
            EthereumOperation.CALLDATACOPY -> HALT(UNKNOWN_INSTRUCTION, "CALLDATACOPY")
            EthereumOperation.CODESIZE -> HALT(UNKNOWN_INSTRUCTION, "CODESIZE")
            EthereumOperation.CODECOPY -> HALT(UNKNOWN_INSTRUCTION, "CODECOPY")
            EthereumOperation.GASPRICE -> HALT(UNKNOWN_INSTRUCTION, "GASPRICE")
            EthereumOperation.EXTCODESIZE -> HALT(UNKNOWN_INSTRUCTION, "EXTCODESIZE")
            EthereumOperation.EXTCODECOPY -> HALT(UNKNOWN_INSTRUCTION, "EXTCODECOPY")
            EthereumOperation.RETURNDATASIZE -> HALT(UNKNOWN_INSTRUCTION, "RETURNDATASIZE")
            EthereumOperation.RETURNDATACOPY -> HALT(UNKNOWN_INSTRUCTION, "RETURNDATACOPY")
            EthereumOperation.BLOCKHASH -> HALT(UNKNOWN_INSTRUCTION, "BLOCKHASH")
            EthereumOperation.COINBASE -> HALT(UNKNOWN_INSTRUCTION, "COINBASE")
            EthereumOperation.TIMESTAMP -> VARIABLE(START_TIME)
            EthereumOperation.NUMBER -> HALT(UNKNOWN_INSTRUCTION, "NUMBER")
            EthereumOperation.DIFFICULTY -> HALT(UNKNOWN_INSTRUCTION, "DIFFICULTY")
            EthereumOperation.GASLIMIT -> HALT(UNKNOWN_INSTRUCTION, "GASLIMIT")
            EthereumOperation.POP -> POP()
            EthereumOperation.MLOAD -> LOAD(MEMORY)
            EthereumOperation.MSTORE -> SAVE(MEMORY)
            EthereumOperation.MSTORE8 -> HALT(UNKNOWN_INSTRUCTION, "MSTORE8")
            EthereumOperation.SLOAD -> LOAD(DISK)
            EthereumOperation.SSTORE -> SAVE(DISK)
            EthereumOperation.JUMP -> JUMP()
            EthereumOperation.JUMPI -> JUMP_IF()
            EthereumOperation.PC -> VARIABLE(INSTRUCTION_POSITION)
            EthereumOperation.MSIZE -> HALT(UNKNOWN_INSTRUCTION, "MSIZE")
            EthereumOperation.GAS -> HALT(UNKNOWN_INSTRUCTION, "GAS")
            EthereumOperation.JUMPDEST -> JUMP_DESTINATION()
            EthereumOperation.LOG0 -> DROP(2)
            EthereumOperation.LOG1 -> DROP(3)
            EthereumOperation.LOG2 -> DROP(4)
            EthereumOperation.LOG3 -> DROP(5)
            EthereumOperation.LOG4 -> DROP(6)
            EthereumOperation.SLOADBYTES -> HALT(UNKNOWN_INSTRUCTION, "SLOADBYTES")
            EthereumOperation.SSTOREBYTES -> HALT(UNKNOWN_INSTRUCTION, "SSTOREBYTES")
            EthereumOperation.SSIZE -> HALT(UNKNOWN_INSTRUCTION, "SSIZE")
            EthereumOperation.CREATE -> HALT(UNKNOWN_INSTRUCTION, "CREATE")
            EthereumOperation.CALL -> CALL()
            EthereumOperation.CALLCODE -> HALT(UNKNOWN_INSTRUCTION, "CALLCODE")
            EthereumOperation.RETURN -> CALL_RETURN()
            EthereumOperation.DELEGATECALL -> HALT(UNKNOWN_INSTRUCTION, "DELEGATECALL")
            EthereumOperation.CALLBLACKBOX -> HALT(UNKNOWN_INSTRUCTION, "CALLBLACKBOX")
            EthereumOperation.STATICCALL -> HALT(UNKNOWN_INSTRUCTION, "STATICCALL")
            EthereumOperation.REVERT -> HALT(REVERT)
            EthereumOperation.STOP2 -> EXIT()
            EthereumOperation.SELFDESTRUCT -> HALT(UNKNOWN_INSTRUCTION, "SELFDESTRUCT")
            EthereumOperation.PUSH1 -> PUSH(BackedInteger(ethereumInstruction.input), width = 2)
            EthereumOperation.PUSH2 -> PUSH(BackedInteger(ethereumInstruction.input), width = 3)
            EthereumOperation.PUSH3 -> PUSH(BackedInteger(ethereumInstruction.input), width = 4)
            EthereumOperation.PUSH4 -> PUSH(BackedInteger(ethereumInstruction.input), width = 5)
            EthereumOperation.PUSH5 -> PUSH(BackedInteger(ethereumInstruction.input), width = 6)
            EthereumOperation.PUSH6 -> PUSH(BackedInteger(ethereumInstruction.input), width = 7)
            EthereumOperation.PUSH7 -> PUSH(BackedInteger(ethereumInstruction.input), width = 8)
            EthereumOperation.PUSH8 -> PUSH(BackedInteger(ethereumInstruction.input), width = 9)
            EthereumOperation.PUSH9 -> PUSH(BackedInteger(ethereumInstruction.input), width = 10)
            EthereumOperation.PUSH10 -> PUSH(BackedInteger(ethereumInstruction.input), width = 11)
            EthereumOperation.PUSH11 -> PUSH(BackedInteger(ethereumInstruction.input), width = 12)
            EthereumOperation.PUSH12 -> PUSH(BackedInteger(ethereumInstruction.input), width = 13)
            EthereumOperation.PUSH13 -> PUSH(BackedInteger(ethereumInstruction.input), width = 14)
            EthereumOperation.PUSH14 -> PUSH(BackedInteger(ethereumInstruction.input), width = 15)
            EthereumOperation.PUSH15 -> PUSH(BackedInteger(ethereumInstruction.input), width = 16)
            EthereumOperation.PUSH16 -> PUSH(BackedInteger(ethereumInstruction.input), width = 17)
            EthereumOperation.PUSH17 -> PUSH(BackedInteger(ethereumInstruction.input), width = 18)
            EthereumOperation.PUSH18 -> PUSH(BackedInteger(ethereumInstruction.input), width = 19)
            EthereumOperation.PUSH19 -> PUSH(BackedInteger(ethereumInstruction.input), width = 20)
            EthereumOperation.PUSH20 -> PUSH(BackedInteger(ethereumInstruction.input), width = 21)
            EthereumOperation.PUSH21 -> PUSH(BackedInteger(ethereumInstruction.input), width = 22)
            EthereumOperation.PUSH22 -> PUSH(BackedInteger(ethereumInstruction.input), width = 23)
            EthereumOperation.PUSH23 -> PUSH(BackedInteger(ethereumInstruction.input), width = 24)
            EthereumOperation.PUSH24 -> PUSH(BackedInteger(ethereumInstruction.input), width = 25)
            EthereumOperation.PUSH25 -> PUSH(BackedInteger(ethereumInstruction.input), width = 26)
            EthereumOperation.PUSH26 -> PUSH(BackedInteger(ethereumInstruction.input), width = 27)
            EthereumOperation.PUSH27 -> PUSH(BackedInteger(ethereumInstruction.input), width = 28)
            EthereumOperation.PUSH28 -> PUSH(BackedInteger(ethereumInstruction.input), width = 29)
            EthereumOperation.PUSH29 -> PUSH(BackedInteger(ethereumInstruction.input), width = 30)
            EthereumOperation.PUSH30 -> PUSH(BackedInteger(ethereumInstruction.input), width = 31)
            EthereumOperation.PUSH31 -> PUSH(BackedInteger(ethereumInstruction.input), width = 32)
            EthereumOperation.PUSH32 -> PUSH(BackedInteger(ethereumInstruction.input), width = 33)
            EthereumOperation.DUP1 -> DUPLICATE(0)
            EthereumOperation.DUP2 -> DUPLICATE(1)
            EthereumOperation.DUP3 -> DUPLICATE(2)
            EthereumOperation.DUP4 -> DUPLICATE(3)
            EthereumOperation.DUP5 -> DUPLICATE(4)
            EthereumOperation.DUP6 -> DUPLICATE(5)
            EthereumOperation.DUP7 -> DUPLICATE(6)
            EthereumOperation.DUP8 -> DUPLICATE(7)
            EthereumOperation.DUP9 -> DUPLICATE(8)
            EthereumOperation.DUP10 -> DUPLICATE(9)
            EthereumOperation.DUP11 -> DUPLICATE(10)
            EthereumOperation.DUP12 -> DUPLICATE(11)
            EthereumOperation.DUP13 -> DUPLICATE(12)
            EthereumOperation.DUP14 -> DUPLICATE(13)
            EthereumOperation.DUP15 -> DUPLICATE(14)
            EthereumOperation.DUP16 -> DUPLICATE(15)
            EthereumOperation.SWAP1 -> SWAP(0, 1)
            EthereumOperation.SWAP2 -> SWAP(0, 2)
            EthereumOperation.SWAP3 -> SWAP(0, 3)
            EthereumOperation.SWAP4 -> SWAP(0, 4)
            EthereumOperation.SWAP5 -> SWAP(0, 5)
            EthereumOperation.SWAP6 -> SWAP(0, 6)
            EthereumOperation.SWAP7 -> SWAP(0, 7)
            EthereumOperation.SWAP8 -> SWAP(0, 8)
            EthereumOperation.SWAP9 -> SWAP(0, 9)
            EthereumOperation.SWAP10 -> SWAP(0, 10)
            EthereumOperation.SWAP11 -> SWAP(0, 11)
            EthereumOperation.SWAP12 -> SWAP(0, 12)
            EthereumOperation.SWAP13 -> SWAP(0, 13)
            EthereumOperation.SWAP14 -> SWAP(0, 14)
            EthereumOperation.SWAP15 -> SWAP(0, 15)
            EthereumOperation.SWAP16 -> SWAP(0, 16)
            EthereumOperation.UNKNOWN -> HALT(UNKNOWN_INSTRUCTION, "UNKNOWN: " + ethereumInstruction.input.serialize())
        }
    }

    fun transpile(ethereumInstructions: List<EthereumInstruction>): List<Instruction> {
        return ethereumInstructions.asSequence()
                .map { this.map(it) }
                .toList()
    }
}