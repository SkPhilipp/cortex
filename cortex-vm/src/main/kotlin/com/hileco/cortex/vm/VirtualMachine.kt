package com.hileco.cortex.vm

import com.hileco.cortex.collections.VmComponent
import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.vm.instructions.stack.ExecutionVariable
import java.math.BigInteger

class VirtualMachine : VmComponent<VirtualMachine> {
    val programs: MutableList<ProgramContext>
    val atlas: MutableMap<BigInteger, Program>
    val variables: VmMap<ExecutionVariable, BigInteger>
    var instructionsExecuted: Int

    constructor(vararg programContexts: ProgramContext, startTime: Long = System.currentTimeMillis()) {
        programs = ArrayList()
        atlas = HashMap()
        variables = LayeredVmMap()
        variables[ExecutionVariable.START_TIME] = startTime.toBigInteger()
        instructionsExecuted = 0
        for (programContext in programContexts) {
            programs.add(programContext)
        }
    }

    private constructor(programs: MutableList<ProgramContext>,
                        atlas: MutableMap<BigInteger, Program>,
                        variables: VmMap<ExecutionVariable, BigInteger>,
                        instructionsExecuted: Int) {
        this.programs = programs
        this.atlas = atlas
        this.variables = variables
        this.instructionsExecuted = instructionsExecuted
    }

    override fun close() {
        programs.forEach { it.close() }
        atlas.values.forEach { it.close() }
        variables.close()
    }

    override fun copy(): VirtualMachine {
        val branchPrograms = programs.map { it.copy() }.toMutableList()
        val branchAtlas = atlas.mapValues { (_, program) -> program.copy() }.toMutableMap()
        return VirtualMachine(branchPrograms, branchAtlas, variables.copy(), instructionsExecuted)
    }
}
