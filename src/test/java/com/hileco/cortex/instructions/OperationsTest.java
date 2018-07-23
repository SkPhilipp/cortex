package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Operations.Add;
import com.hileco.cortex.instructions.Operations.BitwiseAnd;
import com.hileco.cortex.instructions.Operations.BitwiseNot;
import com.hileco.cortex.instructions.Operations.BitwiseOr;
import com.hileco.cortex.instructions.Operations.BitwiseXor;
import com.hileco.cortex.instructions.Operations.Divide;
import com.hileco.cortex.instructions.Operations.Duplicate;
import com.hileco.cortex.instructions.Operations.Equals;
import com.hileco.cortex.instructions.Operations.Exit;
import com.hileco.cortex.instructions.Operations.GreaterThan;
import com.hileco.cortex.instructions.Operations.Hash;
import com.hileco.cortex.instructions.Operations.IsZero;
import com.hileco.cortex.instructions.Operations.Jump;
import com.hileco.cortex.instructions.Operations.JumpDestination;
import com.hileco.cortex.instructions.Operations.JumpIf;
import com.hileco.cortex.instructions.Operations.LessThan;
import com.hileco.cortex.instructions.Operations.Load;
import com.hileco.cortex.instructions.Operations.Modulo;
import com.hileco.cortex.instructions.Operations.Multiply;
import com.hileco.cortex.instructions.Operations.Pop;
import com.hileco.cortex.instructions.Operations.Push;
import com.hileco.cortex.instructions.Operations.Save;
import com.hileco.cortex.instructions.Operations.Subtract;
import com.hileco.cortex.instructions.Operations.Swap;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.function.BiConsumer;

import static com.hileco.cortex.instructions.Operations.NO_DATA;

public class OperationsTest {


    private <T extends Operations.Operation<V>, V> ProgramContext run(T operation, V operands) throws ProgramException {
        return run(operation, operands, (processContext, programContext) -> {
        });
    }

    private <T extends Operations.Operation<V>, V> ProgramContext run(T operation, V operands, BiConsumer<ProcessContext, ProgramContext> customSetup) throws
            ProgramException {
        Program program = new Program();
        program.setInstructions(Collections.singletonList(new Instruction<>(operation, operands)));
        ProgramContext programContext = new ProgramContext(program);
        LayeredStack<byte[]> stack = programContext.getStack();
        stack.push(new byte[]{5});
        stack.push(new byte[]{6});
        stack.push(new byte[]{7});
        stack.push(new byte[]{8});
        programContext.getMemoryStorage().put(BigInteger.valueOf(8L), new ProgramData(new byte[]{0x56, 0x78}));
        ProcessContext processContext = new ProcessContext(programContext);
        customSetup.accept(processContext, programContext);
        ProgramRunner programRunner = new ProgramRunner(processContext);
        programRunner.run();
        return programContext;
    }

    @Test
    public void runPush() throws ProgramException {
        Push.Operands operands = new Push.Operands();
        operands.bytes = new byte[]{127};
        Push push = new Push();
        run(push, operands);
    }

    @Test
    public void runPop() throws ProgramException {
        Pop pop = new Pop();
        run(pop, NO_DATA);
    }

    @Test
    public void runSwap() throws ProgramException {
        Swap.Operands operands = new Swap.Operands();
        operands.topOffsetLeft = 1;
        operands.topOffsetRight = 2;
        Swap swap = new Swap();
        run(swap, operands);
    }

    @Test
    public void runDuplicate() throws ProgramException {
        Duplicate.Operands operands = new Duplicate.Operands();
        operands.topOffset = 1;
        Duplicate duplicate = new Duplicate();
        run(duplicate, operands);
    }

    @Test
    public void runEquals() throws ProgramException {
        Equals equals = new Equals();
        run(equals, NO_DATA);
    }

    @Test
    public void runGreaterThan() throws ProgramException {
        GreaterThan greaterThan = new GreaterThan();
        run(greaterThan, NO_DATA);
    }

    @Test
    public void runLessThan() throws ProgramException {
        LessThan lessThan = new LessThan();
        run(lessThan, NO_DATA);
    }

    @Test
    public void runIsZero() throws ProgramException {
        IsZero isZero = new IsZero();
        run(isZero, NO_DATA);
    }

