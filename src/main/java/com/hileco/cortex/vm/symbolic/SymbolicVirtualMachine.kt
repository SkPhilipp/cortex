package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.vm.layer.Layered
import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class SymbolicVirtualMachine : Layered<SymbolicVirtualMachine> {
    val programs: LayeredStack<SymbolicProgramContext>
    val atlas: LayeredMap<BigInteger, SymbolicProgram>
    var instructionsExecuted: Int

    constructor(vararg programContexts: SymbolicProgramContext) {
        programs = LayeredStack()
        atlas = LayeredMap()
        instructionsExecuted = 0
        for (programContext in programContexts) {
            programs.push(programContext)
        }
    }

    private constructor(programs: LayeredStack<SymbolicProgramContext>,
                        atlas: LayeredMap<BigInteger, SymbolicProgram>,
                        instructionsExecuted: Int) {
        this.programs = programs
        this.atlas = atlas
        this.instructionsExecuted = instructionsExecuted
    }

    override fun branch(): SymbolicVirtualMachine {
        val branchPrograms = LayeredStack<SymbolicProgramContext>()
        programs.asSequence()
                .map { programContext -> programContext.branch() }
                .forEach { branchedProgramContext -> branchPrograms.push(branchedProgramContext) }
        val branchAtlas = LayeredMap<BigInteger, SymbolicProgram>()
        atlas.keySet().asSequence()
                .map { key -> key to atlas[key]!!.branch() }
                .forEach { (key, program) -> branchAtlas[key] = program }
        return SymbolicVirtualMachine(branchPrograms, branchAtlas, instructionsExecuted)
    }

    override fun close() {
        programs.close()
        atlas.close()
    }
}
