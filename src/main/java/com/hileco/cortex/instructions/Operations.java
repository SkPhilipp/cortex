package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.context.data.ProgramData;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.context.layer.LayeredMap;
import com.hileco.cortex.context.layer.LayeredStack;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hileco.cortex.context.ProgramZone.INSTRUCTION_POSITION;
import static com.hileco.cortex.context.ProgramZone.MEMORY;
import static com.hileco.cortex.context.ProgramZone.STACK;

@SuppressWarnings("WeakerAccess")
public class Operations {

    public abstract static class Operation<T> {
        public abstract void execute(ProcessContext process, ProgramContext program, T operands);

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
            return this.getClass()
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

        public void execute(ProcessContext process, ProgramContext program, Operands operands) {
            program.getStack().push(operands.bytes);
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
        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) {
            program.getStack().pop();
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

        public void execute(ProcessContext process, ProgramContext program, Operands operands) {
            program.getStack().swap(operands.topOffsetLeft, operands.topOffsetRight);
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

        public void execute(ProcessContext process, ProgramContext program, Operands operands) {
            program.getStack().duplicate(operands.topOffset);
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
        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) {
            LayeredStack<byte[]> stack = program.getStack();
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

        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) {
            LayeredStack<byte[]> stack = program.getStack();
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

        public void execute(ProcessContext process, ProgramContext program, NoOperands operands) {
            LayeredStack<byte[]> stack = program.getStack();
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

        public void execute(ProcessContext process, ProgramContext program, Operands operands) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(operands.hashMethod);
                LayeredStack<byte[]> stack = program.getStack();
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

    public static class Jump extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands noOperands) {
            byte[] destination = program.getStack().pop();
            program.setState(ProgramContext.ProgramState.IN_JUMP);
            program.setInstructionPosition(new BigInteger(destination).intValue());
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
            program.setState(ProgramContext.ProgramState.DEFAULT);
        }
    }

    public static class JumpIf extends Operation<NoOperands> {
        public void execute(ProcessContext process, ProgramContext program, NoOperands noOperands) {
            LayeredStack<byte[]> stack = program.getStack();
            byte[] destination = program.getStack().pop();
            byte[] top = stack.pop();
            boolean isZero = true;
            for (byte item : top) {
                if (item > 0) {
                    isZero = false;
                }
            }
            if (!isZero) {
                program.setState(ProgramContext.ProgramState.IN_JUMP);
                program.setInstructionPosition(new BigInteger(destination).intValue());
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
            program.setState(ProgramContext.ProgramState.IN_EXIT);
        }

        @Override
        public List<ProgramZone> getInstructionModifiers(NoOperands operands) {
            return Collections.singletonList(ProgramZone.INSTRUCTION_POSITION);
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              PERSISTENCE & PARAMETERS                                  --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    private static LayeredMap<BigInteger, ProgramData> storageFor(ProgramContext program, ProgramStoreZone programStoreZone) {
        LayeredMap<BigInteger, ProgramData> storage;
        switch (programStoreZone) {
            case MEMORY:
                storage = program.getMemoryStorage();
                break;
            case STORAGE:
                storage = program.getDiskStorage();
                break;
            case CALL_DATA:
                storage = program.getCallDataStorage();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported ProgramStoreZone: %s", programStoreZone));
        }
        return storage;
    }

    public static class Load extends Operation<Load.Operands> {
        public static class Operands {
            public ProgramStoreZone programStoreZone;

            @Override
            public String toString() {
                return String.format("%s", programStoreZone);
            }
        }

        public void execute(ProcessContext process, ProgramContext program, Operands operands) {
            byte[] addressBytes = program.getStack().pop();
            BigInteger address = new BigInteger(addressBytes);
            LayeredMap<BigInteger, ProgramData> storage = storageFor(program, operands.programStoreZone);
            ProgramData programData = storage.get(address);
            if (programData == null) {
                throw new IllegalStateException(String.format("Loading empty data at %s:%s", operands.programStoreZone, address.toString()));
            }
            program.getStack().push(programData.getContent());
        }

        public List<Integer> getStackTakes(Load.Operands operands) {
            return Arrays.asList(0, 1);
        }

        public List<ProgramZone> getInstructionModifiers(Operands operands) {
            return Arrays.asList(STACK, MEMORY);
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

        public void execute(ProcessContext process, ProgramContext program, Operands operands) {
            LayeredStack<byte[]> stack = program.getStack();
            byte[] addressBytes = stack.pop();
            BigInteger address = new BigInteger(addressBytes);
            byte[] bytes = stack.pop();
            LayeredMap<BigInteger, ProgramData> storage = storageFor(program, operands.programStoreZone);
            storage.put(address, new ProgramData(bytes));
        }


        public List<Integer> getStackTakes(Save.Operands operands) {
            return Collections.singletonList(0);
        }

        public List<Integer> getStackAdds(Save.Operands operands) {
            return Collections.singletonList(-1);
        }

        public List<ProgramZone> getInstructionModifiers(Operands operands) {
            return Arrays.asList(STACK, MEMORY);
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
