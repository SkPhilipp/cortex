package com.hileco.cortex.instructions.operations.io;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.context.layer.LayeredBytes;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.ProgramException;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@Value
@EqualsAndHashCode(callSuper = true)
public class SAVE extends IoOperation {
    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        LayeredStack<byte[]> stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        byte[] addressBytes = stack.pop();
        BigInteger address = new BigInteger(addressBytes);
        byte[] bytes = stack.pop();
        LayeredBytes layeredBytes;

        ProgramStoreZone programStoreZone = getProgramStoreZone();
        switch (programStoreZone) {
            case MEMORY:
                layeredBytes = program.getMemory();
                break;
            case DISK:
                layeredBytes = program.getProgram().getStorage();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported ProgramStoreZone: %s", programStoreZone));
        }
        layeredBytes.write(address.intValue(), bytes);
    }

    public List<Integer> getStackTakes() {
        return Arrays.asList(0, 1);
    }

    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }
}
