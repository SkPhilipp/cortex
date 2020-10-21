package com.hileco.cortex.symbolic.explore

import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.symbolic.ExpressionOptimizer
import com.hileco.cortex.symbolic.explore.SymbolicInstructionRunner.StepMode.NON_CONCRETE_JUMP_TAKE
import com.hileco.cortex.symbolic.explore.SymbolicInstructionRunner.StepMode.NON_CONCRETE_JUMP_THROW
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicTransfer
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.ProgramException.Reason.*
import com.hileco.cortex.vm.ProgramRunner.Companion.STACK_LIMIT
import com.hileco.cortex.vm.ProgramStoreZone.*
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ONE_32
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.bits.BITWISE_AND
import com.hileco.cortex.vm.instructions.bits.BITWISE_NOT
import com.hileco.cortex.vm.instructions.bits.BITWISE_OR
import com.hileco.cortex.vm.instructions.bits.SHIFT_RIGHT
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

class SymbolicInstructionRunner {

    private val expressionOptimizer = ExpressionOptimizer()

    enum class StepMode {
        NON_CONCRETE_JUMP_TAKE,
        NON_CONCRETE_JUMP_SKIP,
        NON_CONCRETE_JUMP_THROW
    }

    fun execute(instruction: Instruction,
                virtualMachine: SymbolicVirtualMachine,
                programContext: SymbolicProgramContext,
                stepMode: StepMode = NON_CONCRETE_JUMP_THROW) {
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
            is BITWISE_NOT -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val element = programContext.stack.pop()
                programContext.stack.push(expressionOptimizer.optimize(BitwiseNot(element)))
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
                @Suppress("UNUSED_VARIABLE") val gas = programContext.stack.pop()
                val recipientAddress = programContext.stack.pop()
                val valueTransferred = programContext.stack.pop()
                @Suppress("UNUSED_VARIABLE") val inOffset = programContext.stack.pop()
                @Suppress("UNUSED_VARIABLE") val inSize = programContext.stack.pop()
                @Suppress("UNUSED_VARIABLE") val outOffset = programContext.stack.pop()
                @Suppress("UNUSED_VARIABLE") val outSize = programContext.stack.pop()
                // TODO: Support non-concrete memory transfer & non-concrete targets through CALL
                val sourceAddress = programContext.program.address
                virtualMachine.transfers.push(SymbolicTransfer(Value(sourceAddress), recipientAddress, valueTransferred))
                if (recipientAddress is Value) {
                    val recipient = virtualMachine.atlas[recipientAddress.constant] ?: throw ProgramException(CALL_RECIPIENT_MISSING)
                    // TODO: Both transfers and symbolic path entries reference fields which are program-context specific
                    val newContext = SymbolicProgramContext(recipient)
                    virtualMachine.programs.add(newContext)
                    throw UnsupportedOperationException("CALL between programs is not supported")
                } else {
                    programContext.stack.push(Value(ONE_32))
                }
            }
            is CALL_RETURN -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                @Suppress("UNUSED_VARIABLE") val offset = programContext.stack.pop()
                @Suppress("UNUSED_VARIABLE") val size = programContext.stack.pop()
                virtualMachine.programs.removeAt(virtualMachine.programs.size - 1)
                if (virtualMachine.programs.isNotEmpty()) {
                    throw UnsupportedOperationException("CALL_RETURN between programs is not supported")
                }
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
                val addressExpression = programContext.stack.pop()
                val addressValue = addressExpression as? Value
                        ?: throw UnsupportedOperationException("Loading non-concrete address such as $addressExpression is not supported for symbolic execution")
                val address = addressValue.constant
                val storage: VmMap<BackedInteger, Expression> = when (instruction.programStoreZone) {
                    MEMORY -> programContext.memory
                    DISK -> programContext.program.storage
                    CALL_DATA -> programContext.callData
                    CODE -> throw IllegalArgumentException("Unsupported ProgramStoreZone: ${instruction.programStoreZone}")
                }
                val expression = storage[address] ?: VariableExtract(instruction.programStoreZone, addressValue)
                programContext.stack.push(expression)
            }
            is SAVE -> {
                if (programContext.stack.size() < 2) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val addressExpression = programContext.stack.pop()
                val addressValue = addressExpression as? Value
                        ?: throw UnsupportedOperationException("Loading non-concrete address such as $addressExpression is not supported for symbolic execution")
                val storage: VmMap<BackedInteger, Expression> = when (instruction.programStoreZone) {
                    MEMORY -> programContext.memory
                    DISK -> programContext.program.storage
                    CALL_DATA -> throw IllegalArgumentException("Unsupported ProgramStoreZone: ${instruction.programStoreZone}")
                    CODE -> throw IllegalArgumentException("Unsupported ProgramStoreZone: ${instruction.programStoreZone}")
                }
                val valueExpression = programContext.stack.pop()
                storage[addressValue.constant] = valueExpression
            }
            is EXIT -> {
                virtualMachine.programs.removeAt(virtualMachine.programs.size - 1)
            }
            is JUMP -> {
                if (programContext.stack.size() < 1) {
                    throw ProgramException(STACK_UNDERFLOW)
                }
                val addressExpression = programContext.stack.pop()
                val addressValue = addressExpression as? Value
                        ?: throw UnsupportedOperationException("Jumps to non-concrete addresses such as $addressExpression are not supported for symbolic execution")
                val nextInstructionPosition = addressValue.constant.toInt()
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
                val addressExpression = programContext.stack.pop()
                val addressValue = addressExpression as? Value
                        ?: throw UnsupportedOperationException("Jumps to non-concrete addresses such as $addressExpression are not supported for symbolic execution")
                val conditionExpression = programContext.stack.pop()
                if (conditionExpression !is Value) {
                    if (stepMode == NON_CONCRETE_JUMP_THROW) {
                        throw UnsupportedOperationException("Jumps with a non-concrete conditions are not allowed in step mode $stepMode")
                    }
                    virtualMachine.path.push(SymbolicPathEntry(programContext.instructionPosition, addressValue, stepMode == NON_CONCRETE_JUMP_TAKE, conditionExpression))
                }
                if (if (conditionExpression is Value) conditionExpression.constant > ZERO_32 else stepMode == NON_CONCRETE_JUMP_TAKE) {
                    val nextInstructionPosition = addressValue.constant.toInt()
                    val nextInstruction = programContext.program.instructionsAbsolute[nextInstructionPosition]
                            ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
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
                programContext.stack.push(Value(instruction.value))
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
                        programContext.stack.push(Value(programContext.program.address))
                    }
                    INSTRUCTION_POSITION -> {
                        programContext.stack.push(Value(programContext.instructionPosition.toBackedInteger()))
                    }
                    ADDRESS_CALLER -> {
                        val address = if (virtualMachine.programs.size > 1) virtualMachine.programs.last().program.address else ZERO_32
                        programContext.stack.push(Value(address))
                    }
                    TRANSACTION_CALL_DATA_SIZE -> {
                        // TODO: This is a quick workaround to get CALL_DATA_SIZE to work
                        programContext.stack.push(Variable("CALL_DATA_SIZE", 256))
                    }
                    TRANSACTION_FUNDS -> {
                        // TODO: This is a quick workaround to get TRANSACTION_FUNDS to work
                        programContext.stack.push(Value(ZERO_32))
                    }
                    ADDRESS_ORIGIN -> {
                        val address = if (virtualMachine.programs.size > 1) virtualMachine.programs.first().program.address else ZERO_32
                        programContext.stack.push(Value(address))
                    }
                    START_TIME -> {
                        val expression = virtualMachine.variables[START_TIME]
                                ?: throw UnsupportedOperationException("VARIABLE START_TIME is not available")
                        programContext.stack.push(expression)
                    }
                    else -> {
                        throw UnsupportedOperationException("$instruction is not available")
                    }
                }
                if (programContext.stack.size() > STACK_LIMIT) {
                    throw ProgramException(STACK_OVERFLOW)
                }
            }
            else -> {
                throw UnsupportedOperationException("$instruction is not supported")
            }
        }
    }
}