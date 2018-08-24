package com.hileco.cortex.instructions.io;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.context.layer.LayeredBytes;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.ProgramException;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode(callSuper = true)
public class LOAD extends IoInstruction {
    public LOAD(ProgramStoreZone programStoreZone) {
        super(programStoreZone);
    }

    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() < 1) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        byte[] addressBytes = stack.pop();
        BigInteger address = new BigInteger(addressBytes);
        LayeredBytes layeredBytes;
        ProgramStoreZone programStoreZone = getProgramStoreZone();
        switch (programStoreZone) {
            case MEMORY:
                layeredBytes = program.getMemory();
                break;
            case DISK:
                layeredBytes = program.getProgram().getStorage();
                break;
            case CALL_DATA:
                layeredBytes = program.getCallData();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported ProgramStoreZone: %s", programStoreZone));
        }
        byte[] bytes = layeredBytes.read(address.intValue(), 32);
        if (bytes == null) {
            throw new IllegalStateException(String.format("Loading empty data at %s:%s", programStoreZone, address.toString()));
        }
        stack.push(bytes);
    }

    public List<Integer> getStackTakes() {
        return Collections.singletonList(0);
    }

    public List<Integer> getStackAdds() {
        return Collections.singletonList(-1);
    }
}

