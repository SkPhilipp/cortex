package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import org.junit.Test

class SymbolicProgramRunnerTest {
    @Test
    fun test() {
        val program = SymbolicProgram(BARRIER_01.instructions)
        val programContext = SymbolicProgramContext(program)
        val virtualMachine = SymbolicVirtualMachine(programContext)
        val programRunner = SymbolicProgramRunner(virtualMachine)
        programRunner.run()
    }
}