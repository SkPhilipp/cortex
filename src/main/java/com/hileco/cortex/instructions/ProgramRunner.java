package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.instructions.ProgramException.Reason;
import com.hileco.cortex.instructions.output.Table;

import java.math.BigInteger;
import java.util.Arrays;

import static com.hileco.cortex.instructions.output.Color.Palette.CYAN;
import static com.hileco.cortex.instructions.output.Color.Palette.GREEN;
import static com.hileco.cortex.instructions.output.Color.Palette.RED;

public class ProgramRunner {

    private static final boolean TABLE_LOGGING_ENABLED = Boolean.getBoolean(System.getProperty("TABLE_LOGGING_ENABLED", "true"));
    private final ProcessContext processContext;
    private final Table table;

    public ProgramRunner(ProcessContext processContext) {
        this.processContext = processContext;
        table = Table.builder()
                .columns(Arrays.asList(
                        Table.Column.builder().header("#").foreground(RED).width(6).build(),
                        Table.Column.builder().header("size").foreground(CYAN).width(6).build(),
                        Table.Column.builder().header("@3").foreground(CYAN).width(6).build(),
                        Table.Column.builder().header("@2").foreground(CYAN).width(6).build(),
                        Table.Column.builder().header("@1").foreground(CYAN).width(6).build(),
                        Table.Column.builder().header("@0").foreground(CYAN).width(6).build(),
                        Table.Column.builder().header("operation").foreground(GREEN).width(20).build(),
                        Table.Column.builder().header("operands").foreground(GREEN).width(15).build()
                ))
                .build();
    }

    @SuppressWarnings("unchecked")
    public void run() throws ProgramException {
        ProgramContext programContext = processContext.getPrograms().peek();
        while (programContext.getInstructionPosition() != programContext.getProgram().getInstructions().size()) {
            int currentInstructionPosition = programContext.getInstructionPosition();
            Instruction current = programContext.getProgram().getInstructions().get(currentInstructionPosition);
            log(programContext, current);
            current.getOperation().execute(processContext, programContext, current.getOperands());
            programContext = processContext.getPrograms().peek();
            if (programContext == null) {
                break;
            }
            if (programContext.getInstructionPosition() == currentInstructionPosition) {
                programContext.setInstructionPosition(currentInstructionPosition + 1);
            }
            programContext.setInstructionsExecuted(programContext.getInstructionsExecuted() + 1);
            if (programContext.getInstructionsExecuted() >= programContext.getInstructionLimit()) {
                throw new ProgramException(programContext, Reason.INSTRUCTION_LIMIT_REACHED);
            }
        }
        if (TABLE_LOGGING_ENABLED) {
            table.write(System.out);
        }
    }

    private void log(ProgramContext programContext, Instruction instruction) {
        if (TABLE_LOGGING_ENABLED && !(instruction.getOperation() instanceof Operations.NoOp)) {
            table.row(programContext.getInstructionPosition(),
                    programContext.getStack().size(),
                    programContext.getStack().size() > 3 ? new BigInteger(programContext.getStack().get(programContext.getStack().size() - 3)) : "",
                    programContext.getStack().size() > 2 ? new BigInteger(programContext.getStack().get(programContext.getStack().size() - 2)) : "",
                    programContext.getStack().size() > 1 ? new BigInteger(programContext.getStack().get(programContext.getStack().size() - 1)) : "",
                    programContext.getStack().size() > 0 ? new BigInteger(programContext.getStack().get(programContext.getStack().size())) : "",
                    instruction.getOperation().toString(),
                    instruction.getOperands().toString());
        }
    }

}
