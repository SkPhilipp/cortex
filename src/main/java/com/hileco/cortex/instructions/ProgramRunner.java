package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.instructions.ProgramException.Reason;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.output.Table;

import java.math.BigInteger;
import java.util.Arrays;

import static com.hileco.cortex.output.Color.Palette.CYAN;
import static com.hileco.cortex.output.Color.Palette.GREEN;
import static com.hileco.cortex.output.Color.Palette.RED;

public class ProgramRunner {

    private static final boolean TABLE_LOGGING_ENABLED = Boolean.getBoolean(System.getProperty("TABLE_LOGGING_ENABLED", "true"));
    private final ProcessContext processContext;
    private final Table table;

    public ProgramRunner(ProcessContext processContext) {
        this.processContext = processContext;
        this.table = Table.builder()
                .columns(Arrays.asList(
                        Table.Column.builder().header("#").foreground(RED).width(6).build(),
                        Table.Column.builder().header("size").foreground(CYAN).width(6).build(),
                        Table.Column.builder().header("@3").foreground(CYAN).width(6).build(),
                        Table.Column.builder().header("@2").foreground(CYAN).width(6).build(),
                        Table.Column.builder().header("@1").foreground(CYAN).width(6).build(),
                        Table.Column.builder().header("@0").foreground(CYAN).width(6).build(),
                        Table.Column.builder().header("instruction").foreground(GREEN).width(20).build()
                ))
                .build();
    }

    @SuppressWarnings("unchecked")
    public void run() throws ProgramException {
        ProgramContext programContext = this.processContext.getPrograms().peek();
        while (programContext.getInstructionPosition() != programContext.getProgram().getInstructions().size()) {
            int currentInstructionPosition = programContext.getInstructionPosition();
            Instruction current = programContext.getProgram().getInstructions().get(currentInstructionPosition);
            this.log(programContext, current);
            current.execute(this.processContext, programContext);
            programContext = this.processContext.getPrograms().peek();
            if (programContext == null) {
                break;
            }
            if (programContext.getInstructionPosition() == currentInstructionPosition) {
                programContext.setInstructionPosition(currentInstructionPosition + 1);
            }
            programContext.setInstructionsExecuted(programContext.getInstructionsExecuted() + 1);
            if (programContext.getInstructionsExecuted() >= programContext.getInstructionLimit()) {
                throw new ProgramException(programContext, Reason.INSTRUCTION_LIMIT_REACHED_ON_PROGRAM_LEVEL);
            }
            this.processContext.setInstructionsExecuted(this.processContext.getInstructionsExecuted() + 1);
            if (this.processContext.getInstructionsExecuted() >= this.processContext.getInstructionLimit()) {
                throw new ProgramException(programContext, Reason.INSTRUCTION_LIMIT_REACHED_ON_PROCESS_LEVEL);
            }
        }
        if (TABLE_LOGGING_ENABLED) {
            this.table.write(System.out);
        }
    }

    private void log(ProgramContext programContext, Instruction instruction) {
        if (TABLE_LOGGING_ENABLED && !(instruction instanceof NOOP)) {
            this.table.row(programContext.getInstructionPosition(),
                    programContext.getStack().size(),
                    programContext.getStack().size() > 3 ? new BigInteger(programContext.getStack().get(programContext.getStack().size() - 3)) : "",
                    programContext.getStack().size() > 2 ? new BigInteger(programContext.getStack().get(programContext.getStack().size() - 2)) : "",
                    programContext.getStack().size() > 1 ? new BigInteger(programContext.getStack().get(programContext.getStack().size() - 1)) : "",
                    programContext.getStack().size() > 0 ? new BigInteger(programContext.getStack().get(programContext.getStack().size())) : "",
                    instruction.toString());
        }
    }

}
