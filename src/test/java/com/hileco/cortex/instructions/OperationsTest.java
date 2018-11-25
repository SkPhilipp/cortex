package com.hileco.cortex.instructions;

import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.bits.BITWISE_AND;
import com.hileco.cortex.instructions.bits.BITWISE_NOT;
import com.hileco.cortex.instructions.bits.BITWISE_OR;
import com.hileco.cortex.instructions.bits.BITWISE_XOR;
import com.hileco.cortex.instructions.conditions.ConditionInstruction;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.conditions.GREATER_THAN;
import com.hileco.cortex.instructions.conditions.IS_ZERO;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
import com.hileco.cortex.instructions.debug.HALT;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.io.SAVE;
import com.hileco.cortex.instructions.jumps.EXIT;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.DIVIDE;
import com.hileco.cortex.instructions.math.HASH;
import com.hileco.cortex.instructions.math.MODULO;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.math.SUBTRACT;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.POP;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.instructions.stack.SWAP;
import com.hileco.cortex.vm.Program;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.VirtualMachine;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_LIMIT_REACHED;
import static com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA;
import static com.hileco.cortex.vm.ProgramStoreZone.DISK;
import static com.hileco.cortex.vm.ProgramStoreZone.MEMORY;
import static com.hileco.cortex.vm.VirtualMachine.NUMERICAL_LIMIT;

public class OperationsTest {

    private ProgramContext run(List<? extends Instruction> instruction) throws ProgramException {
        return this.run(instruction, (processContext, programContext) -> {
        });
    }

    @SuppressWarnings("unchecked")
    private ProgramContext run(List<? extends Instruction> instructions, BiConsumer<VirtualMachine, ProgramContext> customSetup) throws
            ProgramException {
        var program = new Program((List<Instruction>) instructions);
        var programContext = new ProgramContext(program);
        var processContext = new VirtualMachine(programContext);
        customSetup.accept(processContext, programContext);
        var programRunner = new ProgramRunner(processContext);
        programRunner.run();
        return programContext;
    }

