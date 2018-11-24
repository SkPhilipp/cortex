package com.hileco.cortex.instructions.io;

import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.ProgramStoreZone;
import com.hileco.cortex.vm.layer.LayeredBytes;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode(callSuper = true)
public class LOAD extends IoInstruction {

    public static final StackParameter ADDRESS = new StackParameter("address", 0);

    private static final int SIZE = 32;

    public LOAD(ProgramStoreZone programStoreZone) {
        super(programStoreZone);
    }

    @Override
    public void execute(VirtualMachine process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() < 1) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        var addressBytes = stack.pop();
        var address = new BigInteger(addressBytes);
        LayeredBytes layeredBytes;
        var programStoreZone = this.getProgramStoreZone();
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
        var bytes = layeredBytes.read(address.intValue(), SIZE);
        if (bytes == null) {
            throw new IllegalStateException(String.format("Loading empty data at %s:%s", programStoreZone, address.toString()));
        }
        stack.push(bytes);
    }

    @Override
    public List<Integer> getStackAdds() {
        return Collections.singletonList(-1);
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of(ADDRESS);
    }
}

