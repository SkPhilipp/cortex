package com.hileco.cortex.vm

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.ProgramException.Reason.STACK_OVERFLOW
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.LIMIT_32
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.bits.BITWISE_AND
import com.hileco.cortex.vm.instructions.bits.BITWISE_NOT
import com.hileco.cortex.vm.instructions.bits.BITWISE_OR
import com.hileco.cortex.vm.instructions.bits.BITWISE_XOR
import com.hileco.cortex.vm.instructions.calls.CALL
import com.hileco.cortex.vm.instructions.calls.CALL_RETURN
import com.hileco.cortex.vm.instructions.conditions.EQUALS
import com.hileco.cortex.vm.instructions.conditions.GREATER_THAN
import com.hileco.cortex.vm.instructions.conditions.IS_ZERO
import com.hileco.cortex.vm.instructions.conditions.LESS_THAN
import com.hileco.cortex.vm.instructions.debug.DROP
import com.hileco.cortex.vm.instructions.debug.HALT
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.io.SAVE
import com.hileco.cortex.vm.instructions.jumps.EXIT
import com.hileco.cortex.vm.instructions.jumps.JUMP
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.math.*
import com.hileco.cortex.vm.instructions.stack.*
import com.hileco.cortex.vm.instructions.stack.ExecutionVariable.*
import org.junit.Assert
import org.junit.Test

class ProgramRunnerTest {

