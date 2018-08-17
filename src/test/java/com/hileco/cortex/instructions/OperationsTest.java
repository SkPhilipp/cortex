package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.bits.BITWISE_AND;
import com.hileco.cortex.instructions.bits.BITWISE_NOT;
import com.hileco.cortex.instructions.bits.BITWISE_OR;
import com.hileco.cortex.instructions.bits.BITWISE_XOR;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.conditions.GREATER_THAN;
import com.hileco.cortex.instructions.conditions.IS_ZERO;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
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
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.function.BiConsumer;

public class OperationsTest {

    private <T extends Instruction> ProgramContext run(T instruction) throws ProgramException {
        return run(instruction, (processContext, programContext) -> {
        });
    }

    private <T extends Instruction> ProgramContext run(T instruction, BiConsumer<ProcessContext, ProgramContext> customSetup) throws
            ProgramException {
        Program program = new Program(Collections.singletonList(instruction));
        ProgramContext programContext = new ProgramContext(program);
        LayeredStack<byte[]> stack = programContext.getStack();
        stack.push(new byte[]{5});
        stack.push(new byte[]{6});
        stack.push(new byte[]{7});
        stack.push(new byte[]{8});
        programContext.getMemory().write(8, new byte[]{0x56, 0x78});
        ProcessContext processContext = new ProcessContext(programContext);
        customSetup.accept(processContext, programContext);
        ProgramRunner programRunner = new ProgramRunner(processContext);
        programRunner.run();
        return programContext;
    }

    @Test
    public void runPush() throws ProgramException {
        run(new PUSH(new byte[]{127}));
    }

    @Test
    public void runPop() throws ProgramException {
        run(new POP());
    }

    @Test
    public void runSwap() throws ProgramException {
        run(new SWAP(1, 2));
    }

    @Test
    public void runDuplicate() throws ProgramException {
        run(new DUPLICATE(1));
    }

    @Test
    public void runEquals() throws ProgramException {
        run(new EQUALS());
    }

    @Test
    public void runGreaterThan() throws ProgramException {
        run(new GREATER_THAN());
    }

    @Test
    public void runLessThan() throws ProgramException {
        run(new LESS_THAN());
    }

    @Test
    public void runIsZero() throws ProgramException {
        run(new IS_ZERO());
    }

    @Test
    public void runBitwiseOr() throws ProgramException {
        run(new BITWISE_OR());
    }

    @Test
    public void runBitwiseXor() throws ProgramException {
        run(new BITWISE_XOR());
    }

    @Test
    public void runBitwiseAnd() throws ProgramException {
        run(new BITWISE_AND());
    }

    @Test
    public void runBitwiseNot() throws ProgramException {
        run(new BITWISE_NOT());
    }

    @Test
    public void runAdd() throws ProgramException {
        run(new ADD());
    }

    @Test
    public void runAddOverflowing() throws ProgramException {
        ProgramContext result = run(new ADD(), (processContext, programContext) -> {
            programContext.getStack().push(ProcessContext.NUMERICAL_LIMIT.toByteArray());
            programContext.getStack().push(BigInteger.TEN.toByteArray());
        });
        Assert.assertArrayEquals(BigInteger.valueOf(9).toByteArray(), result.getStack().pop());
    }

    @Test
    public void runSubtract() throws ProgramException {
        run(new SUBTRACT());
    }

    @Test
    public void runMultiply() throws ProgramException {
        run(new MULTIPLY());
    }

    @Test
    public void runMultiplyOverflowing() throws ProgramException {
        ProgramContext result = run(new MULTIPLY(), (processContext, programContext) -> {
            programContext.getStack().push(ProcessContext.NUMERICAL_LIMIT.toByteArray());
            programContext.getStack().push(BigInteger.TEN.toByteArray());
        });
        BigInteger expected = ProcessContext.NUMERICAL_LIMIT.multiply(BigInteger.TEN).mod(ProcessContext.NUMERICAL_LIMIT.add(BigInteger.ONE));
        Assert.assertArrayEquals(expected.toByteArray(), result.getStack().pop());
    }

    @Test
    public void runDivide() throws ProgramException {
        run(new DIVIDE());
    }

    @Test
    public void runModulo() throws ProgramException {
        run(new MODULO());
    }

    @Test
    public void runHashSha512() throws ProgramException {
        run(new HASH("SHA-512"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void runHashUnsupported() throws ProgramException {
        run(new HASH("Unsupported"));
    }

    @Test(expected = ProgramException.class)
    public void runJump() throws ProgramException {
        run(new JUMP());
    }

    @Test
    public void runJumpDestination() throws ProgramException {
        run(new JUMP_DESTINATION());
    }

    @Test(expected = ProgramException.class)
    public void runJumpIf() throws ProgramException {
        run(new JUMP_IF());
    }

    @Test
    public void runExit() throws ProgramException {
        run(new EXIT());
    }

    @Test
    public void runLoad() throws ProgramException {
        run(new LOAD(ProgramStoreZone.MEMORY));
    }

    @Test
    public void runSave() throws ProgramException {
        run(new SAVE(ProgramStoreZone.MEMORY));
    }
}