    @Test
    public void runBitwiseOr() throws ProgramException {
        BitwiseOr bitwiseOr = new BitwiseOr();
        run(bitwiseOr, NO_DATA);
    }

    @Test
    public void runBitwiseXor() throws ProgramException {
        BitwiseXor bitwiseXor = new BitwiseXor();
        run(bitwiseXor, NO_DATA);
    }

    @Test
    public void runBitwiseAnd() throws ProgramException {
        BitwiseAnd bitwiseAnd = new BitwiseAnd();
        run(bitwiseAnd, NO_DATA);
    }

    @Test
    public void runBitwiseNot() throws ProgramException {
        BitwiseNot bitwiseNot = new BitwiseNot();
        run(bitwiseNot, NO_DATA);
    }

    @Test
    public void runAdd() throws ProgramException {
        Add add = new Add();
        run(add, NO_DATA);
    }

    @Test
    public void runAddOverflowing() throws ProgramException {
        Add add = new Add();
        ProgramContext result = run(add, NO_DATA, (processContext, programContext) -> {
            programContext.getStack().push(ProcessContext.NUMERICAL_LIMIT.toByteArray());
            programContext.getStack().push(BigInteger.TEN.toByteArray());
        });
        Assert.assertArrayEquals(BigInteger.valueOf(9).toByteArray(), result.getStack().pop());
    }

    @Test
    public void runSubtract() throws ProgramException {
        Subtract subtract = new Subtract();
        run(subtract, NO_DATA);
    }

    @Test
    public void runMultiply() throws ProgramException {
        Multiply multiply = new Multiply();
        run(multiply, NO_DATA);
    }

    @Test
    public void runMultiplyOverflowing() throws ProgramException {
        Multiply multiply = new Multiply();
        ProgramContext result = run(multiply, NO_DATA, (processContext, programContext) -> {
            programContext.getStack().push(ProcessContext.NUMERICAL_LIMIT.toByteArray());
            programContext.getStack().push(BigInteger.TEN.toByteArray());
        });
        BigInteger expected = ProcessContext.NUMERICAL_LIMIT.multiply(BigInteger.TEN).mod(ProcessContext.NUMERICAL_LIMIT.add(BigInteger.ONE));
        Assert.assertArrayEquals(expected.toByteArray(), result.getStack().pop());
    }

    @Test
    public void runDivide() throws ProgramException {
        Divide divide = new Divide();
        run(divide, NO_DATA);
    }

    @Test
    public void runModulo() throws ProgramException {
        Modulo modulo = new Modulo();
        run(modulo, NO_DATA);
    }

    @Test
    public void runHashSha512() throws ProgramException {
        Hash.Operands operands = new Hash.Operands();
        operands.hashMethod = "SHA-512";
        Hash hash = new Hash();
        run(hash, operands);
    }

    @Test(expected = IllegalArgumentException.class)
    public void runHashUnsupported() throws ProgramException {
        Hash.Operands operands = new Hash.Operands();
        operands.hashMethod = "Unsupported";
        Hash hash = new Hash();
        run(hash, operands);
    }

    @Test(expected = ProgramException.class)
    public void runJump() throws ProgramException {
        Jump jump = new Jump();
        run(jump, NO_DATA);
    }

    @Test
    public void runJumpDestination() throws ProgramException {
        JumpDestination jumpDestination = new JumpDestination();
        run(jumpDestination, NO_DATA);
    }

    @Test(expected = ProgramException.class)
    public void runJumpIf() throws ProgramException {
        JumpIf jumpIf = new JumpIf();
        run(jumpIf, NO_DATA);
    }

    @Test
    public void runExit() throws ProgramException {
        Exit exit = new Exit();
        run(exit, NO_DATA);
    }

    @Test
    public void runLoad() throws ProgramException {
        Load.Operands operands = new Load.Operands();
        operands.programStoreZone = ProgramStoreZone.MEMORY;
        Load load = new Load();
        run(load, operands);
    }

    @Test
    public void runSave() throws ProgramException {
        Save.Operands operands = new Save.Operands();
        operands.programStoreZone = ProgramStoreZone.MEMORY;
        Save save = new Save();
        run(save, operands);
    }

}
