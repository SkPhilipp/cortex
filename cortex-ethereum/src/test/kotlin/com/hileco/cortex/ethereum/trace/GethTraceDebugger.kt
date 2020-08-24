package com.hileco.cortex.ethereum.trace

import com.hileco.cortex.ethereum.deserializeBytes
import com.hileco.cortex.vm.PositionedInstruction
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramRunner
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Assert
import java.math.BigInteger

class GethTraceDebugger(virtualMachine: VirtualMachine, private val gethTrace: GethTrace) {

    private val programRunner: ProgramRunner
    private var instructionsExecuted: Int

    init {
        this.programRunner = ProgramRunner(virtualMachine, this::onInstruction)
        this.instructionsExecuted = 0
    }

    fun run(): Exception? {
        try {
            this.instructionsExecuted = 0
            this.programRunner.run()
            return null
        } catch (e: GethTraceException) {
            return e
        } catch (e: Exception) {
            return e
        }
    }

    private fun message(element: String): String {
        return "Equality issue on step $instructionsExecuted: $element"
    }

    private fun onInstruction(positionedInstruction: PositionedInstruction, programContext: ProgramContext) {
        val gethTraceLog = gethTrace.structLogs[instructionsExecuted]
        Assert.assertEquals(message("Instruction position"), gethTraceLog.pc, positionedInstruction.absolutePosition)
        Assert.assertEquals(message("Stack size"), gethTraceLog.stack.size, programContext.stack.size())
        for (i in 0 until programContext.stack.size()) {
            val stackBytesExpected = BigInteger(gethTraceLog.stack[0].deserializeBytes())
            val stackBytesActual = BigInteger(programContext.stack[0])
            Assert.assertEquals(message("Stack element $i"), stackBytesExpected, stackBytesActual)
        }
        instructionsExecuted++
    }
}