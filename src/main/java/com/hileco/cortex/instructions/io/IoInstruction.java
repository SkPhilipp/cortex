package com.hileco.cortex.instructions.io;


import com.hileco.cortex.vm.ProgramZone;
import com.hileco.cortex.vm.ProgramStoreZone;
import com.hileco.cortex.instructions.Instruction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

import static com.hileco.cortex.vm.ProgramZone.CALL_DATA;
import static com.hileco.cortex.vm.ProgramZone.DISK;
import static com.hileco.cortex.vm.ProgramZone.MEMORY;
import static com.hileco.cortex.vm.ProgramZone.STACK;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public abstract class IoInstruction implements Instruction {
    private final ProgramStoreZone programStoreZone;

    private static List<ProgramZone> programZoneFor(ProgramStoreZone programStoreZone) {
        switch (programStoreZone) {
            case MEMORY:
                return List.of(STACK, MEMORY);
            case DISK:
                return List.of(STACK, DISK);
            case CALL_DATA:
                return List.of(STACK, CALL_DATA);
            default:
                throw new IllegalArgumentException(String.format("Unsupported ProgramStoreZone: %s", programStoreZone));
        }
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return programZoneFor(this.programStoreZone);
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.getClass().getSimpleName(), this.programStoreZone);
    }
}
