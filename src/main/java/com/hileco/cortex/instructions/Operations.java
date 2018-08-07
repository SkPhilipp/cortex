package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.context.layer.LayeredBytes;
import com.hileco.cortex.context.layer.LayeredStack;
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
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_LIMIT_REACHED;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS;

@SuppressWarnings("WeakerAccess")
public class Operations {

    public abstract static class Operation<T> {
        public abstract void execute(ProcessContext process, ProgramContext program, T operands) throws ProgramException;

        public List<Integer> getStackTakes(T operands) {
            return Collections.emptyList();
        }

        public List<Integer> getStackAdds(T operands) {
            return Collections.emptyList();
        }

        public List<ProgramZone> getInstructionModifiers(T operands) {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return getClass()
                    .getSimpleName()
                    .replaceAll("(.)([A-Z])", "$1_$2")
                    .toUpperCase();
        }
    }

    public static class NoOperands {
        @Override
        public String toString() {
            return "";
        }
    }

    public static NoOperands NO_DATA = new NoOperands();

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              STACK OPERATIONS                                          --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    public static class Push extends Operation<Push.Operands> {
        public static class Operands {
            public byte[] bytes;

            @Override
            public String toString() {
                return new BigInteger(bytes).toString();
            }
        }

        public void execute(ProcessContext process, ProgramContext program, Operands operands) throws ProgramException {
            program.getStack().push(operands.bytes);
            if (program.getStack().size() > process.getStackLimit()) {
                throw new ProgramException(program, STACK_LIMIT_REACHED);
            }
        }

        @Override
        public List<Integer> getStackAdds(Operands operands) {
            return Collections.singletonList(-1);
        }

        public List<ProgramZone> getInstructionModifiers(Operands operands) {
            return Collections.singletonList(STACK);
        }
    }

