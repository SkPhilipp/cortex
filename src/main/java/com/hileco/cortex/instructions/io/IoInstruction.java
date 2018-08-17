package com.hileco.cortex.instructions.io;


import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.Instruction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.CALL_DATA;
import static com.hileco.cortex.context.ProgramZone.DISK;
import static com.hileco.cortex.context.ProgramZone.MEMORY;
import static com.hileco.cortex.context.ProgramZone.STACK;

@Getter
@AllArgsConstructor
public abstract class IoInstruction implements Instruction {
    private ProgramStoreZone programStoreZone;

    private static List<ProgramZone> programZoneFor(ProgramStoreZone programStoreZone) {
        switch (programStoreZone) {
            case MEMORY:
                return Arrays.asList(STACK, MEMORY);
            case DISK:
                return Arrays.asList(STACK, DISK);
            case CALL_DATA:
                return Arrays.asList(STACK, CALL_DATA);
            default:
                throw new IllegalArgumentException(String.format("Unsupported ProgramStoreZone: %s", programStoreZone));
        }
    }

    public List<ProgramZone> getInstructionModifiers() {
        return programZoneFor(programStoreZone);
    }

    public String toString() {
        return String.format("%s %s", getClass().getSimpleName(), programStoreZone);
    }
}
