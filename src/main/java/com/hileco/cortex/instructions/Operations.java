package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.context.layer.LayeredBytes;
import com.hileco.cortex.context.layer.LayeredStack;
import com.hileco.cortex.instructions.operations.Operation;
import javafx.util.Pair;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.CALL_DATA;
import static com.hileco.cortex.context.ProgramZone.DISK;
import static com.hileco.cortex.context.ProgramZone.INSTRUCTION_POSITION;
import static com.hileco.cortex.context.ProgramZone.MEMORY;
import static com.hileco.cortex.context.ProgramZone.PROGRAM_CONTEXT;
import static com.hileco.cortex.context.ProgramZone.STACK;
import static com.hileco.cortex.instructions.ProgramException.Reason.CALL_RECIPIENT_MISSING;
import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_OUT_OF_BOUNDS;
import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION;
import static com.hileco.cortex.instructions.ProgramException.Reason.RETURN_DATA_TOO_LARGE;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@SuppressWarnings("WeakerAccess")
public class Operations {


    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              TESTING                                                   --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    public static class NoOp extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) {
        }
    }

}