    public static class Pop extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 1) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            stack.pop();
        }

        public List<Integer> getStackTakes(NoOperands operands) {
            return Collections.singletonList(0);
        }

        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Collections.singletonList(STACK);
        }
    }

    public static class Swap extends Operation<Swap.Operands> {
        public static class Operands {
            public int topOffsetLeft;
            public int topOffsetRight;

            @Override
            public String toString() {
                return String.format("%d, %d", topOffsetLeft, topOffsetRight);
            }
        }

        public void execute(ProcessContext process, ProgramContext program, Operands operands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() <= operands.topOffsetLeft || stack.size() <= operands.topOffsetRight) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            stack.swap(operands.topOffsetLeft, operands.topOffsetRight);
        }

        public List<Integer> getStackTakes(Swap.Operands operands) {
            return Arrays.asList(operands.topOffsetLeft, operands.topOffsetRight);
        }

        public List<Integer> getStackAdds(Swap.Operands operands) {
            return Arrays.asList(operands.topOffsetRight, operands.topOffsetLeft);
        }

        public List<ProgramZone> getInstructionModifiers(Operands operands) {
            return Collections.singletonList(STACK);
        }
    }

    public static class Duplicate extends Operation<Duplicate.Operands> {
        public static class Operands {
            public int topOffset;

            @Override
            public String toString() {
                return Integer.toString(topOffset);
            }
        }

        public void execute(ProcessContext process, ProgramContext program, Operands operands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() <= operands.topOffset) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            stack.duplicate(operands.topOffset);
            if (stack.size() > process.getStackLimit()) {
                throw new ProgramException(program, STACK_LIMIT_REACHED);
            }
        }

        public List<Integer> getStackTakes(Duplicate.Operands operands) {
            return Collections.singletonList(operands.topOffset);
        }

        public List<Integer> getStackAdds(Duplicate.Operands operands) {
            return Collections.singletonList(-1);
        }

        public List<ProgramZone> getInstructionModifiers(Operands operands) {
            return Collections.singletonList(STACK);
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              CONDITIONALS                                              --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    private static byte[] TRUE = {1};
    private static byte[] FALSE = {0};

    private static abstract class ConditionOperation extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 2) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            byte[] left = stack.pop();
            byte[] right = stack.pop();
            boolean equals = innerExecute(left, right);
            stack.push(equals ? TRUE.clone() : FALSE.clone());
        }

        public abstract boolean innerExecute(byte[] left, byte[] right);

        @Override
        public List<Integer> getStackAdds(NoOperands operands) {
            return Collections.singletonList(-1);
        }

        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Collections.singletonList(STACK);
        }
    }

    public static class Equals extends ConditionOperation {
        @Override
        public boolean innerExecute(byte[] left, byte[] right) {
            return Arrays.equals(left, right);
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Arrays.asList(0, 1);
        }
    }

    public static class GreaterThan extends ConditionOperation {
        @Override
        public boolean innerExecute(byte[] left, byte[] right) {
            BigInteger leftAsBigInteger = new BigInteger(left);
            BigInteger rightAsBigInteger = new BigInteger(right);
            return leftAsBigInteger.compareTo(rightAsBigInteger) > 0;
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Arrays.asList(0, 1);
        }
    }

    public static class LessThan extends ConditionOperation {
        @Override
        public boolean innerExecute(byte[] left, byte[] right) {
            BigInteger leftAsBigInteger = new BigInteger(left);
            BigInteger rightAsBigInteger = new BigInteger(right);
            return leftAsBigInteger.compareTo(rightAsBigInteger) < 0;
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Arrays.asList(0, 1);
        }
    }

    public static class IsZero extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) {
            LayeredStack<byte[]> stack = program.getStack();
            byte[] top = stack.pop();
            boolean isZero = true;
            for (byte item : top) {
                if (item > 0) {
                    isZero = false;
                }
            }
            stack.push(isZero ? TRUE.clone() : FALSE.clone());
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Collections.singletonList(0);
        }

        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Collections.singletonList(STACK);
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              BITWISE OPERATIONS                                        --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    private static abstract class BitwiseOperation extends Operation<NoOperands> {
        public abstract byte innerExecute(byte left, byte right);

        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 2) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            byte[] left = stack.pop();
            byte[] right = stack.pop();
            byte[] result = new byte[Math.max(left.length, right.length)];

            for (int i = 0; i < result.length; i++) {
                byte leftByte = i < left.length ? left[i] : 0;
                byte rightByte = i < right.length ? right[i] : 0;
                result[i] = innerExecute(leftByte, rightByte);
            }
            stack.push(result);
        }

        @Override
        public List<Integer> getStackAdds(NoOperands operands) {
            return Collections.singletonList(-1);
        }

        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Collections.singletonList(STACK);
        }
    }

    public static class BitwiseOr extends BitwiseOperation {
        @Override
        public byte innerExecute(byte left, byte right) {
            byte result = left;
            result |= right;
            return result;
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Arrays.asList(0, 1);
        }
    }

    public static class BitwiseXor extends BitwiseOperation {
        @Override
        public byte innerExecute(byte left, byte right) {
            byte result = left;
            result ^= right;
            return result;
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Arrays.asList(0, 1);
        }
    }

    public static class BitwiseAnd extends BitwiseOperation {
        @Override
        public byte innerExecute(byte left, byte right) {
            byte result = left;
            result &= right;
            return result;
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Arrays.asList(0, 1);
        }
    }

    public static class BitwiseNot extends Operation<NoOperands> {
        @Override
        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) {
            LayeredStack<byte[]> stack = program.getStack();
            byte[] pop = stack.pop();
            byte[] result = new byte[pop.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = 127;
                result[i] ^= pop[i];
            }
            stack.push(result);
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Collections.singletonList(0);
        }

        @Override
        public List<Integer> getStackAdds(NoOperands operands) {
            return Collections.singletonList(-1);
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              MATHEMATICAL OPERATIONS                                   --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    private static abstract class MathematicalOperation extends Operation<NoOperands> {

        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 2) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            byte[] left = stack.pop();
            byte[] right = stack.pop();
            BigInteger leftAsBigInteger = new BigInteger(left);
            BigInteger rightAsBigInteger = new BigInteger(right);
            BigInteger result = innerExecute(process, program, leftAsBigInteger, rightAsBigInteger);
            stack.push(result.toByteArray());
        }

        public abstract BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right);

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Arrays.asList(0, 1);
        }

        @Override
        public List<Integer> getStackAdds(NoOperands operands) {
            return Collections.singletonList(-1);
        }

        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Collections.singletonList(STACK);
        }
    }

    public static class Add extends MathematicalOperation {
        @Override
        public BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right) {
            return left.add(right).mod(process.getOverflowLimit().add(BigInteger.ONE));
        }
    }

    public static class Subtract extends MathematicalOperation {
        @Override
        public BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right) {
            return left.subtract(right).mod(process.getUnderflowLimit().subtract(BigInteger.ONE));
        }
    }

    public static class Multiply extends MathematicalOperation {
        @Override
        public BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right) {
            return left.multiply(right).mod(process.getOverflowLimit().add(BigInteger.ONE));
        }
    }

    public static class Divide extends MathematicalOperation {
        @Override
        public BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right) {
            return left.divide(right);
        }
    }

    public static class Modulo extends MathematicalOperation {
        @Override
        public BigInteger innerExecute(ProcessContext process, ProgramContext program, BigInteger left, BigInteger right) {
            return left.mod(right);
        }
    }

    public static class Hash extends Operation<Hash.Operands> {

        public static class Operands {
            public String hashMethod;

            @Override
            public String toString() {
                return hashMethod;
            }
        }

        public void execute(ProcessContext process, ProgramContext program, Operands operands) throws ProgramException {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(operands.hashMethod);
                LayeredStack<byte[]> stack = program.getStack();
                if (stack.size() < 1) {
                    throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
                }
                messageDigest.update(stack.pop());
                stack.push(messageDigest.digest());
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(String.format("Unknown hash method: %s", operands.hashMethod), e);
            }
        }

        @Override
        public List<Integer> getStackTakes(Hash.Operands operands) {
            return Collections.singletonList(0);
        }

        @Override
        public List<Integer> getStackAdds(Hash.Operands operands) {
            return Collections.singletonList(-1);
        }

        public List<ProgramZone> getInstructionModifiers(Operands operands) {
            return Collections.singletonList(STACK);
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              PROGRAM FLOW OPERATIONS                                   --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    private static void performJump(ProgramContext program, int nextInstructionPosition) throws ProgramException {
        if (nextInstructionPosition < 0) {
            throw new ProgramException(program, JUMP_OUT_OF_BOUNDS);
        }
        List<Instruction> instructions = program.getProgram().getInstructions();
        if (nextInstructionPosition > instructions.size()) {
            throw new ProgramException(program, JUMP_OUT_OF_BOUNDS);
        }
        Instruction instruction = instructions.get(nextInstructionPosition);
        if (!(instruction.getOperation() instanceof JumpDestination)) {
            throw new ProgramException(program, JUMP_TO_ILLEGAL_INSTRUCTION);
        }
        program.setInstructionPosition(nextInstructionPosition);
    }

    public static class Jump extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands noOperands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 1) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            int nextInstructionPosition = new BigInteger(stack.pop()).intValue();
            performJump(program, nextInstructionPosition);
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Collections.singletonList(0);
        }

        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Collections.singletonList(INSTRUCTION_POSITION);
        }
    }

    public static class JumpDestination extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) {
        }
    }

    public static class JumpIf extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands noOperands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 2) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            int nextInstructionPosition = new BigInteger(program.getStack().pop()).intValue();
            byte[] top = stack.pop();
            boolean isNonZero = false;
            for (byte item : top) {
                if (item > 0) {
                    isNonZero = true;
                }
            }
            if (isNonZero) {
                performJump(program, nextInstructionPosition);
            }
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Arrays.asList(0, 1);
        }

        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Arrays.asList(STACK, INSTRUCTION_POSITION);
        }
    }

    public static class Exit extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) {
            process.getPrograms().clear();
        }

        @Override
        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Collections.singletonList(ProgramZone.PROGRAM_CONTEXT);
        }
    }

    public static class Call extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands noOperands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 6) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            BigInteger recipientAddress = new BigInteger(stack.pop());
            BigInteger valueTransferred = new BigInteger(stack.pop());
            BigInteger inOffset = new BigInteger(stack.pop());
            BigInteger inSize = new BigInteger(stack.pop());
            BigInteger outOffset = new BigInteger(stack.pop());
            BigInteger outSize = new BigInteger(stack.pop());

            program.setReturnDataOffset(outOffset);
            program.setReturnDataSize(outSize);
            Program recipient = process.getAtlas().get(recipientAddress);
            if (recipient == null) {
                throw new ProgramException(program, CALL_RECIPIENT_MISSING);
            }
            BigInteger sourceAddress = program.getProgram().getAddress();
            recipient.getTransfers().push(new Pair<>(sourceAddress, valueTransferred));
            ProgramContext newContext = new ProgramContext(recipient);
            byte[] inputData = program.getMemory().read(inOffset.intValue(), inSize.intValue());
            newContext.getCallData().clear();
            newContext.getCallData().write(0, inputData);
            process.getPrograms().push(newContext);
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Arrays.asList(0, 1, 2, 3, 4, 5);
        }

        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Arrays.asList(STACK, PROGRAM_CONTEXT, MEMORY);
        }
    }

    public static class CallReturn extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands noOperands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 2) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            BigInteger offset = new BigInteger(stack.pop());
            BigInteger size = new BigInteger(stack.pop());
            process.getPrograms().pop();
            ProgramContext nextContext = process.getPrograms().peek();
            byte[] data = program.getMemory().read(offset.intValue(), size.intValue());
            BigInteger wSize = nextContext.getReturnDataSize();
            if (data.length > wSize.intValue()) {
                throw new ProgramException(program, RETURN_DATA_TOO_LARGE);
            }
            byte[] dataExpanded = Arrays.copyOf(data, wSize.intValue());
            BigInteger wOffset = nextContext.getReturnDataOffset();
            nextContext.getMemory().write(wOffset.intValue(), dataExpanded, wSize.intValue());
        }

        @Override
        public List<Integer> getStackTakes(NoOperands operands) {
            return Arrays.asList(0, 1);
        }

        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Arrays.asList(STACK, PROGRAM_CONTEXT, MEMORY);
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              PERSISTENCE & PARAMETERS                                  --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

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

    public static class Load extends Operation<Load.Operands> {
        public static class Operands {
            public ProgramStoreZone programStoreZone;

            @Override
            public String toString() {
                return String.format("%s", programStoreZone);
            }
        }

        public void execute(ProcessContext process, ProgramContext program, Operands operands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 1) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            byte[] addressBytes = stack.pop();
            BigInteger address = new BigInteger(addressBytes);
            LayeredBytes layeredBytes;
            switch (operands.programStoreZone) {
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
                    throw new IllegalArgumentException(String.format("Unsupported ProgramStoreZone: %s", operands.programStoreZone));
            }
            byte[] bytes = layeredBytes.read(address.intValue(), 32);
            if (bytes == null) {
                throw new IllegalStateException(String.format("Loading empty data at %s:%s", operands.programStoreZone, address.toString()));
            }
            stack.push(bytes);
        }

        public List<Integer> getStackTakes(Load.Operands operands) {
            return Arrays.asList(0, 1);
        }

        public List<ProgramZone> getInstructionModifiers(Operands operands) {
            return Operations.programZoneFor(operands.programStoreZone);
        }

    }

    public static class Save extends Operation<Save.Operands> {
        public static class Operands {
            public ProgramStoreZone programStoreZone;

            @Override
            public String toString() {
                return String.format("%s", programStoreZone);
            }
        }

        public void execute(ProcessContext process, ProgramContext program, Operands operands) throws ProgramException {
            LayeredStack<byte[]> stack = program.getStack();
            if (stack.size() < 2) {
                throw new ProgramException(program, STACK_TOO_FEW_ELEMENTS);
            }
            byte[] addressBytes = stack.pop();
            BigInteger address = new BigInteger(addressBytes);
            byte[] bytes = stack.pop();
            LayeredBytes layeredBytes;
            switch (operands.programStoreZone) {
                case MEMORY:
                    layeredBytes = program.getMemory();
                    break;
                case DISK:
                    layeredBytes = program.getProgram().getStorage();
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unsupported ProgramStoreZone: %s", operands.programStoreZone));
            }
            layeredBytes.write(address.intValue(), bytes);
        }


        public List<Integer> getStackTakes(Save.Operands operands) {
            return Collections.singletonList(0);
        }

        public List<Integer> getStackAdds(Save.Operands operands) {
            return Collections.singletonList(-1);
        }

        public List<ProgramZone> getInstructionModifiers(Save.Operands operands) {
            return Operations.programZoneFor(operands.programStoreZone);
        }
    }

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
