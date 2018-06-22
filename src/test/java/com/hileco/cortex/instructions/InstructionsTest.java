package com.hileco.cortex.instructions;

import com.hileco.cortex.instructions.Instructions.Add;
import com.hileco.cortex.instructions.Instructions.BitwiseAnd;
import com.hileco.cortex.instructions.Instructions.BitwiseNot;
import com.hileco.cortex.instructions.Instructions.BitwiseOr;
import com.hileco.cortex.instructions.Instructions.BitwiseXor;
import com.hileco.cortex.instructions.Instructions.Divide;
import com.hileco.cortex.instructions.Instructions.Duplicate;
import com.hileco.cortex.instructions.Instructions.Equals;
import com.hileco.cortex.instructions.Instructions.Exit;
import com.hileco.cortex.instructions.Instructions.GreaterThan;
import com.hileco.cortex.instructions.Instructions.Hash;
import com.hileco.cortex.instructions.Instructions.IsZero;
import com.hileco.cortex.instructions.Instructions.Jump;
import com.hileco.cortex.instructions.Instructions.JumpDestination;
import com.hileco.cortex.instructions.Instructions.JumpIf;
import com.hileco.cortex.instructions.Instructions.LessThan;
import com.hileco.cortex.instructions.Instructions.Load;
import com.hileco.cortex.instructions.Instructions.Modulo;
import com.hileco.cortex.instructions.Instructions.Multiply;
import com.hileco.cortex.instructions.Instructions.NoData;
import com.hileco.cortex.instructions.Instructions.Pop;
import com.hileco.cortex.instructions.Instructions.Push;
import com.hileco.cortex.instructions.Instructions.Save;
import com.hileco.cortex.instructions.Instructions.Subtract;
import com.hileco.cortex.instructions.Instructions.Swap;
import com.hileco.cortex.primitives.LayeredMap;
import com.hileco.cortex.primitives.LayeredStack;
import com.hileco.cortex.primitives.ProcessContext;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

public class InstructionsTest {

    private NoData NO_DATA = new NoData();

    private ProcessContext testProcessContext() {
        ProcessContext processContext = new ProcessContext();
        LayeredStack<byte[]> stack = processContext.getStack();
        stack.push(new byte[]{5});
        stack.push(new byte[]{6});
        stack.push(new byte[]{7});
        stack.push(new byte[]{8});
        Map<String, LayeredMap<String, byte[]>> storage = processContext.getStorage();
        storage.put("MEMORY", new LayeredMap<>());
        storage.get("MEMORY").put("0x1234", new byte[]{0x56, 0x78});
        return processContext;
    }

    @Test
    public void runPush() {
        ProcessContext context;
        context = testProcessContext();
        Push.Data data = new Push.Data();
        data.bytes = new byte[]{127};
        Push push = new Push();
        push.execute(context, data);
    }

    @Test
    public void runPop() {
        ProcessContext context = testProcessContext();
        Pop pop = new Pop();
        pop.execute(context, NO_DATA);
    }

    @Test
    public void runSwap() {
        ProcessContext context = testProcessContext();
        Swap.Data data = new Swap.Data();
        data.topOffsetLeft = 1;
        data.topOffsetRight = 2;
        Swap swap = new Swap();
        swap.execute(context, data);
    }

    @Test
    public void runDuplicate() {
        ProcessContext context = testProcessContext();
        Duplicate.Data data = new Duplicate.Data();
        data.topOffset = 1;
        Duplicate duplicate = new Duplicate();
        duplicate.execute(context, data);
    }

    @Test
    public void runEquals() {
        ProcessContext context = testProcessContext();
        Equals equals = new Equals();
        equals.execute(context, NO_DATA);
    }

    @Test
    public void runGreaterThan() {
        ProcessContext context = testProcessContext();
        GreaterThan greaterThan = new GreaterThan();
        greaterThan.execute(context, NO_DATA);
    }

    @Test
    public void runLessThan() {
        ProcessContext context = testProcessContext();
        LessThan lessThan = new LessThan();
        lessThan.execute(context, NO_DATA);
    }

