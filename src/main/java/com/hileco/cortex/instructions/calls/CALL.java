package com.hileco.cortex.instructions.calls;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import javafx.util.Pair;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.MEMORY;
import static com.hileco.cortex.context.ProgramZone.PROGRAM_CONTEXT;
import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.CALL_RECIPIENT_MISSING;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

public class CALL implements Instruction {
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() < 6) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        BigInteger recipientAddress = new BigInteger(stack.pop());
        BigInteger valueTransferred = new BigInteger(stack.pop());
        BigInteger inOffset = new BigInteger(stack.pop());
        BigInteger inSize = new BigInteger(stack.pop());
        BigInteger outOffset = new BigInteger(stack.pop());
        BigInteger outSize = new BigInteger(stack.pop());

        program.setReturnDataOffset(outOffset);
        program.setReturnDataSize(outSize);
        Program recipient = process.getAtlas().get(recipientAddress);
        if (recipient == null) {
            throw new ProgramException(program, CALL_RECIPIENT_MISSING);
        }
        BigInteger sourceAddress = program.getProgram().getAddress();
        recipient.getTransfers().push(new Pair<>(sourceAddress, valueTransferred));
        ProgramContext newContext = new ProgramContext(recipient);
        byte[] inputData = program.getMemory().read(inOffset.intValue(), inSize.intValue());
        newContext.getCallData().clear();
        newContext.getCallData().write(0, inputData);
        process.getPrograms().push(newContext);
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1, 2, 3, 4, 5);
    }

    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    public List<ProgramZone> getInstructionModifiers() {
        return Arrays.asList(STACK, PROGRAM_CONTEXT, MEMORY);
    }

    public String toString() {
        return "CALL";
    }
}
