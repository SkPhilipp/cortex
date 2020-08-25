package com.hileco.cortex.ethereum.trace

import com.fasterxml.jackson.databind.ObjectMapper
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.vm.PositionedInstruction
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramRunner
import com.hileco.cortex.vm.VirtualMachine
import com.hileco.cortex.vm.bytes.BackedInteger
import org.junit.Assert

class GethTraceDebugger(virtualMachine: VirtualMachine, private val gethTrace: GethTrace) {

    private val objectMapper: ObjectMapper
    private val programRunner: ProgramRunner
    private var instructionsExecuted: Int

    init {
        this.objectMapper = ObjectMapper()
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

    private fun message(gethTraceLog: GethTraceLog, element: String): String {
        return "Equality issue on step $instructionsExecuted: $element\nExpected JSON: ${objectMapper.writeValueAsString(gethTraceLog)}"
    }

    private fun onInstruction(positionedInstruction: PositionedInstruction, programContext: ProgramContext) {
        val gethTraceLog = gethTrace.structLogs[instructionsExecuted]
        Assert.assertEquals(message(gethTraceLog, "Instruction position"), gethTraceLog.pc, positionedInstruction.absolutePosition)
        Assert.assertEquals(message(gethTraceLog, "Stack size"), gethTraceLog.stack.size, programContext.stack.size())

        for (i in 0 until programContext.stack.size()) {
            val stackBytesExpected = gethTraceLog.stack[0]
            val stackBytesActual = programContext.stack[0]
            val stackValueExpected = BackedInteger(gethTraceLog.stack[0].deserializeBytes())
            Assert.assertEquals(message(gethTraceLog, "Stack element $i ($stackBytesExpected vs $stackBytesActual)"), stackValueExpected, stackBytesActual)
        }
        instructionsExecuted++
    }
}