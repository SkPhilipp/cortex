package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.constraints.Solver
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.*
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.ProgramConstants.Companion.DEFAULT_BRANCH_LIMIT
import com.hileco.cortex.vm.ProgramConstants.Companion.INSTRUCTION_LIMIT
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

class SymbolicProgramExplorer(virtualMachine: SymbolicVirtualMachine,
                              val branchLimit: Int = DEFAULT_BRANCH_LIMIT) {

    private val completedVirtualMachines: BlockingDeque<SymbolicVirtualMachine>
    private val queuedVirtualMachines: BlockingDeque<SymbolicVirtualMachine>

    init {
        queuedVirtualMachines = LinkedBlockingDeque()
        queuedVirtualMachines.offer(virtualMachine)
        completedVirtualMachines = LinkedBlockingDeque()
    }

    fun run(): List<SymbolicVirtualMachine> {
        while (queuedVirtualMachines.isNotEmpty()) {
            val virtualMachine = queuedVirtualMachines.pollFirst()
            try {
                if (!virtualMachine.programs.isEmpty()) {
                    var programContext: SymbolicProgramContext = virtualMachine.programs.peek()
                    while (programContext.instructionPosition < programContext.program.instructions.size) {
                        val currentInstructionPosition = programContext.instructionPosition
                        val instruction = programContext.program.instructions[currentInstructionPosition]
                        if (instruction is JUMP_IF) {
                            val branchedVirtualMachine = virtualMachine.branch()
                            chooseJumpIf(branchedVirtualMachine, currentInstructionPosition, false)
                            chooseJumpIf(virtualMachine, currentInstructionPosition, true)
                            break
                        }
                        instruction.execute(virtualMachine, programContext)
                        if (virtualMachine.programs.isEmpty()) {
                            virtualMachine.exited = true
                            completedVirtualMachines.offer(virtualMachine)
                            break
                        }
                        programContext = virtualMachine.programs.peek()
                        if (programContext.instructionPosition == currentInstructionPosition) {
                            programContext.instructionPosition = currentInstructionPosition + 1
                        }
                        programContext.instructionsExecuted++
                        virtualMachine.instructionsExecuted++
                        if (programContext.instructionsExecuted >= INSTRUCTION_LIMIT) {
                            throw ProgramException(INSTRUCTION_LIMIT_REACHED_ON_PROGRAM)
                        }
                        if (virtualMachine.instructionsExecuted >= INSTRUCTION_LIMIT) {
                            throw ProgramException(INSTRUCTION_LIMIT_REACHED_ON_VIRTUAL_MACHINE)
                        }
                    }
                    if (programContext.instructionPosition < programContext.program.instructions.size) {
                        virtualMachine.exited = true
                        completedVirtualMachines.offer(virtualMachine)
                    }
                } else {
                    virtualMachine.exited = true
                    completedVirtualMachines.offer(virtualMachine)
                }
            } catch (e: ProgramException) {
                virtualMachine.exited = true
                virtualMachine.exitedReason = e.reason
                virtualMachine.close()
                completedVirtualMachines.offer(virtualMachine)
            }
        }
        return completedVirtualMachines.asSequence().toList()
    }

    private fun chooseJumpIf(virtualMachine: SymbolicVirtualMachine, currentInstructionPosition: Int, take: Boolean) {
        val programContext = virtualMachine.programs.peek()
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_TOO_FEW_ELEMENTS)
        }
        val address = programContext.stack.pop()
        val condition = programContext.stack.pop()
        virtualMachine.path.push(SymbolicPathEntry(currentInstructionPosition, address, take, condition))
        if (take) {
            if (address !is Expression.Value) {
                throw UnsupportedOperationException("Non-concrete address calling is not supported for symbolic execution")
            }
            if (address.constant < 0) {
                throw ProgramException(JUMP_OUT_OF_BOUNDS)
            }
            val instructions = programContext.program.instructions
            if (address.constant >= instructions.size) {
                throw ProgramException(JUMP_OUT_OF_BOUNDS)
            }
            instructions[address.constant.toInt()] as? JUMP_DESTINATION ?: throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
            programContext.instructionPosition = address.constant.toInt()
        } else {
            programContext.instructionPosition++
        }
        if(virtualMachine.path.asSequence().count() > branchLimit) {
            throw ProgramException(BRANCH_LIMIT_REACHED_ON_SYMBOLIC_VIRTUAL_MACHINE)
        }
        val solver = Solver()
        val solution = solver.solve(virtualMachine.condition())
        if (solution.isSolvable) {
            queuedVirtualMachines.offer(virtualMachine)
        }
    }
}
