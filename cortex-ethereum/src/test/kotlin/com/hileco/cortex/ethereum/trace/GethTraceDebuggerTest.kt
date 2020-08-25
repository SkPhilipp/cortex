package com.hileco.cortex.ethereum.trace

import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.ethereum.deserializeBytes
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Test


class GethTraceDebuggerTest {

    @Test
    fun traceBarrier000() {
        val gethTraceLoader = GethTraceLoader()
        val gethTrace = gethTraceLoader.loadTrace("/traces/barrier000-trace.json")
        val ethereumBarriers = EthereumBarriers()
        val ethereumBarrier = ethereumBarriers.byId("000")
        val program = Program(ethereumBarrier.cortexInstructions)
        val programContext = ProgramContext(program)
        programContext.callData.write(0, gethTrace.input.deserializeBytes())
        val virtualMachine = VirtualMachine(programContext)
        val gethTraceDebugger = GethTraceDebugger(virtualMachine, gethTrace)

        gethTraceDebugger.run()?.printStackTrace()
    }
}