    private fun run(instructions: List<Instruction>, customSetup: (VirtualMachine, ProgramContext) -> Unit = { _, _ -> }): ProgramContext {
        val program = Program(instructions)
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        customSetup(virtualMachine, programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        return programContext
    }

    @Test
    fun runBitwiseAnd() {
        val instructions = listOf(
                PUSH(5.toBackedInteger()),
                PUSH(3.toBackedInteger()),
                BITWISE_AND())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-and")
                .headingParagraph("BITWISE_AND").paragraph("The BITWISE_AND operation performs a bitwise AND operation on each bit of the top two elements on" + " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(1.toBackedInteger(), stack.pop())
    }

    @Test
    fun runBitwiseNot() {
        val instructions = listOf(
                PUSH(127.toBackedInteger()),
                BITWISE_NOT())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-not")
                .headingParagraph("BITWISE_NOT").paragraph("The BITWISE_NOT operation performs logical negation on each bit of the top element on the stack")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(LIMIT_32 - 127.toBackedInteger(), stack.pop())
    }

    @Test
    fun runBitwiseXor() {
        val instructions = listOf(
                PUSH(5.toBackedInteger()),
                PUSH(3.toBackedInteger()),
                BITWISE_XOR())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-xor")
                .headingParagraph("BITWISE_XOR").paragraph("The BITWISE_XOR operation performs a bitwise XOR operation on each bit of the top two elements on" + " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(6.toBackedInteger(), stack.pop())
    }

    @Test
    fun runBitwiseOr() {
        val instructions = listOf(
                PUSH(5.toBackedInteger()),
                PUSH(3.toBackedInteger()),
                BITWISE_OR())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-or")
                .headingParagraph("BITWISE_OR").paragraph("The BITWISE_OR operation performs a bitwise OR operation on each bit of the top two elements on" + " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(7.toBackedInteger(), stack.pop())
    }

    @Test
    fun runCallIntoWinner() {
        val callerInstructions = listOf(
                PUSH(123.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                SAVE(ProgramStoreZone.MEMORY),
                PUSH(0.toBackedInteger()),
                PUSH(0.toBackedInteger()),
                PUSH(LOAD.SIZE.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                PUSH(0.toBackedInteger()),
                PUSH(LIBRARY_ADDRESS),
                PUSH(0.toBackedInteger()),
                CALL()
        )
        val libraryInstructions = listOf(
                PUSH(0.toBackedInteger()),
                LOAD(ProgramStoreZone.CALL_DATA)
        )
        val callerProgram = Program(callerInstructions)
        val callerProgramContext = ProgramContext(callerProgram)
        val virtualMachine = VirtualMachine(callerProgramContext)
        val libraryProgram = Program(libraryInstructions)
        virtualMachine.atlas[LIBRARY_ADDRESS] = libraryProgram
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        val stack = virtualMachine.programs.last().stack
        Documentation.of("instructions/call")
                .headingParagraph("CALL").paragraph("The CALL operation allows for interaction between programs. An area of MEMORY (marked by offset" +
                        " and size) in the calling program may be made available through CALL_DATA to the callee. A second area of MEMORY (marked by offset and" +
                        " size) may also be designated for the callee to return data into. CALL transfers program execution from the caller to the start of the " +
                        " callee program. Additionally, value owned by the calling program may be transferred to the callee program.")
                .paragraph("Example calling program:").source(callerInstructions)
                .paragraph("Example callee program at address ${LIBRARY_ADDRESS}:").source(libraryInstructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(123.toBackedInteger(), stack.pop())
    }

    @Test
    fun runCallAndReturn() {
        val callerInstructions = listOf(
                PUSH(LOAD.SIZE.toBackedInteger()),
                PUSH(0.toBackedInteger()),
                PUSH(0.toBackedInteger()),
                PUSH(0.toBackedInteger()),
                PUSH(0.toBackedInteger()),
                PUSH(LIBRARY_ADDRESS),
                PUSH(0.toBackedInteger()),
                CALL(),
                PUSH(0.toBackedInteger()),
                LOAD(ProgramStoreZone.MEMORY)
        )
        val libraryInstructions = listOf(
                PUSH(12345.toBackedInteger()),
                PUSH(0.toBackedInteger()),
                SAVE(ProgramStoreZone.MEMORY),
                PUSH(LOAD.SIZE.toBackedInteger()),
                PUSH(0.toBackedInteger()),
                CALL_RETURN()
        )
        val callerProgram = Program(callerInstructions)
        val callerProgramContext = ProgramContext(callerProgram)
        val virtualMachine = VirtualMachine(callerProgramContext)
        val libraryProgram = Program(libraryInstructions)
        virtualMachine.atlas[LIBRARY_ADDRESS] = libraryProgram
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        Documentation.of("instructions/call-return")
                .headingParagraph("CALL_RETURN").paragraph("The CALL_RETURN operation allows for a callee program invoked by a CALL to return data to" +
                        " the calling program. CALL_RETURN takes an area of MEMORY (marked by offset and size) of the callee and places this into the" +
                        " CALL-specified area of MEMORY. CALL_RETURN ends program execution from the callee and resumes program execution of the caller.")
                .paragraph("Example calling program:").source(callerInstructions)
                .paragraph("Example callee program at address $LIBRARY_ADDRESS:").source(libraryInstructions)
                .paragraph("Resulting stack:").source(callerProgramContext.stack)
        Assert.assertEquals(1, callerProgramContext.stack.size())
        Assert.assertEquals(12345.toBackedInteger(), callerProgramContext.stack.pop())
    }

    @Test
    fun runEquals() {
        val instructions = listOf(
                PUSH(100.toBackedInteger()),
                PUSH(100.toBackedInteger()),
                EQUALS())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/equals")
                .headingParagraph("EQUALS").paragraph("The EQUALS operation removes two elements from the stack, then adds a 1 or 0 to the stack" + " depending on whether the top element was equal to the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(1.toBackedInteger(), stack.pop())
    }

    @Test
    fun runGreaterThan() {
        val instructions = listOf(
                PUSH(10.toBackedInteger()),
                PUSH(100.toBackedInteger()),
                GREATER_THAN())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/greater-than")
                .headingParagraph("GREATER_THAN").paragraph("The GREATER_THAN operation removes two elements from the stack, then adds a 1 or 0 to the stack" + " depending on whether the top element was greater than the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(1.toBackedInteger(), stack.pop())
    }

    @Test
    fun runIsZero() {
        val instructions = listOf(
                PUSH(0.toBackedInteger()),
                IS_ZERO())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/is-zero")
                .headingParagraph("IS_ZERO").paragraph("The IS_ZERO operation removes the top element of the stack then adds a 1 or 0 to the stack" + " depending on whether the element was equal to 0.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(1.toBackedInteger(), stack.pop())
    }

    @Test
    fun runLessThan() {
        val instructions = listOf(
                PUSH(100.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                LESS_THAN())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/less-than")
                .headingParagraph("LESS_THAN").paragraph("The LESS_THAN operation removes two elements from the stack, then adds a 1 or 0 to the stack" + " depending on whether the top element was less than the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(1.toBackedInteger(), stack.pop())
    }

    @Test
    fun runDrop() {
        val instructions = listOf(
                PUSH(1.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                PUSH(100.toBackedInteger()),
                DROP(2))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/drop")
                .headingParagraph("DROP").paragraph("The DROP operation removes multiple elements from the top of the stack." +
                        " It is generally only used within optimization or transpiler processes," +
                        " to replace NOOP-like instructions which remove elements from the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(1.toBackedInteger(), stack.pop())
    }

    @Test(expected = ProgramException::class)
    fun runHalt() {
        val instructions = listOf(HALT(STACK_OVERFLOW))
        Documentation.of("instructions/halt")
                .headingParagraph("HALT").paragraph("The HALT operation cancels execution of all programs on the virtual machine, and provides a reason for doing so. It is " +
                        "generally only used within optimization processed to replace instructions which would otherwise " +
                        "cause the same errors during runtime.")
                .paragraph("Example program:").source(instructions)
        this.run(instructions)
    }

    @Test
    fun runNoop() {
        val instructions = listOf(NOOP())
        this.run(instructions)
        Documentation.of("instructions/noop")
                .headingParagraph("NOOP").paragraph("This operation does nothing. It is generally only used within optimization processes to replace " +
                        "instructions instead of having to remove them. This allows all JUMP-related instructions remain " +
                        "functional.")
                .paragraph("Example program:").source(instructions)
    }

    @Test
    fun runSaveLoad() {
        val instructions = listOf(
                PUSH(10.toBackedInteger()),
                PUSH(1234.toBackedInteger()),
                SAVE(ProgramStoreZone.MEMORY),
                PUSH(1234.toBackedInteger()),
                LOAD(ProgramStoreZone.MEMORY)
        )
        val stack = this.run(instructions).stack
        Documentation.of("instructions/save-and-load")
                .headingParagraph("SAVE & LOAD").paragraph("The SAVE operation removes two elements from the stack, using the top element as an" +
                        " address and the second element as a value to write into the area specified" +
                        " (${ProgramStoreZone.MEMORY}, or ${ProgramStoreZone.DISK})." +
                        " The LOAD  operation removes one element from the stack, using it as an" +
                        " address to read from the area specified (${ProgramStoreZone.MEMORY}, ${ProgramStoreZone.DISK}, or ${ProgramStoreZone.CALL_DATA}).")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(10.toBackedInteger(), stack.pop())
    }

    @Test
    fun runJump() {
        val instructions = listOf(
                PUSH(3.toBackedInteger()),
                JUMP(),
                PUSH(100.toBackedInteger()),
                JUMP_DESTINATION())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/jump")
                .headingParagraph("JUMP").paragraph("The JUMP operation removes one element from the stack, using it to set the instruction position of the " +
                        "program itself. JUMPs may only result in instruction positions which point to a JUMP_DESTINATION instruction. The JUMP_DESTINATION by itself is equal to a NOOP.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(0, stack.size())
    }

    @Test
    fun testJumpMultiWidth() {
        val instructions = listOf(
                PUSH(6.toBackedInteger(), width = 2),
                JUMP(),
                PUSH(100.toBackedInteger(), width = 3),
                JUMP_DESTINATION())
        val stack = this.run(instructions).stack
        Assert.assertEquals(0, stack.size())
    }

    @Test
    fun runJumpIf() {
        val instructions = listOf(PUSH(1.toBackedInteger()),
                PUSH(4.toBackedInteger()),
                JUMP_IF(),
                PUSH(100.toBackedInteger()),
                JUMP_DESTINATION())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/jump-if")
                .headingParagraph("JUMP_IF").paragraph("The JUMP_IF operation removes two elements from the stack, using the top element to set the " +
                        "instruction position of the program itself, depending on whether the second element is a " +
                        "positive value. JUMP_IFs may only result in instruction positions which point to a " +
                        "JUMP_DESTINATION instruction. The JUMP_DESTINATION by itself is equal to a NOOP.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(0, stack.size())
    }

    @Test
    fun runJumpDestination() {
        val instructions = listOf(
                PUSH(3.toBackedInteger()),
                JUMP(),
                PUSH(100.toBackedInteger()),
                JUMP_DESTINATION())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/jump-destination")
                .headingParagraph("JUMP_DESTINATION").paragraph("Marks a part of a program as being able to be jumped to.")
        Assert.assertEquals(0, stack.size())
    }

    @Test
    fun runExit() {
        val instructions = listOf(
                PUSH(10.toBackedInteger()),
                EXIT(),
                PUSH(100.toBackedInteger()))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/exit")
                .headingParagraph("EXIT").paragraph("The EXIT operation ends execution of the program.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(10.toBackedInteger(), stack.pop())
    }

    @Test
    fun runAdd() {
        val instructions = listOf(
                PUSH(100.toBackedInteger()),
                PUSH(1.toBackedInteger()),
                ADD())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/add")
                .headingParagraph("ADD").paragraph("The ADD operation removes two elements from the stack, adds them together and puts the " +
                        "result on the stack. (Results larger than $LIMIT_32 overflow without error)")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(101.toBackedInteger(), stack.pop())
    }

    @Test
    fun runOverflowAdd() {
        val instructions = listOf(
                PUSH(LIMIT_32),
                PUSH(10.toBackedInteger()),
                ADD())
        val stack = this.run(instructions).stack
        Assert.assertEquals(9.toBackedInteger(), stack.pop())
    }

    @Test
    fun runDivide() {
        val instructions = listOf(
                PUSH(20.toBackedInteger()),
                PUSH(100.toBackedInteger()),
                DIVIDE())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/divide")
                .headingParagraph("DIVIDE").paragraph("The DIVIDE operation removes two elements from the stack, divides them with the top " +
                        "element being the dividend and the second element being the divisor. It puts the" +
                        "resulting quotient on the stack. (Results larger than $LIMIT_32 overflow without error)")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(5.toBackedInteger(), stack.pop())
    }

    @Test
    fun runHash() {
        val instructions = listOf(
                PUSH(123.toBackedInteger()),
                HASH("SHA-256"))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/hash")
                .headingParagraph("HASH").paragraph("The HASH operation removes one element from the stack, performs the desired hashing " + "method on it and adds the resulting hash to the stack")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
    }

    @Test
    fun runModulo() {
        val instructions = listOf(
                PUSH(3.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                MODULO())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/modulo")
                .headingParagraph("MODULO").paragraph("The MODULO operation removes two elements from the stack, divides them with the top " +
                        "element being the dividend and the second element being the divisor. It puts the" +
                        "resulting remainder on the stack. (Results larger than $LIMIT_32 overflow without error)")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(1.toBackedInteger(), stack.pop())
    }

    @Test
    fun runMultiply() {
        val instructions = listOf(
                PUSH(10.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                MULTIPLY())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/multiply")
                .headingParagraph("MULTIPLY").paragraph("The MULTIPLY operation removes two elements from the stack, multiplies them and puts" +
                        " the result on the stack. (Results larger than $LIMIT_32 overflow without error)")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(100.toBackedInteger(), stack.pop())
    }

    @Test
    fun runMultiplyOverflow() {
        val instructions = listOf(
                PUSH(LIMIT_32),
                PUSH(10.toBackedInteger()),
                MULTIPLY())
        val stack = this.run(instructions).stack
        Assert.assertEquals(LIMIT_32 * 10.toBackedInteger(), stack.pop())
    }

    @Test
    fun runSubtract() {
        val instructions = listOf(
                PUSH(1.toBackedInteger()),
                PUSH(100.toBackedInteger()),
                SUBTRACT())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/subtract")
                .headingParagraph("SUBTRACT").paragraph("The SUBTRACT operation removes two elements from the stack, subtracts the second element from the " + "top element and puts the result on the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(99.toBackedInteger(), stack.pop())
    }

    @Test
    fun runDuplicate() {
        val instructions = listOf(
                PUSH(100.toBackedInteger()),
                DUPLICATE(0))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/duplicate")
                .headingParagraph("DUPLICATE").paragraph("The DUPLICATE operation adds a duplicate of an element on the stack, to the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(2, stack.size())
        Assert.assertEquals(100.toBackedInteger(), stack.pop())
        Assert.assertEquals(100.toBackedInteger(), stack.pop())
    }

    @Test
    fun runPop() {
        val instructions = listOf(
                PUSH(1.toBackedInteger()),
                PUSH(100.toBackedInteger()),
                POP())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/pop")
                .headingParagraph("POP").paragraph("The POP operation removes the top element from the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(1.toBackedInteger(), stack.pop())
    }

    @Test
    fun runPush() {
        val instructions = listOf(
                PUSH(1.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                PUSH(100.toBackedInteger()))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/push")
                .headingParagraph("PUSH").paragraph("The PUSH operation adds one element to top of the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(3, stack.size())
        Assert.assertEquals(100.toBackedInteger(), stack.pop())
        Assert.assertEquals(10.toBackedInteger(), stack.pop())
        Assert.assertEquals(1.toBackedInteger(), stack.pop())
    }

    @Test
    fun runSwap() {
        val instructions = listOf(
                PUSH(100.toBackedInteger()),
                PUSH(1.toBackedInteger()),
                SWAP(0, 1))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/swap")
                .headingParagraph("SWAP").paragraph("The SWAP operation swaps two elements on the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(2, stack.size())
        Assert.assertEquals(100.toBackedInteger(), stack.pop())
        Assert.assertEquals(1.toBackedInteger(), stack.pop())
    }

    @Test
    fun documentVariable() {
        val instructions = listOf(
                VARIABLE(ADDRESS_SELF)
        )
        val stack = this.run(instructions).stack
        Documentation.of("instructions/variable")
                .headingParagraph("VARIABLE")
                .paragraph("The VARIABLE adds the value of the referenced execution-bound variable to the stack.")
                .paragraph("Available execution-bound variables by name are: ${values().joinToString { "$it" }}")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
    }

    @Test
    fun testVariableAddressSelf() {
        val instructions = listOf(
                VARIABLE(ADDRESS_SELF)
        )
        val stack = this.run(instructions).stack
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(0.toBackedInteger(), stack.pop())
    }

    @Test
    fun testVariableInstructionPosition() {
        val instructions = listOf(
                PUSH(1.toBackedInteger()),
                POP(),
                VARIABLE(INSTRUCTION_POSITION)
        )
        val stack = this.run(instructions).stack
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(2.toBackedInteger(), stack.pop())
    }

    @Test
    fun testVariableAddressCaller() {
        val instructions = listOf(
                VARIABLE(ADDRESS_CALLER)
        )
        val stack = this.run(instructions).stack
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(0.toBackedInteger(), stack.pop())
    }

    @Test
    fun testVariableAddressOrigin() {
        val instructions = listOf(VARIABLE(ADDRESS_ORIGIN))
        val stack = this.run(instructions).stack
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(0.toBackedInteger(), stack.pop())
    }

    @Test
    fun testVariableStartTime() {
        val startTime = System.currentTimeMillis()
        val instructions = listOf(VARIABLE(START_TIME))
        val stack = this.run(instructions) { virtualMachine, _ ->
            virtualMachine.variables[START_TIME] = startTime.toBackedInteger()
        }.stack
        Assert.assertEquals(1, stack.size())
        Assert.assertEquals(startTime.toBackedInteger(), stack.pop())
    }

    companion object {
        val LIBRARY_ADDRESS = 0x123456789.toBackedInteger()
    }

}
