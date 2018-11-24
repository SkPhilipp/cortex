package com.hileco.cortex.instructions.io;

import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.ProgramStoreZone;
import com.hileco.cortex.vm.layer.LayeredBytes;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.List;

import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@EqualsAndHashCode(callSuper = true)
public class SAVE extends IoInstruction {

    public static final StackParameter ADDRESS = new StackParameter("address", 0);
    public static final StackParameter BYTES = new StackParameter("bytes", 1);

    public SAVE(ProgramStoreZone programStoreZone) {
        super(programStoreZone);
    }

    @Override
    public void execute(VirtualMachine process, ProgramContext program) throws ProgramException {
        var stack = program.getStack();
        if (stack.size() < 2) {
            throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
        }
        var addressBytes = stack.pop();
        var address = new BigInteger(addressBytes);
        var bytes = stack.pop();
        LayeredBytes layeredBytes;

        var programStoreZone = this.getProgramStoreZone();
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

    @Override
    public List<Integer> getStackAdds() {
        return List.of();
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of(ADDRESS, BYTES);
    }
}
