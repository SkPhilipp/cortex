package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.instructions.ProgramException.Reason;
import com.hileco.cortex.instructions.output.Table;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_OUT_OF_BOUNDS;
import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_LIMIT_REACHED;
import static com.hileco.cortex.instructions.output.Color.Palette.CYAN;
import static com.hileco.cortex.instructions.output.Color.Palette.GREEN;
import static com.hileco.cortex.instructions.output.Color.Palette.RED;

public class ProgramRunner {
    private ProgramContext programContext;

    public ProgramRunner(ProgramContext programContext) {
        this.programContext = programContext;
    }

    @SuppressWarnings("unchecked")
    public void run(List<Instruction> instructions) throws ProgramException {
        int size = instructions.size();
        Table table = Table.builder()
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
        while (!programContext.isExiting() && programContext.getInstructionPosition() != size) {
            checkInstructionPosition(size);
            Instruction current = instructions.get(programContext.getInstructionPosition());
            checkJumping(current);
            if (!(current.getOperation() instanceof Operations.NoOp)) {
                table.row(
                        programContext.getInstructionPosition(),
                        programContext.getStack().size(),
                        programContext.getStack().size() > 3 ? new BigInteger(programContext.getStack().get(programContext.getStack().size() - 3)) : "",
                        programContext.getStack().size() > 2 ? new BigInteger(programContext.getStack().get(programContext.getStack().size() - 2)) : "",
                        programContext.getStack().size() > 1 ? new BigInteger(programContext.getStack().get(programContext.getStack().size() - 1)) : "",
                        programContext.getStack().size() > 0 ? new BigInteger(programContext.getStack().get(programContext.getStack().size())) : "",
                        current.getOperation().toString(),
                        current.getOperands().toString());
            }
            incrementInstructionPosition(current);
            current.getOperation().execute(programContext, current.getOperands());
            incrementInstructionsExecuted();
            checkStack();
            manageInstructionExecution();
        }
        table.write(System.out);
    }

    private void incrementInstructionsExecuted() {
        programContext.setInstructionsExecuted(programContext.getInstructionsExecuted() + 1);
    }

    private void incrementInstructionPosition(Instruction current) {
        if (!(current.getOperation() instanceof Operations.Jump)) {
            programContext.setInstructionPosition(programContext.getInstructionPosition() + 1);
        }
    }

    private void checkInstructionPosition(int size) throws ProgramException {
        if (programContext.getInstructionPosition() < 0) {
            throw new ProgramException(programContext, JUMP_OUT_OF_BOUNDS);
        }
        if (programContext.getInstructionPosition() > size) {
            throw new ProgramException(programContext, JUMP_OUT_OF_BOUNDS);
        }
    }

    private void checkJumping(Instruction current) throws ProgramException {
        if (programContext.isJumping()
                && !(current.getOperation() instanceof Operations.JumpDestination)) {
            throw new ProgramException(programContext, JUMP_TO_ILLEGAL_INSTRUCTION);
        }
    }

    private void checkStack() throws ProgramException {
        if (programContext.getStack().size() >= programContext.getStackLimit()) {
            throw new ProgramException(programContext, STACK_LIMIT_REACHED);
        }
    }

    private void manageInstructionExecution() throws ProgramException {
        if (programContext.getInstructionsExecuted() >= programContext.getInstructionLimit()) {
            throw new ProgramException(programContext, Reason.INSTRUCTION_LIMIT_REACHED);
        }
    }
}
