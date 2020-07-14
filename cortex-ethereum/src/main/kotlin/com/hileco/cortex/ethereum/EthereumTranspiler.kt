package com.hileco.cortex.ethereum

import com.hileco.cortex.vm.ProgramException.Reason.REVERT
import com.hileco.cortex.vm.ProgramException.Reason.UNKNOWN_INSTRUCTION
import com.hileco.cortex.vm.ProgramStoreZone.*
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.bits.BITWISE_AND
import com.hileco.cortex.vm.instructions.bits.BITWISE_NOT
import com.hileco.cortex.vm.instructions.bits.BITWISE_OR
import com.hileco.cortex.vm.instructions.bits.BITWISE_XOR
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
            EthereumOperation.SHR -> HALT(UNKNOWN_INSTRUCTION, "SHR")
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
            EthereumOperation.PUSH1 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH2 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH3 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH4 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH5 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH6 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH7 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH8 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH9 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH10 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH11 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH12 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH13 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH14 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH15 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH16 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH17 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH18 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH19 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH20 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH21 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH22 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH23 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH24 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH25 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH26 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH27 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH28 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH29 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH30 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH31 -> PUSH(ethereumInstruction.input)
            EthereumOperation.PUSH32 -> PUSH(ethereumInstruction.input)
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
            EthereumOperation.UNKNOWN -> HALT(UNKNOWN_INSTRUCTION, "UNKNOWN")
        }
    }

    fun transpile(ethereumInstructions: List<EthereumInstruction>): List<Instruction> {
        return ethereumInstructions.asSequence()
                .map { this.map(it) }
                .toList()
    }
}