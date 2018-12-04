package com.hileco.cortex.instructions

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_LIMIT_REACHED
import com.hileco.cortex.instructions.bits.BITWISE_AND
import com.hileco.cortex.instructions.bits.BITWISE_NOT
import com.hileco.cortex.instructions.bits.BITWISE_OR
import com.hileco.cortex.instructions.bits.BITWISE_XOR
import com.hileco.cortex.instructions.conditions.*
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.math.*
import com.hileco.cortex.instructions.stack.DUPLICATE
import com.hileco.cortex.instructions.stack.POP
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.instructions.stack.SWAP
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramStoreZone.*
import com.hileco.cortex.vm.VirtualMachine
import com.hileco.cortex.vm.VirtualMachine.NUMERICAL_LIMIT
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class OperationsTest {

    @Throws(ProgramException::class)
    private fun run(instruction: List<Instruction>): ProgramContext {
        return this.run(instruction) { _, _ -> }
    }

    @Throws(ProgramException::class)
    private fun run(instructions: List<Instruction>, customSetup: (VirtualMachine, ProgramContext) -> Unit): ProgramContext {
        val program = Program(instructions)
        val programContext = ProgramContext(program)
        val processContext = VirtualMachine(programContext)
        customSetup(processContext, programContext)
        val programRunner = ProgramRunner(processContext)
        programRunner.run()
        return programContext
    }

    @Test
    @Throws(ProgramException::class)
    fun runPush() {
        val instructions = listOf(
                PUSH(byteArrayOf(1)),
                PUSH(byteArrayOf(10)),
                PUSH(byteArrayOf(100)))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/push")
                .headingParagraph("PUSH").paragraph("The PUSH operation adds one element to top of the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 3)
        Assert.assertArrayEquals(stack.pop(), instructions[2].bytes)
        Assert.assertArrayEquals(stack.pop(), instructions[1].bytes)
        Assert.assertArrayEquals(stack.pop(), instructions[0].bytes)
    }

    @Test
    @Throws(ProgramException::class)
    fun runPop() {
        val instructions = listOf(
                PUSH(byteArrayOf(1)),
                PUSH(byteArrayOf(100)),
                POP())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/pop")
                .headingParagraph("POP").paragraph("The POP operation removes the top element from the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), (instructions[0] as PUSH).bytes)
    }

    @Test
    @Throws(ProgramException::class)
    fun runSwap() {
        val instructions = listOf(
                PUSH(byteArrayOf(100)),
                PUSH(byteArrayOf(1)),
                SWAP(0, 1))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/swap")
                .headingParagraph("SWAP").paragraph("The SWAP operation swaps two elements on the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 2)
        Assert.assertArrayEquals(stack.pop(), (instructions[0] as PUSH).bytes)
        Assert.assertArrayEquals(stack.pop(), (instructions[1] as PUSH).bytes)
    }

    @Test
    @Throws(ProgramException::class)
    fun runDuplicate() {
        val instructions = listOf(
                PUSH(byteArrayOf(100)),
                DUPLICATE(0))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/duplicate")
                .headingParagraph("DUPLICATE").paragraph("The DUPLICATE operation adds a duplicate of an element on the stack, to the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 2)
        Assert.assertArrayEquals(stack.pop(), (instructions[0] as PUSH).bytes)
        Assert.assertArrayEquals(stack.pop(), (instructions[0] as PUSH).bytes)
    }

    @Test
    @Throws(ProgramException::class)
    fun runEquals() {
        val instructions = listOf(
                PUSH(byteArrayOf(100)),
                PUSH(byteArrayOf(100)),
                EQUALS())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/equals")
                .headingParagraph("EQUALS").paragraph("The EQUALS operation removes two elements from the stack, then adds a 1 or 0 to the stack" + " depending on whether the top element was equal to the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE)
    }

    @Test
    @Throws(ProgramException::class)
    fun runGreaterThan() {
        val instructions = listOf(
                PUSH(byteArrayOf(10)),
                PUSH(byteArrayOf(100)),
                GREATER_THAN())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/greater-than")
                .headingParagraph("GREATER_THAN").paragraph("The GREATER_THAN operation removes two elements from the stack, then adds a 1 or 0 to the stack" + " depending on whether the top element was greater than the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE)
    }

    @Test
    @Throws(ProgramException::class)
    fun runLessThan() {
        val instructions = listOf(
                PUSH(byteArrayOf(100)),
                PUSH(byteArrayOf(10)),
                LESS_THAN())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/less-than")
                .headingParagraph("LESS_THAN").paragraph("The LESS_THAN operation removes two elements from the stack, then adds a 1 or 0 to the stack" + " depending on whether the top element was less than the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE)
    }

    @Test
    @Throws(ProgramException::class)
    fun runIsZero() {
        val instructions = listOf(
                PUSH(byteArrayOf(0)),
                IS_ZERO())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/is-zero")
                .headingParagraph("IS_ZERO").paragraph("The IS_ZERO operation removes the top element of the stack then adds a 1 or 0 to the stack" + " depending on whether the element was equal to 0.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE)
    }

    @Test
    @Throws(ProgramException::class)
    fun runBitwiseOr() {
        val instructions = listOf(
                PUSH(byteArrayOf(5)),
                PUSH(byteArrayOf(3)),
                BITWISE_OR())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-or")
                .headingParagraph("BITWISE_OR").paragraph("The BITWISE_OR operation performs a bitwise OR operation on each bit of the top two elements on" + " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(7))
    }

    @Test
    @Throws(ProgramException::class)
    fun runBitwiseXor() {
        val instructions = listOf(
                PUSH(byteArrayOf(5)),
                PUSH(byteArrayOf(3)),
                BITWISE_XOR())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-xor")
                .headingParagraph("BITWISE_XOR").paragraph("The BITWISE_XOR operation performs a bitwise XOR operation on each bit of the top two elements on" + " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(6))
    }

    @Test
    @Throws(ProgramException::class)
    fun runBitwiseAnd() {
        val instructions = listOf(
                PUSH(byteArrayOf(5)),
                PUSH(byteArrayOf(3)),
                BITWISE_AND())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-and")
                .headingParagraph("BITWISE_AND").paragraph("The BITWISE_AND operation performs a bitwise AND operation on each bit of the top two elements on" + " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(1))
    }

    @Test
    @Throws(ProgramException::class)
    fun runBitwiseNot() {
        val instructions = listOf(
                PUSH(byteArrayOf(127)),
                BITWISE_NOT())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-not")
                .headingParagraph("BITWISE_NOT").paragraph("The BITWISE_NOT operation performs logical negation on each bit of the top element on the stack")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(0))
    }

    @Test
    @Throws(ProgramException::class)
    fun runAdd() {
        val instructions = listOf(
                PUSH(byteArrayOf(100)),
                PUSH(byteArrayOf(1)),
                ADD())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/add")
                .headingParagraph("ADD").paragraph(String.format("The ADD operation removes two elements from the stack, adds them together and puts the " +
                        "result on the stack. (This result may overflow if it would have been larger " +
                        "than %s)", NUMERICAL_LIMIT))
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(101))
    }

    @Test
    @Throws(ProgramException::class)
    fun runAddOverflowing() {
        val instructions = listOf(
                PUSH(NUMERICAL_LIMIT.toByteArray()),
                PUSH(BigInteger.TEN.toByteArray()),
                ADD())
        val stack = this.run(instructions).stack
        Assert.assertArrayEquals(BigInteger.valueOf(9).toByteArray(), stack.pop())
    }

    @Test
    @Throws(ProgramException::class)
    fun runSubtract() {
        val instructions = listOf(
                PUSH(byteArrayOf(1)),
                PUSH(byteArrayOf(100)),
                SUBTRACT())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/subtract")
                .headingParagraph("SUBTRACT").paragraph("The SUBTRACT operation removes two elements from the stack, subtracts the second element from the " + "top element and puts the result on the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(99))
    }

    @Test
    @Throws(ProgramException::class)
    fun runMultiply() {
        val instructions = listOf(
                PUSH(byteArrayOf(10)),
                PUSH(byteArrayOf(10)),
                MULTIPLY())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/multiply")
                .headingParagraph("MULTIPLY").paragraph(String.format("The MULTIPLY operation removes two elements from the stack, multiplies them and puts" +
                        " the result on the stack. (This result may overflow if it would have been " +
                        "larger" +
                        " than %s)", NUMERICAL_LIMIT))
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(100))
    }

    @Test
    @Throws(ProgramException::class)
    fun runMultiplyOverflowing() {
        val instructions = listOf(
                PUSH(NUMERICAL_LIMIT.toByteArray()),
                PUSH(BigInteger.TEN.toByteArray()),
                MULTIPLY())
        val stack = this.run(instructions).stack
        val expected = NUMERICAL_LIMIT.multiply(BigInteger.TEN).mod(NUMERICAL_LIMIT.add(BigInteger.ONE))
        Assert.assertArrayEquals(expected.toByteArray(), stack.pop())
    }

    @Test
    @Throws(ProgramException::class)
    fun runDivide() {
        val instructions = listOf(
                PUSH(byteArrayOf(20)),
                PUSH(byteArrayOf(100)),
                DIVIDE())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/divide")
                .headingParagraph("DIVIDE").paragraph(String.format("The DIVIDE operation removes two elements from the stack, divides them with the top " +
                        "element being the dividend and the second element being the divisor. It puts the" +
                        " " +
                        "resulting quotient on the stack. (This result may overflow if it would have been" +
                        " " +
                        "larger than %s)", NUMERICAL_LIMIT))
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(5))
    }

    @Test
    @Throws(ProgramException::class)
    fun runModulo() {
        val instructions = listOf(
                PUSH(byteArrayOf(3)),
                PUSH(byteArrayOf(10)),
                MODULO())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/modulo")
                .headingParagraph("MODULO").paragraph(String.format("The MODULO operation removes two elements from the stack, divides them with the top " +
                        "element being the dividend and the second element being the divisor. It puts the" +
                        " " +
                        "resulting remainder on the stack. (This result may overflow if it would have " +
                        "been " +
                        "larger than %s)", NUMERICAL_LIMIT))
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(1))
    }

    @Test
    @Throws(ProgramException::class)
    fun runHashSha512() {
        val instructions = listOf(
                PUSH(byteArrayOf(123)),
                HASH("SHA-512"))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/hash")
                .headingParagraph("HASH").paragraph("The HASH operation removes one element from the stack, performs the desired hashing " + "method on it and adds the resulting hash to the stack")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
    }

    @Test
    @Throws(ProgramException::class)
    fun runJump() {
        val instructions = listOf(
                PUSH(byteArrayOf(3)),
                JUMP(),
                PUSH(byteArrayOf(100)),
                JUMP_DESTINATION())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/jump")
                .headingParagraph("JUMP").paragraph("The JUMP operation removes one element from the stack, using it to set the instruction position of the " +
                        "program itself. JUMPs may only result in instruction positions which point to a JUMP_DESTINATION" +
                        " " +
                        "instruction. The JUMP_DESTINATION by itself is equal to a NOOP.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 0)
    }

    @Test
    @Throws(ProgramException::class)
    fun runJumpIf() {
        val instructions = listOf(PUSH(byteArrayOf(1)),
                PUSH(byteArrayOf(4)),
                JUMP_IF(),
                PUSH(byteArrayOf(100)),
                JUMP_DESTINATION())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/jump-if")
                .headingParagraph("JUMP_IF").paragraph("The JUMP_IF operation removes two elements from the stack, using the top element to set the " +
                        "instruction position of the program itself, depending on whether the second element is a " +
                        "positive value. JUMP_IFs may only result in instruction positions which point to a " +
                        "JUMP_DESTINATION instruction. The JUMP_DESTINATION by itself is equal to a NOOP.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 0)
    }

    @Test
    @Throws(ProgramException::class)
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
    @Throws(ProgramException::class)
    fun runExit() {
        val instructions = listOf(
                PUSH(byteArrayOf(10)),
                EXIT(),
                PUSH(byteArrayOf(100)))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/exit")
                .headingParagraph("EXIT").paragraph("The EXIT operation ends execution of the program.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 1)
        Assert.assertNotEquals(stack.pop(), byteArrayOf(10))
    }

    @Test(expected = ProgramException::class)
    @Throws(ProgramException::class)
    fun runHalt() {
        val instructions = listOf(
                HALT(STACK_LIMIT_REACHED))
        Documentation.of("instructions/halt")
                .headingParagraph("HALT").paragraph("The HALT operation cancels execution of the entire process, and provides a reason for doing so. It is " +
                        "generally only used within optimization processed to replace instructions which would otherwise " +
                        "cause the same errors during runtime.")
                .paragraph("Example program:").source(instructions)
        this.run(instructions)
    }

    @Test
    @Throws(ProgramException::class)
    fun runSaveAndLoad() {
        val instructions = listOf(
                PUSH(BigInteger("10032157633811666223373963209218291332868453566459764444214480010939495088128").toByteArray()),
                PUSH(BigInteger.valueOf(1234).toByteArray()),
                SAVE(MEMORY),
                PUSH(byteArrayOf(10)),
                PUSH(BigInteger.valueOf(1234).toByteArray()),
                LOAD(MEMORY)
        )
        val stack = this.run(instructions).stack
        Documentation.of("instructions/save-and-load")
                .headingParagraph("SAVE & LOAD").paragraph(String.format("The SAVE operation removes two elements from the stack, using the top element as an" +
                        " address and the second element as a value to write into the area specified" +
                        " (%s, or %s)." +
                        " The LOAD  operation removes one element from the stack, using it as an" +
                        " address to read from the area specified (%s, %s, or %s).", MEMORY, DISK,
                        MEMORY, DISK, CALL_DATA))
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size.toLong(), 2)
        Assert.assertArrayEquals(stack.pop(), (instructions[0] as PUSH).bytes)
    }
}
