package com.hileco.cortex.instructions.calls;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.Pair;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.MEMORY;
import static com.hileco.cortex.context.ProgramZone.PROGRAM_CONTEXT;
import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.CALL_RECIPIENT_MISSING;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode
public class CALL implements Instruction {
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
    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1, 2, 3, 4, 5);
    }

    @Override
    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Arrays.asList(STACK, PROGRAM_CONTEXT, MEMORY);
    }

    @Override
    public String toString() {
        return "CALL";
    }
}
