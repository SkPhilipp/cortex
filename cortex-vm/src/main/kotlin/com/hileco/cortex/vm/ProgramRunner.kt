package com.hileco.cortex.vm

import com.hileco.cortex.collections.VmByteArray
import com.hileco.cortex.vm.ProgramException.Reason.*
import com.hileco.cortex.vm.ProgramStoreZone.*
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ONE_32
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.bytes.asUInt256
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
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or
import kotlin.experimental.xor

class ProgramRunner(private val virtualMachine: VirtualMachine,
                    private var onInstruction: (PositionedInstruction, ProgramContext) -> Unit = { _, _ -> }) {
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
            onInstruction(positionedInstruction, programContext)
            runInstruction(positionedInstruction, programContext)
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

    private fun runInstruction(positionedInstruction: PositionedInstruction, programContext: ProgramContext) {
        when (val instruction = positionedInstruction.instruction) {
            is BITWISE_AND -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                val result = ByteArray(32)
                for (i in result.indices) {
                    result[i] = left[i] and right[i]
                }
                programContext.stack.push(BackedInteger(result))
            }
            is BITWISE_NOT -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val element = programContext.stack.pop()
                val result = ByteArray(32)
                for (i in result.indices) {
                    result[i] = element[i].inv()
                }
                programContext.stack.push(BackedInteger(result))
            }
            is BITWISE_OR -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                val result = ByteArray(32)
                for (i in result.indices) {
                    result[i] = left[i] or right[i]
                }
                programContext.stack.push(BackedInteger(result))
            }
            is BITWISE_XOR -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                val result = ByteArray(32)
                for (i in result.indices) {
                    result[i] = left[i] xor right[i]
                }
                programContext.stack.push(BackedInteger(result))
            }
            is CALL -> {
                if (programContext.stack.size() < 7) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val gas = programContext.stack.pop()
                val recipientAddress = programContext.stack.pop()
                val valueTransferred = programContext.stack.pop()
                val inOffset = programContext.stack.pop()
                val inSize = programContext.stack.pop()
                val outOffset = programContext.stack.pop()
                val outSize = programContext.stack.pop()
                programContext.returnDataOffset = outOffset
                programContext.returnDataSize = outSize
                val recipient = virtualMachine.atlas[recipientAddress] ?: throw ProgramException(CALL_RECIPIENT_MISSING)
                val sourceAddress = programContext.program.address
                recipient.transfers.push(sourceAddress to valueTransferred)
                val newContext = ProgramContext(recipient)
                val inputData = programContext.memory.read(inOffset.asUInt() * LOAD.SIZE, inSize.asUInt())
                newContext.callData.clear()
                newContext.callData.write(0, inputData)
                virtualMachine.programs.add(newContext)
            }
            is CALL_RETURN -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val offset = programContext.stack.pop()
                val size = programContext.stack.pop()
                virtualMachine.programs.removeAt(virtualMachine.programs.size - 1)
                if (virtualMachine.programs.isNotEmpty()) {
                    val nextContext = virtualMachine.programs.last()
                    val data = programContext.memory.read(offset.asUInt(), size.asUInt())
                    val wSize = nextContext.returnDataSize
                    if (data.size > wSize.asUInt()) {
                        throw ProgramException(CALL_RETURN_DATA_TOO_LARGE)
                    }
                    val dataExpanded = data.copyOf(wSize.asUInt())
                    val wOffset = nextContext.returnDataOffset
                    nextContext.memory.write(wOffset.asUInt(), dataExpanded, wSize.asUInt())
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
                programContext.stack.push(if (left == right) ONE_32 else ZERO_32)
            }
            is GREATER_THAN -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(if (left > right) ONE_32 else ZERO_32)
            }
            is IS_ZERO -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val top = programContext.stack.pop()
                programContext.stack.push(if (top == ZERO_32) ONE_32 else ZERO_32)
            }
            is LESS_THAN -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(if (left < right) ONE_32 else ZERO_32)
            }
            is DROP -> {
                if (programContext.stack.size() < instruction.elements) {
                    throw ProgramException(STACK_UNDERFLOW)
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
                val address = programContext.stack.pop()
                val storage: VmByteArray = when (instruction.programStoreZone) {
                    MEMORY -> programContext.memory
                    DISK -> programContext.program.storage
                    CALL_DATA -> programContext.callData
                }
                if (address.asUInt() * LOAD.SIZE + LOAD.SIZE > storage.size()) {
                    throw ProgramException(STORAGE_ACCESS_OUT_OF_BOUNDS)
                }
                val bytes = storage.read(address.asUInt() * LOAD.SIZE, LOAD.SIZE)
                programContext.stack.push(BackedInteger(bytes))

            }
            is SAVE -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val address = programContext.stack.pop()
                val storage: VmByteArray = when (instruction.programStoreZone) {
                    MEMORY -> programContext.memory
                    DISK -> programContext.program.storage
                    CALL_DATA -> throw IllegalArgumentException("Unsupported ProgramStoreZone: $instruction.programStoreZone")
                }
                val value = programContext.stack.pop()
                if (address.asUInt() * LOAD.SIZE + 32 > storage.size()) {
                    throw ProgramException(STORAGE_ACCESS_OUT_OF_BOUNDS)
                }
                storage.write(address.asUInt() * LOAD.SIZE, value.getBackingArray())
            }
            is EXIT -> {
                virtualMachine.programs.removeAt(virtualMachine.programs.size - 1)
            }
            is JUMP -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val address = programContext.stack.pop()
                val nextInstruction = programContext.program.instructionsAbsolute[address.asUInt()] ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
                nextInstruction.instruction as? JUMP_DESTINATION ?: throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
                programContext.instructionPosition = address.asUInt()
            }
            is JUMP_DESTINATION -> {
            }
            is JUMP_IF -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val address = programContext.stack.pop().asUInt()
                val condition = programContext.stack.pop()
                if (condition != ZERO_32) {
                    val nextInstruction = programContext.program.instructionsAbsolute[address] ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
                    nextInstruction.instruction as? JUMP_DESTINATION ?: throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
                    programContext.instructionPosition = address
                }
            }
            is ADD -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(left + right)
            }
            is DIVIDE -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(left / right)
            }
            is HASH -> {
                try {
                    val messageDigest = MessageDigest.getInstance(instruction.method)
                    if (programContext.stack.size() < 1) {
                        throw ProgramException(STACK_UNDERFLOW)
                    }
                    val value = programContext.stack.pop()
                    messageDigest.update(value.getBackingArray())
                    programContext.stack.push(BackedInteger(messageDigest.digest()))
                } catch (e: NoSuchAlgorithmException) {
                    throw IllegalArgumentException("Unknown hash method: ${instruction.method}", e)
                }
            }
            is MODULO -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(left % right)
            }
            is MULTIPLY -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(left * right)
            }
            is SUBTRACT -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(left - right)
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
                programContext.stack.push(instruction.value)
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
                        programContext.stack.push(programContext.program.address)
                    }
                    INSTRUCTION_POSITION -> {
                        programContext.stack.push(programContext.instructionPosition.asUInt256())
                    }
                    ADDRESS_CALLER -> {
                        val address = if (virtualMachine.programs.size > 1) virtualMachine.programs.last().program.address else ZERO_32
                        programContext.stack.push(address)
                    }
                    CALL_DATA_SIZE -> {
                        // TODO: This is a quick workaround to get CALL_DATA_SIZE to work
                        val size = programContext.callData.size()
                        programContext.stack.push(size.asUInt256())
                    }
                    TRANSACTION_FUNDS -> {
                        // TODO: This is a quick workaround to get TRANSACTION_FUNDS to work
                        programContext.stack.push(ZERO_32)
                    }
                    ADDRESS_ORIGIN -> {
                        val address = if (virtualMachine.programs.size > 1) virtualMachine.programs.first().program.address else ZERO_32
                        programContext.stack.push(address)
                    }
                    else -> {
                        val value = virtualMachine.variables[instruction.executionVariable] ?: ZERO_32
                        programContext.stack.push(value)
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
        const val INSTRUCTION_LIMIT = 100_000
    }
}
