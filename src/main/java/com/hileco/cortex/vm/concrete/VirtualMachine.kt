package com.hileco.cortex.vm.concrete

import com.hileco.cortex.instructions.stack.ExecutionVariable
import com.hileco.cortex.instructions.stack.ExecutionVariable.START_TIME
import com.hileco.cortex.vm.layer.Layered
import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class VirtualMachine : Layered<VirtualMachine> {
    val programs: LayeredStack<ProgramContext>
    val atlas: LayeredMap<BigInteger, Program>
    val variables: LayeredMap<ExecutionVariable, BigInteger>
    var instructionsExecuted: Int

    constructor(vararg programContexts: ProgramContext, startTime: Long = System.currentTimeMillis()) {
        programs = LayeredStack()
        atlas = LayeredMap()
        variables = LayeredMap()
        variables[START_TIME] = startTime.toBigInteger()
        instructionsExecuted = 0
        for (programContext in programContexts) {
            programs.push(programContext)
        }
    }

    private constructor(programs: LayeredStack<ProgramContext>,
                        atlas: LayeredMap<BigInteger, Program>,
                        variables: LayeredMap<ExecutionVariable, BigInteger>,
                        instructionsExecuted: Int) {
        this.programs = programs
        this.atlas = atlas
        this.variables = variables
        this.instructionsExecuted = instructionsExecuted
    }

    override fun branch(): VirtualMachine {
        val branchPrograms = LayeredStack<ProgramContext>()
        programs.asSequence()
                .map { programContext -> programContext.branch() }
                .forEach { branchedProgramContext -> branchPrograms.push(branchedProgramContext) }
        val branchAtlas = LayeredMap<BigInteger, Program>()
        atlas.keySet().asSequence()
                .map { key -> key to atlas[key]!!.branch() }
                .forEach { (key, program) -> branchAtlas[key] = program }
        return VirtualMachine(branchPrograms, branchAtlas, variables.branch(), instructionsExecuted)
    }

    override fun close() {
        programs.close()
        atlas.close()
        variables.close()
    }
}
