package com.hileco.cortex.analysis.explore

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.*
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.ProgramConstants.Companion.INSTRUCTION_LIMIT
import com.hileco.cortex.vm.symbolic.SymbolicPathEntry
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.util.concurrent.ForkJoinPool

class SymbolicProgramExplorer(val dropPredicate: (SymbolicVirtualMachine) -> Boolean = DEFAULT_DROP_PREDICATE,
                              val stopPredicate: (Long) -> Boolean = DEFAULT_STOP_PREDICATE) {

    val completed: MutableList<SymbolicVirtualMachine>
    val dropped: MutableList<SymbolicVirtualMachine>
    private val forkJoinPool: ForkJoinPool

    init {
        completed = arrayListOf()
        dropped = arrayListOf()
        forkJoinPool = ForkJoinPool(PARALLELISM)
    }

    /**
     * Submits the [virtualMachine] for exploration. During exploration, [SymbolicVirtualMachine]s may branch, every branched [SymbolicVirtualMachine] is also
     * explored until it reaches an end state or it does not pass the [dropPredicate] upon branching.
     *
     * Exploration of all branches is awaited, unless the [stopPredicate] indicates to stop early.
     */
    @Synchronized
    fun explore(virtualMachine: SymbolicVirtualMachine) {
        val start = System.currentTimeMillis()
        forkJoinPool.submit {
            process(virtualMachine)
        }
        do {
            Thread.sleep(1)
            val time = System.currentTimeMillis() - start
        } while (!forkJoinPool.isQuiescent && !stopPredicate(time))
    }

    fun process(virtualMachine: SymbolicVirtualMachine) {
        try {
            if (!virtualMachine.programs.isEmpty()) {
                var programContext: SymbolicProgramContext = virtualMachine.programs.peek()
                while (programContext.instructionPosition < programContext.program.instructions.size) {
                    val currentInstructionPosition = programContext.instructionPosition
                    val instruction = programContext.program.instructions[currentInstructionPosition]
                    if (instruction is JUMP_IF
                            && programContext.stack.size() <= JUMP_IF.CONDITION.position + 1
                            && programContext.stack.peek(JUMP_IF.CONDITION.position) !is Expression.Value) {
                        val branchedVirtualMachine = virtualMachine.branch()
                        chooseJumpIf(branchedVirtualMachine, currentInstructionPosition, false)
                        chooseJumpIf(virtualMachine, currentInstructionPosition, true)
                        break
                    }
                    instruction.execute(virtualMachine, programContext)
                    if (virtualMachine.programs.isEmpty()) {
                        virtualMachine.exited = true
                        virtualMachine.close()
                        completed.add(virtualMachine)
                        break
                    }
                    programContext = virtualMachine.programs.peek()
                    if (programContext.instructionPosition == currentInstructionPosition) {
                        programContext.instructionPosition = currentInstructionPosition + 1
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
                if (programContext.instructionPosition == programContext.program.instructions.size) {
                    virtualMachine.exited = true
                    virtualMachine.close()
                    completed.add(virtualMachine)
                }
            } else {
                virtualMachine.exited = true
                virtualMachine.close()
                completed.add(virtualMachine)
            }
        } catch (e: ProgramException) {
            virtualMachine.exited = true
            virtualMachine.exitedReason = e.reason
            virtualMachine.close()
            completed.add(virtualMachine)
        }
    }

    private fun chooseJumpIf(virtualMachine: SymbolicVirtualMachine, currentInstructionPosition: Int, take: Boolean) {
        val programContext = virtualMachine.programs.peek()
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val address = programContext.stack.pop()
        val condition = programContext.stack.pop()
        virtualMachine.path.push(SymbolicPathEntry(currentInstructionPosition, address, take, condition))
        if (take) {
            if (address !is Expression.Value) {
                throw UnsupportedOperationException("Non-concrete address calling is not supported for symbolic execution")
            }
            if (address.constant < 0) {
                throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
            }
            val instructions = programContext.program.instructions
            if (address.constant >= instructions.size) {
                throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
            }
            instructions[address.constant.toInt()] as? JUMP_DESTINATION ?: throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
            programContext.instructionPosition = address.constant.toInt()
        } else {
            programContext.instructionPosition++
        }
        if (dropPredicate(virtualMachine)) {
            dropped.add(virtualMachine)
        } else {
            forkJoinPool.submit {
                process(virtualMachine)
            }
        }
    }

    companion object {
        val DEFAULT_STOP_PREDICATE = { time: Long -> time > DEFAULT_STOP_PREDICATE_TIME_LIMIT }
        val DEFAULT_DROP_PREDICATE = { virtualMachine: SymbolicVirtualMachine -> virtualMachine.path.size() >= DEFAULT_DROP_PREDICATE_PATH_LIMIT }
        private const val DEFAULT_STOP_PREDICATE_TIME_LIMIT = 2000
        private const val DEFAULT_DROP_PREDICATE_PATH_LIMIT = 50
        const val PARALLELISM = 4
    }
}
