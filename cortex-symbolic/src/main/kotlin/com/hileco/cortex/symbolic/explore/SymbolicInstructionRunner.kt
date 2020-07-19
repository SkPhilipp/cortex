package com.hileco.cortex.symbolic.explore

import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.symbolic.ExpressionOptimizer
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.ProgramException.Reason.*
import com.hileco.cortex.vm.ProgramRunner.Companion.STACK_LIMIT
import com.hileco.cortex.vm.ProgramStoreZone.*
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.bits.BITWISE_AND
import com.hileco.cortex.vm.instructions.bits.BITWISE_OR
import com.hileco.cortex.vm.instructions.bits.SHIFT_RIGHT
import com.hileco.cortex.vm.instructions.calls.CALL
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

class SymbolicInstructionRunner {

    private val expressionOptimizer = ExpressionOptimizer()

    fun execute(instruction: Instruction, virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        when (instruction) {
            is BITWISE_AND -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(BitwiseAnd(left, right)))
            }
            is BITWISE_OR -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(BitwiseOr(left, right)))
            }
            is SHIFT_RIGHT -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(ShiftRight(left, right)))
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
                if (inOffset != Value(0)
                        || inSize != Value(0)
                        || outOffset != Value(0)
                        || outSize != Value(0)) {
                    throw UnsupportedOperationException("Memory transfer is not supported for symbolic execution")
                }
                if (recipientAddress !is Value) {
                    throw UnsupportedOperationException("Non-concrete address calling is not supported for symbolic execution")
                }
                val recipient = virtualMachine.atlas[recipientAddress.constant.toBigInteger()]
                        ?: throw ProgramException(ProgramException.Reason.CALL_RECIPIENT_MISSING)
                val sourceAddress = programContext.program.address
                recipient.transfers.push(Value(sourceAddress.toLong()) to valueTransferred)
                val newContext = SymbolicProgramContext(recipient)
                virtualMachine.programs.add(newContext)
            }
            is EQUALS -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(Equals(left, right)))
            }
            is GREATER_THAN -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(GreaterThan(left, right)))
            }
            is IS_ZERO -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(IsZero(left)))
            }
            is LESS_THAN -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(LessThan(left, right)))
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
                val addressExpression = programContext.stack.pop() as? Value
                        ?: throw UnsupportedOperationException("Non-concrete address loading is not supported for symbolic execution")
                val address = addressExpression.constant.toBigInteger()
                val storage: VmMap<BigInteger, Expression> = when (instruction.programStoreZone) {
                    MEMORY -> programContext.memory
                    DISK -> programContext.program.storage
                    CALL_DATA -> programContext.callData
                }
                val expression = storage[address] ?: Reference(instruction.programStoreZone, addressExpression)
                programContext.stack.push(expression)
            }
            is SAVE -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val addressExpression = programContext.stack.pop() as? Value
                        ?: throw UnsupportedOperationException("Non-concrete address loading is not supported for symbolic execution")
                val storage: VmMap<BigInteger, Expression> = when (instruction.programStoreZone) {
                    MEMORY -> programContext.memory
                    DISK -> programContext.program.storage
                    CALL_DATA -> throw IllegalArgumentException("Unsupported ProgramStoreZone: ${instruction.programStoreZone}")
                }
                val valueExpression = programContext.stack.pop()
                storage[addressExpression.constant.toBigInteger()] = valueExpression
            }
            is EXIT -> {
                virtualMachine.programs.removeAt(virtualMachine.programs.size - 1)
            }
            is JUMP -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val addressExpression = programContext.stack.pop() as? Value
                        ?: throw UnsupportedOperationException("Jumps to non-concrete addresses are not supported for symbolic execution")
                val nextInstructionPosition = addressExpression.constant.toInt()
                val nextInstruction = programContext.program.instructionsAbsolute[nextInstructionPosition] ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
                if (nextInstruction.instruction is JUMP_DESTINATION) {
                    programContext.instructionPosition = nextInstructionPosition
                } else {
                    throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
                }
            }
            is JUMP_DESTINATION -> {
            }
            is JUMP_IF -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val addressExpression = programContext.stack.pop() as? Value
                        ?: throw UnsupportedOperationException("Jumps to non-concrete addresses are not supported for symbolic execution")
                val conditionExpression = programContext.stack.pop() as? Value
                        ?: throw UnsupportedOperationException("Jumps using non-concrete conditions should not be performed via this method")
                if (conditionExpression.constant > 0) {
                    val nextInstructionPosition = addressExpression.constant.toInt()
                    val nextInstruction = programContext.program.instructionsAbsolute[nextInstructionPosition] ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
                    if (nextInstruction.instruction is JUMP_DESTINATION) {
                        programContext.instructionPosition = nextInstructionPosition
                    } else {
                        throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
                    }
                }
            }
            is ADD -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(Add(left, right)))
            }
            is DIVIDE -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(Divide(left, right)))
            }
            is HASH -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(Hash(left, instruction.method)))
            }
            is MODULO -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(Modulo(left, right)))
            }
            is EXPONENT -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(Exponent(left, right)))
            }
            is MULTIPLY -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(Multiply(left, right)))
            }
            is SUBTRACT -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val left = programContext.stack.pop()
                val right = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(Subtract(left, right)))
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
                programContext.stack.push(Value(BigInteger(instruction.bytes).toLong()))
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
                        programContext.stack.push(Value(programContext.program.address.toLong()))
                    }
                    INSTRUCTION_POSITION -> {
                        programContext.stack.push(Value(programContext.instructionPosition.toLong()))
                    }
                    ADDRESS_CALLER -> {
                        val address = if (virtualMachine.programs.size > 1) virtualMachine.programs.last().program.address.toLong() else 0
                        programContext.stack.push(Value(address))
                    }
                    CALL_DATA_SIZE -> {
                        // TODO: This is a quick workaround to get CALL_DATA_SIZE to work
                        programContext.stack.push(Reference(CALL_DATA, Value(999)))
                    }
                    TRANSACTION_FUNDS -> {
                        // TODO: This is a quick workaround to get TRANSACTION_FUNDS to work
                        programContext.stack.push(Value(0))
                    }
                    ADDRESS_ORIGIN -> {
                        val address = if (virtualMachine.programs.size > 1) virtualMachine.programs.first().program.address.toLong() else 0
                        programContext.stack.push(Value(address))
                    }
                    else -> {
                        virtualMachine.variables[instruction.executionVariable]
                    }
                }
                if (programContext.stack.size() > STACK_LIMIT) {
                    throw ProgramException(STACK_OVERFLOW)
                }
            }
            else -> {
                throw UnsupportedOperationException()
            }
        }
    }
}