package com.hileco.cortex.vm

import com.hileco.cortex.collections.VmByteArray
import com.hileco.cortex.vm.ProgramException.Reason.*
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
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.io.SAVE
import com.hileco.cortex.vm.instructions.jumps.EXIT
import com.hileco.cortex.vm.instructions.jumps.JUMP
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.math.*
import com.hileco.cortex.vm.instructions.stack.*
import com.hileco.cortex.vm.instructions.stack.ExecutionVariable.*
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or
import kotlin.experimental.xor

class ProgramRunner(private val virtualMachine: VirtualMachine) {
    fun run() {
        if (virtualMachine.programs.isEmpty()) {
            return
        }
        var programContext: ProgramContext = virtualMachine.programs.last()
        while (programContext.program.instructionsRelative.isNotEmpty()
                && programContext.instructionPosition <= programContext.program.instructionsRelative.last().absolutePosition) {
            val currentInstructionPosition = programContext.instructionPosition
            val positionedInstruction = programContext.program.instructionsAbsolute[currentInstructionPosition]
                    ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
            runInstruction(positionedInstruction.instruction, programContext)
            if (virtualMachine.programs.isEmpty()) {
                break
            }
            programContext = virtualMachine.programs.last()
            if (programContext.instructionPosition == currentInstructionPosition) {
                programContext.instructionPosition = currentInstructionPosition + positionedInstruction.instruction.width
            }
            programContext.instructionsExecuted++
            virtualMachine.instructionsExecuted++
            if (programContext.instructionsExecuted >= INSTRUCTION_LIMIT) {
                throw ProgramException(REACHED_LIMIT_INSTRUCTIONS_ON_PROGRAM)
            }
            if (virtualMachine.instructionsExecuted >= INSTRUCTION_LIMIT) {
                throw ProgramException(REACHED_LIMIT_INSTRUCTIONS_ON_VIRTUAL_MACHINE)
            }
        }
    }

