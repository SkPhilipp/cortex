package com.hileco.cortex.instructions.calls

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.vm.concrete.ProgramRunner
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.concrete.Program
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.concrete.VirtualMachine
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class CALL_RETURNTest : InstructionTest() {
    @Test
    fun runCallAndReturn() {
        val callerInstructions = listOf(
                PUSH(LOAD.SIZE.toLong()),
                PUSH(0),
                PUSH(0),
                PUSH(0),
                PUSH(0),
                PUSH(LIBRARY_ADDRESS),
                CALL(),
                PUSH(0),
                LOAD(ProgramStoreZone.MEMORY)
        )
        val libraryInstructions = listOf(
                PUSH(12345),
                PUSH(0),
                SAVE(ProgramStoreZone.MEMORY),
                PUSH(LOAD.SIZE.toLong()),
                PUSH(0),
                CALL_RETURN()
        )
        val callerProgram = Program(callerInstructions)
        val callerProgramContext = ProgramContext(callerProgram)
        val virtualMachine = VirtualMachine(callerProgramContext)
        val libraryProgram = Program(libraryInstructions)
        virtualMachine.atlas[LIBRARY_ADDRESS.toBigInteger()] = libraryProgram
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        Documentation.of("instructions/call-return")
                .headingParagraph("CALL_RETURN").paragraph("The CALL_RETURN operation allows for a callee program invoked by a CALL to return data to" +
                        " the calling program. CALL_RETURN takes an area of MEMORY (marked by offset and size) of the callee and places this into the" +
                        " CALL-specified area of MEMORY. CALL_RETURN ends program execution from the callee and resumes program execution of the caller.")
                .paragraph("Example calling program:").source(callerInstructions)
                .paragraph("Example callee program at address $LIBRARY_ADDRESS:").source(libraryInstructions)
                .paragraph("Resulting stack:").source(callerProgramContext.stack)
        Assert.assertEquals(callerProgramContext.stack.size(), 1)
        Assert.assertEquals(BigInteger(callerProgramContext.stack.pop()), 12345.toBigInteger())
    }

    companion object {
        const val LIBRARY_ADDRESS = 0x123456789
    }
}
