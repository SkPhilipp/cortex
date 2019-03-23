package com.hileco.cortex.vm.concrete

import com.hileco.cortex.instructions.stack.ExecutionVariable
import com.hileco.cortex.instructions.stack.ExecutionVariable.START_TIME
import com.hileco.cortex.vm.layer.DelegateLayered
import com.hileco.cortex.vm.layer.LayeredMap
import java.math.BigInteger

class VirtualMachine : DelegateLayered<VirtualMachine> {
    val programs: MutableList<ProgramContext>
    val atlas: MutableMap<BigInteger, Program>
    val variables: LayeredMap<ExecutionVariable, BigInteger>
    var instructionsExecuted: Int

    constructor(vararg programContexts: ProgramContext, startTime: Long = System.currentTimeMillis()) {
        programs = ArrayList()
        atlas = HashMap()
        variables = LayeredMap()
        variables[START_TIME] = startTime.toBigInteger()
        instructionsExecuted = 0
        for (programContext in programContexts) {
            programs.add(programContext)
        }
    }

    private constructor(programs: MutableList<ProgramContext>,
                        atlas: MutableMap<BigInteger, Program>,
                        variables: LayeredMap<ExecutionVariable, BigInteger>,
                        instructionsExecuted: Int) {
        this.programs = programs
        this.atlas = atlas
        this.variables = variables
        this.instructionsExecuted = instructionsExecuted
    }

    override fun recreateParent(): VirtualMachine {
        val branchPrograms = programs.map { program -> program.parent() }.toMutableList()
        val branchAtlas = atlas.mapValues { (_, symbolicProgram) -> symbolicProgram.parent()}.toMutableMap()
        return VirtualMachine(branchPrograms, branchAtlas, variables.parent(), instructionsExecuted)
    }

    override fun branchDelegates(): VirtualMachine {
        val branchPrograms = programs.map { program -> program.branch() }.toMutableList()
        val branchAtlas = atlas.mapValues { (_, symbolicProgram) -> symbolicProgram.branch() }.toMutableMap()
        return VirtualMachine(branchPrograms, branchAtlas, variables.branch(), instructionsExecuted)
    }

    override fun closeDelegates() {
        programs.forEach { program -> program.close() }
        atlas.values.forEach { symbolicProgram -> symbolicProgram.close() }
        variables.close()
    }
}