    @Test
    public void runIsZero() {
        ProcessContext context = testProcessContext();
        IsZero isZero = new IsZero();
        isZero.execute(context, NO_DATA);
    }

    @Test
    public void runBitwiseOr() {
        ProcessContext context = testProcessContext();
        BitwiseOr bitwiseOr = new BitwiseOr();
        bitwiseOr.execute(context, NO_DATA);
    }

    @Test
    public void runBitwiseXor() {
        ProcessContext context = testProcessContext();
        BitwiseXor bitwiseXor = new BitwiseXor();
        bitwiseXor.execute(context, NO_DATA);
    }

    @Test
    public void runBitwiseAnd() {
        ProcessContext context = testProcessContext();
        BitwiseAnd bitwiseAnd = new BitwiseAnd();
        bitwiseAnd.execute(context, NO_DATA);
    }

    @Test
    public void runBitwiseNot() {
        ProcessContext context = testProcessContext();
        BitwiseNot bitwiseNot = new BitwiseNot();
        bitwiseNot.execute(context, NO_DATA);
    }

    @Test
    public void runAdd() {
        ProcessContext context = testProcessContext();
        Add add = new Add();
        add.execute(context, NO_DATA);
    }

    @Test
    public void runSubtract() {
        ProcessContext context = testProcessContext();
        Subtract subtract = new Subtract();
        subtract.execute(context, NO_DATA);
    }

    @Test
    public void runMultiply() {
        ProcessContext context = testProcessContext();
        Multiply multiply = new Multiply();
        multiply.execute(context, NO_DATA);
    }

    @Test
    public void runDivide() {
        ProcessContext context = testProcessContext();
        Divide divide = new Divide();
        divide.execute(context, NO_DATA);
    }

    @Test
    public void runModulo() {
        ProcessContext context = testProcessContext();
        Modulo modulo = new Modulo();
        modulo.execute(context, NO_DATA);
    }

    @Ignore
    @Test
    public void runHashSha3() {
        ProcessContext context = testProcessContext();
        Hash.Data data = new Hash.Data();
        data.hashMethod = "SHA3";
        Hash hash = new Hash();
        hash.execute(context, data);
    }

    @Test
    public void runHashNone() {
        ProcessContext context = testProcessContext();
        Hash.Data data = new Hash.Data();
        data.hashMethod = "NONE";
        Hash hash = new Hash();
        hash.execute(context, data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void runHashUnsupported() {
        ProcessContext context = testProcessContext();
        Hash.Data data = new Hash.Data();
        data.hashMethod = "Unsupported";
        Hash hash = new Hash();
        hash.execute(context, data);
    }

    @Test
    public void runJump() {
        ProcessContext context = testProcessContext();
        Jump.Data data = new Jump.Data();
        data.destination = 1;
        Jump jump = new Jump();
        jump.execute(context, data);
    }

    @Test
    public void runJumpDestination() {
        ProcessContext context = testProcessContext();
        JumpDestination jumpDestination = new JumpDestination();
        jumpDestination.execute(context, NO_DATA);
    }

    @Test
    public void runJumpIf() {
        ProcessContext context = testProcessContext();
        JumpIf.Data data = new JumpIf.Data();
        data.destination = 1;
        JumpIf jumpIf = new JumpIf();
        jumpIf.execute(context, data);
    }

    @Test
    public void runExit() {
        ProcessContext context = testProcessContext();
        Exit exit = new Exit();
        exit.execute(context, NO_DATA);
    }

    @Test
    public void runLoad() {
        ProcessContext context = testProcessContext();
        Load.Data data = new Load.Data();
        data.group = "MEMORY";
        data.address = "0x1234";
        Load load = new Load();
        load.execute(context, data);
    }

    @Test
    public void runSave() {
        ProcessContext context = testProcessContext();
        Save.Data data = new Save.Data();
        data.group = "MEMORY";
        data.address = "0x1234";
        Save save = new Save();
        save.execute(context, data);
    }

}
