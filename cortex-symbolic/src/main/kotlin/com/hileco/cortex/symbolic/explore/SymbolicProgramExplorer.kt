package com.hileco.cortex.symbolic.explore

import com.hileco.cortex.symbolic.explore.strategies.ExploreStrategy
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.ProgramException.Reason.*
import com.hileco.cortex.vm.ProgramRunner.Companion.INSTRUCTION_LIMIT
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicInteger

class SymbolicProgramExplorer(private val strategy: ExploreStrategy) {
    private val tasks = AtomicInteger(0)
    private val forkJoinPool: ForkJoinPool = ForkJoinPool(PARALLELISM)
    private val symbolicInstructionRunner = SymbolicInstructionRunner()

    /**
     * Submits the [virtualMachine] for exploration. During exploration, [SymbolicVirtualMachine]s may branch, every branched [SymbolicVirtualMachine] is also
     * explored until it reaches an end state or it does not pass the [ExploreStrategy.checkDrop] upon branching.
     *
     * Exploration of all branches is awaited, unless the [ExploreStrategy.checkStop] indicates to stop early.
     */
    @Synchronized
    fun explore(virtualMachine: SymbolicVirtualMachine) {
        tasks.incrementAndGet()
        forkJoinPool.submit {
            process(virtualMachine)
        }
        do {
            Thread.sleep(1)
        } while (tasks.get() > 0)
    }

    fun process(virtualMachine: SymbolicVirtualMachine) {
        try {
            if (virtualMachine.programs.isNotEmpty()) {
                var programContext: SymbolicProgramContext = virtualMachine.programs.last()
                while (programContext.instructionPosition < programContext.program.instructionsLastPosition) {
                    val currentInstructionPosition = programContext.instructionPosition
                    val positionedInstruction = programContext.program.instructionsAbsolute[currentInstructionPosition]
                            ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
                    val instruction = positionedInstruction.instruction
                    if (instruction is JUMP_IF
                            && programContext.stack.size() <= JUMP_IF.CONDITION.position + 1
                            && programContext.stack.peek(JUMP_IF.CONDITION.position) !is Expression.Value) {
                        val branchedVirtualMachine = virtualMachine.copy()
                        chooseJumpIf(branchedVirtualMachine, currentInstructionPosition, false)
                        chooseJumpIf(virtualMachine, currentInstructionPosition, true)
                        break
                    }
                    symbolicInstructionRunner.execute(instruction, virtualMachine, programContext)
                    if (virtualMachine.programs.isEmpty()) {
                        virtualMachine.exited = true
                        strategy.handleComplete(virtualMachine)
                        break
                    }
                    programContext = virtualMachine.programs.last()
                    if (programContext.instructionPosition == currentInstructionPosition) {
                        programContext.instructionPosition = currentInstructionPosition + instruction.width
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
                if (programContext.instructionPosition == programContext.program.instructionsLastPosition) {
                    virtualMachine.exited = true
                    strategy.handleComplete(virtualMachine)
                }
            } else {
                virtualMachine.exited = true
                strategy.handleComplete(virtualMachine)
            }
        } catch (e: ProgramException) {
            virtualMachine.exited = true
            virtualMachine.exitedReason = e.reason
            strategy.handleComplete(virtualMachine)
        } finally {
            tasks.decrementAndGet()
        }
    }

    private fun chooseJumpIf(virtualMachine: SymbolicVirtualMachine, currentInstructionPosition: Int, take: Boolean) {
        val programContext = virtualMachine.programs.last()
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

            val nextInstructionPosition = address.constant.toInt()
            val nextInstruction = programContext.program.instructionsAbsolute[nextInstructionPosition] ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
            if (nextInstruction.instruction is JUMP_DESTINATION) {
                programContext.instructionPosition = nextInstructionPosition
            } else {
                throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
            }
        } else {
            programContext.instructionPosition++
        }
        if (strategy.checkDrop(virtualMachine)) {
            strategy.handleDrop(virtualMachine)
        } else {
            if (!strategy.checkStop()) {
                tasks.incrementAndGet()
                forkJoinPool.submit {
                    process(virtualMachine)
                }
            }
        }
    }

    companion object {
        const val PARALLELISM = 16
    }
}
