package com.hileco.cortex.instructions.calls

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Test
import java.math.BigInteger

class CALLTest : InstructionTest() {
    @Test(expected = ProgramException::class)
    fun runCallIntoWinner() {
        val callerInstructions = listOf(
                PUSH(0),
                PUSH(0),
                PUSH(0),
                PUSH(0),
                PUSH(0),
                PUSH(LIBRARY_ADDRESS),
                CALL()
        )
        val libraryInstructions = listOf(
                HALT(WINNER)
        )
        val callerProgram = Program(callerInstructions)
        val callerProgramContext = ProgramContext(callerProgram)
        val virtualMachine = VirtualMachine(callerProgramContext)
        val libraryProgram = Program(libraryInstructions)
        virtualMachine.atlas[BigInteger.valueOf(LIBRARY_ADDRESS)] = libraryProgram
        Documentation.of("instructions/call")
                .headingParagraph("CALL").paragraph("The CALL operation allows for interaction between programs. An area of MEMORY (marked by offset" +
                        " and size) in the calling program may be made available through CALL_DATA to the callee. A second area of MEMORY (marked by offset and" +
                        " size) may also be designated for the callee to return data into. CALL transfers program execution from the caller to the start of the " +
                        " callee program. Additionally, value owned by the calling program may be transferred to the callee program.")
                .paragraph("Example calling program:").source(callerInstructions)
                .paragraph("Example callee program at $LIBRARY_ADDRESS:").source(libraryInstructions)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    companion object {
        const val LIBRARY_ADDRESS = 0x123456789
    }
}