    @Test
    public void runPush() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{1}),
                new PUSH(new byte[]{10}),
                new PUSH(new byte[]{100}));
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/push")
                .headingParagraph("PUSH").paragraph("The PUSH operation adds one element to top of the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 3);
        Assert.assertArrayEquals(stack.pop(), instructions.get(2).getBytes());
        Assert.assertArrayEquals(stack.pop(), instructions.get(1).getBytes());
        Assert.assertArrayEquals(stack.pop(), instructions.get(0).getBytes());
    }

    @Test
    public void runPop() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{1}),
                new PUSH(new byte[]{100}),
                new POP());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/pop")
                .headingParagraph("POP").paragraph("The POP operation removes the top element from the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), ((PUSH) instructions.get(0)).getBytes());
    }

    @Test
    public void runSwap() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{100}),
                new PUSH(new byte[]{1}),
                new SWAP(0, 1));
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/swap")
                .headingParagraph("SWAP").paragraph("The SWAP operation swaps two elements on the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 2);
        Assert.assertArrayEquals(stack.pop(), ((PUSH) instructions.get(0)).getBytes());
        Assert.assertArrayEquals(stack.pop(), ((PUSH) instructions.get(1)).getBytes());
    }

    @Test
    public void runDuplicate() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{100}),
                new DUPLICATE(0));
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/duplicate")
                .headingParagraph("DUPLICATE").paragraph("The DUPLICATE operation adds a duplicate of an element on the stack, to the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 2);
        Assert.assertArrayEquals(stack.pop(), ((PUSH) instructions.get(0)).getBytes());
        Assert.assertArrayEquals(stack.pop(), ((PUSH) instructions.get(0)).getBytes());
    }

    @Test
    public void runEquals() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{100}),
                new PUSH(new byte[]{100}),
                new EQUALS());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/equals")
                .headingParagraph("EQUALS").paragraph("The EQUALS operation removes two elements from the stack, then adds a 1 or 0 to the stack" +
                                                              " depending on whether the top element was equal to the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE);
    }

    @Test
    public void runGreaterThan() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{10}),
                new PUSH(new byte[]{100}),
                new GREATER_THAN());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/greater-than")
                .headingParagraph("GREATER_THAN").paragraph("The GREATER_THAN operation removes two elements from the stack, then adds a 1 or 0 to the stack" +
                                                                    " depending on whether the top element was greater than the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE);
    }

    @Test
    public void runLessThan() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{100}),
                new PUSH(new byte[]{10}),
                new LESS_THAN());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/less-than")
                .headingParagraph("LESS_THAN").paragraph("The LESS_THAN operation removes two elements from the stack, then adds a 1 or 0 to the stack" +
                                                                 " depending on whether the top element was less than the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE);
    }

    @Test
    public void runIsZero() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{0}),
                new IS_ZERO());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/is-zero")
                .headingParagraph("IS_ZERO").paragraph("The IS_ZERO operation removes the top element of the stack then adds a 1 or 0 to the stack" +
                                                               " depending on whether the element was equal to 0.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE);
    }

    @Test
    public void runBitwiseOr() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{0b0101}),
                new PUSH(new byte[]{0b0011}),
                new BITWISE_OR());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/bitwise-or")
                .headingParagraph("BITWISE_OR").paragraph("The BITWISE_OR operation performs a bitwise OR operation on each bit of the top two elements on" +
                                                                  " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), new byte[]{0b0111});
    }

    @Test
    public void runBitwiseXor() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{0b0101}),
                new PUSH(new byte[]{0b0011}),
                new BITWISE_XOR());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/bitwise-xor")
                .headingParagraph("BITWISE_XOR").paragraph("The BITWISE_XOR operation performs a bitwise XOR operation on each bit of the top two elements on" +
                                                                   " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), new byte[]{0b0110});
    }

    @Test
    public void runBitwiseAnd() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{0b0101}),
                new PUSH(new byte[]{0b0011}),
                new BITWISE_AND());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/bitwise-and")
                .headingParagraph("BITWISE_AND").paragraph("The BITWISE_AND operation performs a bitwise AND operation on each bit of the top two elements on" +
                                                                   " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), new byte[]{0b0001});
    }

    @Test
    public void runBitwiseNot() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{0b1111111}),
                new BITWISE_NOT());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/bitwise-not")
                .headingParagraph("BITWISE_NOT").paragraph("The BITWISE_NOT operation performs logical negation on each bit of the top element on the stack")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), new byte[]{0});
    }

    @Test
    public void runAdd() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{100}),
                new PUSH(new byte[]{1}),
                new ADD());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/add")
                .headingParagraph("ADD").paragraph(String.format("The ADD operation removes two elements from the stack, adds them together and puts the " +
                                                                         "result on the stack. (This result may overflow if it would have been larger " +
                                                                         "than %s)", NUMERICAL_LIMIT))
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), new byte[]{101});
    }

    @Test
    public void runAddOverflowing() throws ProgramException {
        var instructions = List.of(
                new PUSH(NUMERICAL_LIMIT.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new ADD());
        var stack = this.run(instructions).getStack();
        Assert.assertArrayEquals(BigInteger.valueOf(9).toByteArray(), stack.pop());
    }

    @Test
    public void runSubtract() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{1}),
                new PUSH(new byte[]{100}),
                new SUBTRACT());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/subtract")
                .headingParagraph("SUBTRACT").paragraph("The SUBTRACT operation removes two elements from the stack, subtracts the second element from the " +
                                                                "top element and puts the result on the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), new byte[]{99});
    }

    @Test
    public void runMultiply() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{10}),
                new PUSH(new byte[]{10}),
                new MULTIPLY());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/multiply")
                .headingParagraph("MULTIPLY").paragraph(String.format("The MULTIPLY operation removes two elements from the stack, multiplies them and puts" +
                                                                              " the result on the stack. (This result may overflow if it would have been " +
                                                                              "larger" +
                                                                              " than %s)", NUMERICAL_LIMIT))
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), new byte[]{100});
    }

    @Test
    public void runMultiplyOverflowing() throws ProgramException {
        var instructions = List.of(
                new PUSH(NUMERICAL_LIMIT.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new MULTIPLY());
        var stack = this.run(instructions).getStack();
        var expected = NUMERICAL_LIMIT.multiply(BigInteger.TEN).mod(NUMERICAL_LIMIT.add(BigInteger.ONE));
        Assert.assertArrayEquals(expected.toByteArray(), stack.pop());
    }

    @Test
    public void runDivide() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{20}),
                new PUSH(new byte[]{100}),
                new DIVIDE());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/divide")
                .headingParagraph("DIVIDE").paragraph(String.format("The DIVIDE operation removes two elements from the stack, divides them with the top " +
                                                                            "element being the dividend and the second element being the divisor. It puts the" +
                                                                            " " +
                                                                            "resulting quotient on the stack. (This result may overflow if it would have been" +
                                                                            " " +
                                                                            "larger than %s)", NUMERICAL_LIMIT))
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), new byte[]{5});
    }

    @Test
    public void runModulo() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{3}),
                new PUSH(new byte[]{10}),
                new MODULO());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/modulo")
                .headingParagraph("MODULO").paragraph(String.format("The MODULO operation removes two elements from the stack, divides them with the top " +
                                                                            "element being the dividend and the second element being the divisor. It puts the" +
                                                                            " " +
                                                                            "resulting remainder on the stack. (This result may overflow if it would have " +
                                                                            "been " +
                                                                            "larger than %s)", NUMERICAL_LIMIT))
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertArrayEquals(stack.pop(), new byte[]{1});
    }

    @Test
    public void runHashSha512() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{123}),
                new HASH("SHA-512"));
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/hash")
                .headingParagraph("HASH").paragraph("The HASH operation removes one element from the stack, performs the desired hashing " +
                                                            "method on it and adds the resulting hash to the stack")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
    }

    @Test
    public void runJump() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{3}),
                new JUMP(),
                new PUSH(new byte[]{100}),
                new JUMP_DESTINATION());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/jump")
                .headingParagraph("JUMP").paragraph("The JUMP operation removes one element from the stack, using it to set the instruction position of the " +
                                                            "program itself. JUMPs may only result in instruction positions which point to a JUMP_DESTINATION" +
                                                            " " +
                                                            "instruction. The JUMP_DESTINATION by itself is equal to a NOOP.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 0);
    }

    @Test
    public void runJumpIf() throws ProgramException {
        var instructions = List.of(new PUSH(new byte[]{1}),
                                   new PUSH(new byte[]{4}),
                                   new JUMP_IF(),
                                   new PUSH(new byte[]{100}),
                                   new JUMP_DESTINATION());
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/jump-if")
                .headingParagraph("JUMP_IF").paragraph("The JUMP_IF operation removes two elements from the stack, using the top element to set the " +
                                                               "instruction position of the program itself, depending on whether the second element is a " +
                                                               "positive value. JUMP_IFs may only result in instruction positions which point to a " +
                                                               "JUMP_DESTINATION instruction. The JUMP_DESTINATION by itself is equal to a NOOP.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 0);
    }

    @Test
    public void runNoop() throws ProgramException {
        var instructions = List.of(new NOOP());
        this.run(instructions);
        Documentation.of("instructions/noop")
                .headingParagraph("NOOP").paragraph("This operation does nothing. It is generally only used within optimization processes to replace " +
                                                            "instructions instead of having to remove them. This allows all JUMP-related instructions remain " +
                                                            "functional.")
                .paragraph("Example program:").source(instructions);
    }

    @Test
    public void runExit() throws ProgramException {
        var instructions = List.of(
                new PUSH(new byte[]{10}),
                new EXIT(),
                new PUSH(new byte[]{100}));
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/exit")
                .headingParagraph("EXIT").paragraph("The EXIT operation ends execution of the program.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 1);
        Assert.assertNotEquals(stack.pop(), new byte[]{10});
    }

    @Test(expected = ProgramException.class)
    public void runHalt() throws ProgramException {
        var instructions = List.of(
                new HALT(STACK_LIMIT_REACHED));
        Documentation.of("instructions/halt")
                .headingParagraph("HALT").paragraph("The HALT operation cancels execution of the entire process, and provides a reason for doing so. It is " +
                                                            "generally only used within optimization processed to replace instructions which would otherwise " +
                                                            "cause the same errors during runtime.")
                .paragraph("Example program:").source(instructions);
        this.run(instructions);
    }

    @Test
    public void runSaveAndLoad() throws ProgramException {
        var instructions = List.of(
                new PUSH(new BigInteger("10032157633811666223373963209218291332868453566459764444214480010939495088128").toByteArray()),
                new PUSH(BigInteger.valueOf(1234).toByteArray()),
                new SAVE(MEMORY),
                new PUSH(new byte[]{10}),
                new PUSH(BigInteger.valueOf(1234).toByteArray()),
                new LOAD(MEMORY)
        );
        var stack = this.run(instructions).getStack();
        Documentation.of("instructions/save-and-load")
                .headingParagraph("SAVE & LOAD").paragraph(String.format("The SAVE operation removes two elements from the stack, using the top element as an" +
                                                                                 " address and the second element as a value to write into the area specified" +
                                                                                 " (%s, or %s)." +
                                                                                 " The LOAD  operation removes one element from the stack, using it as an" +
                                                                                 " address to read from the area specified (%s, %s, or %s).", MEMORY, DISK,
                                                                         MEMORY, DISK, CALL_DATA))
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack);
        Assert.assertEquals(stack.size(), 2);
        Assert.assertArrayEquals(stack.pop(), ((PUSH) instructions.get(0)).getBytes());
    }
}
