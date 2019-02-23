package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.vm.layer.Layered
import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class SymbolicVirtualMachine : Layered<SymbolicVirtualMachine> {
    val programs: LayeredStack<SymbolicProgramContext>
    val atlas: LayeredMap<BigInteger, SymbolicProgram>
    var path: LayeredStack<SymbolicPathEntry>
    var instructionsExecuted: Int
    var exited: Boolean = false
    var exitedReason: ProgramException.Reason? = null

    constructor(vararg programContexts: SymbolicProgramContext) {
        programs = LayeredStack()
        atlas = LayeredMap()
        path = LayeredStack()
        instructionsExecuted = 0
        for (programContext in programContexts) {
            programs.push(programContext)
        }
    }

    private constructor(programs: LayeredStack<SymbolicProgramContext>,
                        atlas: LayeredMap<BigInteger, SymbolicProgram>,
                        path: LayeredStack<SymbolicPathEntry>,
                        instructionsExecuted: Int) {
        this.programs = programs
        this.atlas = atlas
        this.path = path
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
        return SymbolicVirtualMachine(branchPrograms, branchAtlas, path.branch(), instructionsExecuted)
    }

    override fun close() {
        programs.close()
        atlas.close()
    }
}
