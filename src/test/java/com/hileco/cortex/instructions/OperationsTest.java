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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigInteger;

import static com.hileco.cortex.instructions.Operations.NO_DATA;

public class OperationsTest {

    private ProcessContext processContext;
    private ProgramContext programContext;

    @Before
    public void setup() {
        Program program = Mockito.mock(Program.class);
        programContext = new ProgramContext(program);
        LayeredStack<byte[]> stack = programContext.getStack();
        stack.push(new byte[]{5});
        stack.push(new byte[]{6});
        stack.push(new byte[]{7});
        stack.push(new byte[]{8});
        programContext.getMemoryStorage().put(BigInteger.valueOf(8L), new ProgramData(new byte[]{0x56, 0x78}));
        processContext = new ProcessContext(programContext);
    }

    @Test
    public void runPush() {

        // TODO: Perform setup & run using the actual instruction as the content of the program, instead of setting up a fake mock program
        Push.Operands operands = new Push.Operands();
        operands.bytes = new byte[]{127};
        Push push = new Push();
        push.execute(processContext, programContext, operands);
    }

    @Test
    public void runPop() {
        Pop pop = new Pop();
        pop.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runSwap() {
        Swap.Operands operands = new Swap.Operands();
        operands.topOffsetLeft = 1;
        operands.topOffsetRight = 2;
        Swap swap = new Swap();
        swap.execute(processContext, programContext, operands);
    }

    @Test
    public void runDuplicate() {
        Duplicate.Operands operands = new Duplicate.Operands();
        operands.topOffset = 1;
        Duplicate duplicate = new Duplicate();
        duplicate.execute(processContext, programContext, operands);
    }

    @Test
    public void runEquals() {
        Equals equals = new Equals();
        equals.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runGreaterThan() {
        GreaterThan greaterThan = new GreaterThan();
        greaterThan.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runLessThan() {
        LessThan lessThan = new LessThan();
        lessThan.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runIsZero() {
        IsZero isZero = new IsZero();
        isZero.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runBitwiseOr() {
        BitwiseOr bitwiseOr = new BitwiseOr();
        bitwiseOr.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runBitwiseXor() {
        BitwiseXor bitwiseXor = new BitwiseXor();
        bitwiseXor.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runBitwiseAnd() {
        BitwiseAnd bitwiseAnd = new BitwiseAnd();
        bitwiseAnd.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runBitwiseNot() {
        BitwiseNot bitwiseNot = new BitwiseNot();
        bitwiseNot.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runAdd() {
        Add add = new Add();
        add.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runAddOverflowing() {
        programContext.getStack().push(processContext.getOverflowLimit().toByteArray());
        programContext.getStack().push(BigInteger.ONE.toByteArray());
        Add add = new Add();
        add.execute(processContext, programContext, NO_DATA);
        Assert.assertArrayEquals(BigInteger.ZERO.toByteArray(), programContext.getStack().pop());
    }

    @Test
    public void runSubtract() {
        Subtract subtract = new Subtract();
        subtract.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runMultiply() {
        Multiply multiply = new Multiply();
        multiply.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runMultiplyOverflowing() {
        programContext.getStack().push(processContext.getOverflowLimit().toByteArray());
        programContext.getStack().push(BigInteger.TEN.toByteArray());
        Multiply multiply = new Multiply();
        multiply.execute(processContext, programContext, NO_DATA);
        BigInteger expected = processContext.getOverflowLimit().multiply(BigInteger.TEN).mod(processContext.getOverflowLimit().add(BigInteger.ONE));
        Assert.assertArrayEquals(expected.toByteArray(), programContext.getStack().pop());
    }

    @Test
    public void runDivide() {
        Divide divide = new Divide();
        divide.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runModulo() {
        Modulo modulo = new Modulo();
        modulo.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runHashSha512() {
        Hash.Operands operands = new Hash.Operands();
        operands.hashMethod = "SHA-512";
        Hash hash = new Hash();
        hash.execute(processContext, programContext, operands);
    }

    @Test(expected = IllegalArgumentException.class)
    public void runHashUnsupported() {
        Hash.Operands operands = new Hash.Operands();
        operands.hashMethod = "Unsupported";
        Hash hash = new Hash();
        hash.execute(processContext, programContext, operands);
    }

    @Test
    public void runJump() {
        Jump jump = new Jump();
        jump.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runJumpDestination() {
        JumpDestination jumpDestination = new JumpDestination();
        jumpDestination.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runJumpIf() {
        JumpIf jumpIf = new JumpIf();
        jumpIf.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runExit() {
        Exit exit = new Exit();
        exit.execute(processContext, programContext, NO_DATA);
    }

    @Test
    public void runLoad() {
        Load.Operands operands = new Load.Operands();
        operands.programStoreZone = ProgramStoreZone.MEMORY;
        Load load = new Load();
        load.execute(processContext, programContext, operands);
    }

    @Test
    public void runSave() {
        Save.Operands operands = new Save.Operands();
        operands.programStoreZone = ProgramStoreZone.MEMORY;
        Save save = new Save();
        save.execute(processContext, programContext, operands);
    }

}
