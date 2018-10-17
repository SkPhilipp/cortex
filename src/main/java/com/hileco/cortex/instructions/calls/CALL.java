package com.hileco.cortex.instructions.calls;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.Pair;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.MEMORY;
import static com.hileco.cortex.context.ProgramZone.PROGRAM_CONTEXT;
import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.CALL_RECIPIENT_MISSING;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode
public class CALL implements Instruction {

    public static final StackParameter RECIPIENT_ADDRESS = new StackParameter("recipientAddress", 0);
    public static final StackParameter VALUE_TRANSFERRED = new StackParameter("valueTransferred", 1);
    public static final StackParameter IN_OFFSET = new StackParameter("inOffset", 2);
    public static final StackParameter IN_SIZE = new StackParameter("inSize", 3);
    public static final StackParameter OUT_OFFSET = new StackParameter("outOffset", 4);
    public static final StackParameter OUT_SIZE = new StackParameter("outSize", 5);

    @Override
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() < 6) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        var recipientAddress = new BigInteger(stack.pop());
        var valueTransferred = new BigInteger(stack.pop());
        var inOffset = new BigInteger(stack.pop());
        var inSize = new BigInteger(stack.pop());
        var outOffset = new BigInteger(stack.pop());
        var outSize = new BigInteger(stack.pop());

        program.setReturnDataOffset(outOffset);
        program.setReturnDataSize(outSize);
        var recipient = process.getAtlas().get(recipientAddress);
        if (recipient == null) {
            throw new ProgramException(program, CALL_RECIPIENT_MISSING);
        }
        var sourceAddress = program.getProgram().getAddress();
        recipient.getTransfers().push(new Pair<>(sourceAddress, valueTransferred));
        var newContext = new ProgramContext(recipient);
        var inputData = program.getMemory().read(inOffset.intValue(), inSize.intValue());
        newContext.getCallData().clear();
        newContext.getCallData().write(0, inputData);
        process.getPrograms().push(newContext);
    }

    @Override
    public List<Integer> getStackAdds() {
        return List.of();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return List.of(STACK, PROGRAM_CONTEXT, MEMORY);
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of(RECIPIENT_ADDRESS, VALUE_TRANSFERRED, IN_OFFSET, IN_SIZE, OUT_OFFSET, OUT_SIZE);
    }

    @Override
    public String toString() {
        return "CALL";
    }
}
