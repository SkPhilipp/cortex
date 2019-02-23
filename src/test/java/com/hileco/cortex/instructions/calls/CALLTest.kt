package com.hileco.cortex.instructions.calls

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.concrete.Program
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.concrete.VirtualMachine
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class CALLTest : InstructionTest() {
    @Test
    fun runCallIntoWinner() {
        val callerInstructions = listOf(
                PUSH(123),
                PUSH(10),
                SAVE(MEMORY),
                PUSH(0),
                PUSH(0),
                PUSH(LOAD.SIZE.toLong()),
                PUSH(10),
                PUSH(0),
                PUSH(LIBRARY_ADDRESS),
                CALL()
        )
        val libraryInstructions = listOf(
                PUSH(0),
                LOAD(CALL_DATA)
        )
        val callerProgram = Program(callerInstructions)
        val callerProgramContext = ProgramContext(callerProgram)
        val virtualMachine = VirtualMachine(callerProgramContext)
        val libraryProgram = Program(libraryInstructions)
        virtualMachine.atlas[BigInteger.valueOf(LIBRARY_ADDRESS)] = libraryProgram
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        val stack = virtualMachine.programs.peek().stack
        Documentation.of("instructions/call")
                .headingParagraph("CALL").paragraph("The CALL operation allows for interaction between programs. An area of MEMORY (marked by offset" +
                        " and size) in the calling program may be made available through CALL_DATA to the callee. A second area of MEMORY (marked by offset and" +
                        " size) may also be designated for the callee to return data into. CALL transfers program execution from the caller to the start of the " +
                        " callee program. Additionally, value owned by the calling program may be transferred to the callee program.")
                .paragraph("Example calling program:").source(callerInstructions)
                .paragraph("Example callee program at address $LIBRARY_ADDRESS:").source(libraryInstructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertEquals(BigInteger(stack.pop()), BigInteger.valueOf(123))
    }

    companion object {
        const val LIBRARY_ADDRESS = 0x123456789
    }
}