    private fun runInstruction(instruction: Instruction, programContext: ProgramContext) {
        when (instruction) {
            is BITWISE_AND -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                val result = ByteArray(Math.max(left.size, right.size))
                for (i in result.indices) {
                    val leftByte = if (i < left.size) left[i] else 0
                    val rightByte = if (i < right.size) right[i] else 0
                    result[i] = leftByte and rightByte
                }
                programContext.stack.push(result)
            }
            is BITWISE_NOT -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val element = programContext.stack.pop()
                val result = ByteArray(element.size)
                for (i in result.indices) {
                    result[i] = element[i].inv()
                }
                programContext.stack.push(result)
            }
            is BITWISE_OR -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                val result = ByteArray(Math.max(left.size, right.size))
                for (i in result.indices) {
                    val leftByte = if (i < left.size) left[i] else 0
                    val rightByte = if (i < right.size) right[i] else 0
                    result[i] = leftByte or rightByte
                }
                programContext.stack.push(result)
            }
            is BITWISE_XOR -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                val result = ByteArray(Math.max(left.size, right.size))
                for (i in result.indices) {
                    val leftByte = if (i < left.size) left[i] else 0
                    val rightByte = if (i < right.size) right[i] else 0
                    result[i] = leftByte xor rightByte
                }
                programContext.stack.push(result)
            }
            is CALL -> {
                if (programContext.stack.size() < 7) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val gas = BigInteger(programContext.stack.pop())
                val recipientAddress = BigInteger(programContext.stack.pop())
                val valueTransferred = BigInteger(programContext.stack.pop())
                val inOffset = BigInteger(programContext.stack.pop())
                val inSize = BigInteger(programContext.stack.pop())
                val outOffset = BigInteger(programContext.stack.pop())
                val outSize = BigInteger(programContext.stack.pop())
                programContext.returnDataOffset = outOffset
                programContext.returnDataSize = outSize
                val recipient = virtualMachine.atlas[recipientAddress] ?: throw ProgramException(CALL_RECIPIENT_MISSING)
                val sourceAddress = programContext.program.address
                recipient.transfers.push(sourceAddress to valueTransferred)
                val newContext = ProgramContext(recipient)
                val inputData = programContext.memory.read(inOffset.toInt() * LOAD.SIZE, inSize.toInt())
                newContext.callData.clear()
                newContext.callData.write(0, inputData)
                virtualMachine.programs.add(newContext)
            }
            is CALL_RETURN -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val offset = BigInteger(programContext.stack.pop())
                val size = BigInteger(programContext.stack.pop())
                virtualMachine.programs.removeAt(virtualMachine.programs.size - 1)
                if (virtualMachine.programs.isNotEmpty()) {
                    val nextContext = virtualMachine.programs.last()
                    val data = programContext.memory.read(offset.toInt(), size.toInt())
                    val wSize = nextContext.returnDataSize
                    if (data.size > wSize.toInt()) {
                        throw ProgramException(CALL_RETURN_DATA_TOO_LARGE)
                    }
                    val dataExpanded = data.copyOf(wSize.toInt())
                    val wOffset = nextContext.returnDataOffset
                    nextContext.memory.write(wOffset.toInt(), dataExpanded, wSize.toInt())
                    // TODO: If `CALL` would have a width other than 1 this will not be enough
                    nextContext.instructionPosition++
                }
            }
            is EQUALS -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                val result = BigInteger(left) == BigInteger(right)
                programContext.stack.push(if (result) TRUE.clone() else FALSE.clone())
            }
            is GREATER_THAN -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                val result = BigInteger(left) > BigInteger(right)
                programContext.stack.push(if (result) TRUE.clone() else FALSE.clone())
            }
            is IS_ZERO -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val top = programContext.stack.pop()
                val isZero = !top.any { byte -> byte > 0 }
                val resultReference = if (isZero) TRUE else FALSE
                programContext.stack.push(resultReference.clone())
            }
            is LESS_THAN -> {

                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                val result = BigInteger(left) < BigInteger(right)
                programContext.stack.push(if (result) TRUE.clone() else FALSE.clone())
            }
            is DROP -> {
                if (programContext.stack.size() < instruction.elements) {
                    throw ProgramException(ProgramException.Reason.STACK_UNDERFLOW)
                }
                repeat(instruction.elements) {
                    programContext.stack.pop()
                }
            }
            is HALT -> {
                throw ProgramException(instruction.reason)
            }
            is NOOP -> {
            }
            is LOAD -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val addressBytes = programContext.stack.pop()
                val address = BigInteger(addressBytes)
                val storage: VmByteArray = when (instruction.programStoreZone) {
                    MEMORY -> programContext.memory
                    DISK -> programContext.program.storage
                    CALL_DATA -> programContext.callData
                }
                if (address.toInt() * LOAD.SIZE + LOAD.SIZE > storage.size()) {
                    throw ProgramException(STORAGE_ACCESS_OUT_OF_BOUNDS)
                }
                val bytes = storage.read(address.toInt() * LOAD.SIZE, LOAD.SIZE)
                programContext.stack.push(bytes)

            }
            is SAVE -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val addressBytes = programContext.stack.pop()
                val address = BigInteger(addressBytes)
                val storage: VmByteArray = when (instruction.programStoreZone) {
                    MEMORY -> programContext.memory
                    DISK -> programContext.program.storage
                    CALL_DATA -> throw IllegalArgumentException("Unsupported ProgramStoreZone: $instruction.programStoreZone")
                }
                val bytes = programContext.stack.pop()
                val alignmentOffset = LOAD.SIZE - bytes.size
                if (address.toInt() * LOAD.SIZE + alignmentOffset + bytes.size > storage.size()) {
                    throw ProgramException(STORAGE_ACCESS_OUT_OF_BOUNDS)
                }
                storage.write(address.toInt() * LOAD.SIZE + alignmentOffset, bytes)
            }
            is EXIT -> {
                virtualMachine.programs.removeAt(virtualMachine.programs.size - 1)
            }
            is JUMP -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val address = BigInteger(programContext.stack.pop()).toInt()
                val positionedInstruction = programContext.program.instructionsAbsolute[address] ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
                positionedInstruction.instruction as? JUMP_DESTINATION ?: throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
                programContext.instructionPosition = address
            }
            is JUMP_DESTINATION -> {
            }
            is JUMP_IF -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val address = BigInteger(programContext.stack.pop()).toInt()
                val condition = BigInteger(programContext.stack.pop())
                if (condition.signum() == 1) {
                    val positionedInstruction = programContext.program.instructionsAbsolute[address] ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
                    positionedInstruction.instruction as? JUMP_DESTINATION ?: throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
                    programContext.instructionPosition = address
                }
            }
            is ADD -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = BigInteger(programContext.stack.pop())
                val right = BigInteger(programContext.stack.pop())
                val result = left.add(right).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
                programContext.stack.push(result.toByteArray())
            }
            is DIVIDE -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = BigInteger(programContext.stack.pop())
                val right = BigInteger(programContext.stack.pop())
                val result = left.divide(right)
                programContext.stack.push(result.toByteArray())
            }
            is HASH -> {
                try {
                    val messageDigest = MessageDigest.getInstance(instruction.method)
                    if (programContext.stack.size() < 1) {
                        throw ProgramException(STACK_UNDERFLOW)
                    }
                    // TODO: This conversion is currently needed due to BigInteger-sourced values not yet being padded with 0
                    val byteArray = BigInteger(programContext.stack.pop()).toByteArray()
                    messageDigest.update(byteArray)
                    programContext.stack.push(messageDigest.digest())
                } catch (e: NoSuchAlgorithmException) {
                    throw IllegalArgumentException("Unknown hash method: ${instruction.method}", e)
                }
            }
            is MODULO -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = BigInteger(programContext.stack.pop())
                val right = BigInteger(programContext.stack.pop())
                val result = left.mod(right)
                programContext.stack.push(result.toByteArray())
            }
            is MULTIPLY -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = BigInteger(programContext.stack.pop())
                val right = BigInteger(programContext.stack.pop())
                val result = left.multiply(right).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
                programContext.stack.push(result.toByteArray())
            }
            is SUBTRACT -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = BigInteger(programContext.stack.pop())
                val right = BigInteger(programContext.stack.pop())
                val result = left.subtract(right)
                programContext.stack.push(result.toByteArray())
            }
            is DUPLICATE -> {
                if (programContext.stack.size() <= instruction.topOffset) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                programContext.stack.duplicate(instruction.topOffset)
                if (programContext.stack.size() > STACK_LIMIT) {
                    throw ProgramException(STACK_OVERFLOW)
                }
            }
            is POP -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                programContext.stack.pop()
            }
            is PUSH -> {
                programContext.stack.push(instruction.bytes)
                if (programContext.stack.size() > STACK_LIMIT) {
                    throw ProgramException(STACK_OVERFLOW)
                }
            }
            is SWAP -> {
                if (programContext.stack.size() <= instruction.topOffsetLeft || programContext.stack.size() <= instruction.topOffsetRight) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                programContext.stack.swap(instruction.topOffsetLeft, instruction.topOffsetRight)
            }
            is VARIABLE -> {
                when (instruction.executionVariable) {
                    ADDRESS_SELF -> {
                        programContext.stack.push(programContext.program.address.toByteArray())
                    }
                    INSTRUCTION_POSITION -> {
                        programContext.stack.push(programContext.instructionPosition.toBigInteger().toByteArray())
                    }
                    ADDRESS_CALLER -> {
                        val address = if (virtualMachine.programs.size > 1) virtualMachine.programs.last().program.address else 0.toBigInteger()
                        programContext.stack.push(address.toByteArray())
                    }
                    CALL_DATA_SIZE -> {
                        // TODO: This is a quick workaround to get CALL_DATA_SIZE to work
                        val size = programContext.callData.size()
                        programContext.stack.push(BigInteger.valueOf(size.toLong()).toByteArray())
                    }
                    TRANSACTION_FUNDS -> {
                        // TODO: This is a quick workaround to get TRANSACTION_FUNDS to work
                        programContext.stack.push(BigInteger.valueOf(0L).toByteArray())
                    }
                    ADDRESS_ORIGIN -> {
                        val address = if (virtualMachine.programs.size > 1) virtualMachine.programs.first().program.address else 0.toBigInteger()
                        programContext.stack.push(address.toByteArray())
                    }
                    else -> {
                        val value = virtualMachine.variables[instruction.executionVariable] ?: 0.toBigInteger()
                        programContext.stack.push(value.toByteArray())
                    }
                }
                if (programContext.stack.size() > STACK_LIMIT) {
                    throw ProgramException(STACK_OVERFLOW)
                }
            }
        }
    }

    companion object {
        const val STACK_LIMIT: Long = Long.MAX_VALUE
        val OVERFLOW_LIMIT: BigInteger = BigInteger(byteArrayOf(2)).pow(256)
        const val INSTRUCTION_LIMIT = 100_000
        private val TRUE = byteArrayOf(1)
        private val FALSE = byteArrayOf(0)
    }
}
