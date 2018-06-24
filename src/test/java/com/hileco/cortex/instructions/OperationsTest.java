package com.hileco.cortex.instructions;

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
import com.hileco.cortex.primitives.LayeredStack;
import org.junit.Ignore;
import org.junit.Test;

import static com.hileco.cortex.instructions.Operations.NO_DATA;

public class OperationsTest {

    private ProgramContext testProcessContext() {
        ProgramContext programContext = new ProgramContext();
        LayeredStack<byte[]> stack = programContext.getStack();
        stack.push(new byte[]{5});
        stack.push(new byte[]{6});
        stack.push(new byte[]{7});
        stack.push(new byte[]{8});
        programContext.setData("MEMORY", "0x1234", new ProgramContext.ProgramData(new byte[]{0x56, 0x78}));
        return programContext;
    }

    @Test
    public void runPush() {
        ProgramContext context = testProcessContext();
        Push.Operands operands = new Push.Operands();
        operands.bytes = new byte[]{127};
        Push push = new Push();
        push.execute(context, operands);
    }

    @Test
    public void runPop() {
        ProgramContext context = testProcessContext();
        Pop pop = new Pop();
        pop.execute(context, NO_DATA);
    }

    @Test
    public void runSwap() {
        ProgramContext context = testProcessContext();
        Swap.Operands operands = new Swap.Operands();
        operands.topOffsetLeft = 1;
        operands.topOffsetRight = 2;
        Swap swap = new Swap();
        swap.execute(context, operands);
    }

    @Test
    public void runDuplicate() {
        ProgramContext context = testProcessContext();
        Duplicate.Operands operands = new Duplicate.Operands();
        operands.topOffset = 1;
        Duplicate duplicate = new Duplicate();
        duplicate.execute(context, operands);
    }

    @Test
    public void runEquals() {
        ProgramContext context = testProcessContext();
        Equals equals = new Equals();
        equals.execute(context, NO_DATA);
    }

    @Test
    public void runGreaterThan() {
        ProgramContext context = testProcessContext();
        GreaterThan greaterThan = new GreaterThan();
        greaterThan.execute(context, NO_DATA);
    }

    @Test
    public void runLessThan() {
        ProgramContext context = testProcessContext();
        LessThan lessThan = new LessThan();
        lessThan.execute(context, NO_DATA);
    }

    @Test
    public void runIsZero() {
        ProgramContext context = testProcessContext();
        IsZero isZero = new IsZero();
        isZero.execute(context, NO_DATA);
    }

    @Test
    public void runBitwiseOr() {
        ProgramContext context = testProcessContext();
        BitwiseOr bitwiseOr = new BitwiseOr();
        bitwiseOr.execute(context, NO_DATA);
    }

    @Test
    public void runBitwiseXor() {
        ProgramContext context = testProcessContext();
        BitwiseXor bitwiseXor = new BitwiseXor();
        bitwiseXor.execute(context, NO_DATA);
    }

    @Test
    public void runBitwiseAnd() {
        ProgramContext context = testProcessContext();
        BitwiseAnd bitwiseAnd = new BitwiseAnd();
        bitwiseAnd.execute(context, NO_DATA);
    }

    @Test
    public void runBitwiseNot() {
        ProgramContext context = testProcessContext();
        BitwiseNot bitwiseNot = new BitwiseNot();
        bitwiseNot.execute(context, NO_DATA);
    }

    @Test
    public void runAdd() {
        ProgramContext context = testProcessContext();
        Add add = new Add();
        add.execute(context, NO_DATA);
    }

    @Test
    public void runSubtract() {
        ProgramContext context = testProcessContext();
        Subtract subtract = new Subtract();
        subtract.execute(context, NO_DATA);
    }

    @Test
    public void runMultiply() {
        ProgramContext context = testProcessContext();
        Multiply multiply = new Multiply();
        multiply.execute(context, NO_DATA);
    }

    @Test
    public void runDivide() {
        ProgramContext context = testProcessContext();
        Divide divide = new Divide();
        divide.execute(context, NO_DATA);
    }

    @Test
    public void runModulo() {
        ProgramContext context = testProcessContext();
        Modulo modulo = new Modulo();
        modulo.execute(context, NO_DATA);
    }

    @Ignore
    @Test
    public void runHashSha3() {
        ProgramContext context = testProcessContext();
        Hash.Operands operands = new Hash.Operands();
        operands.hashMethod = "SHA3";
        Hash hash = new Hash();
        hash.execute(context, operands);
    }

    @Test
    public void runHashNone() {
        ProgramContext context = testProcessContext();
        Hash.Operands operands = new Hash.Operands();
        operands.hashMethod = "NONE";
        Hash hash = new Hash();
        hash.execute(context, operands);
    }

    @Test(expected = IllegalArgumentException.class)
    public void runHashUnsupported() {
        ProgramContext context = testProcessContext();
        Hash.Operands operands = new Hash.Operands();
        operands.hashMethod = "Unsupported";
        Hash hash = new Hash();
        hash.execute(context, operands);
    }

    @Test
    public void runJump() {
        ProgramContext context = testProcessContext();
        Jump.Operands operands = new Jump.Operands();
        operands.destination = 1;
        Jump jump = new Jump();
        jump.execute(context, operands);
    }

    @Test
    public void runJumpDestination() {
        ProgramContext context = testProcessContext();
        JumpDestination jumpDestination = new JumpDestination();
        jumpDestination.execute(context, NO_DATA);
    }

    @Test
    public void runJumpIf() {
        ProgramContext context = testProcessContext();
        JumpIf.Operands operands = new JumpIf.Operands();
        operands.destination = 1;
        JumpIf jumpIf = new JumpIf();
        jumpIf.execute(context, operands);
    }

    @Test
    public void runExit() {
        ProgramContext context = testProcessContext();
        Exit exit = new Exit();
        exit.execute(context, NO_DATA);
    }

    @Test
    public void runLoad() {
        ProgramContext context = testProcessContext();
        Load.Operands operands = new Load.Operands();
        operands.group = "MEMORY";
        operands.address = "0x1234";
        Load load = new Load();
        load.execute(context, operands);
    }

    @Test
    public void runSave() {
        ProgramContext context = testProcessContext();
        Save.Operands operands = new Save.Operands();
        operands.group = "MEMORY";
        operands.address = "0x1234";
        Save save = new Save();
        save.execute(context, operands);
    }

}
