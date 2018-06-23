package com.hileco.cortex.instructions;

import com.hileco.cortex.primitives.LayeredMap;
import com.hileco.cortex.primitives.LayeredStack;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class Instructions {

    public interface InstructionExecutor<T extends InstructionData> {
        void execute(ProgramContext context, T Data);
    }

    public interface InstructionData {
    }

    public static class NoData implements InstructionData {
    }

    public static NoData NO_DATA = new NoData();

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              STACK OPERATIONS                                          --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    public static class Push implements InstructionExecutor<Push.Data> {
        public static class Data implements InstructionData {
            public byte[] bytes;
        }

        public void execute(ProgramContext context, Data data) {
            context.getStack().push(data.bytes);
        }

        @Override
        public String toString() {
            return "PUSH";
        }
    }

    public static class Pop implements InstructionExecutor<NoData> {
        public void execute(ProgramContext context, NoData data) {
            context.getStack().pop();
        }

        @Override
        public String toString() {
            return "POP";
        }
    }

    public static class Swap implements InstructionExecutor<Swap.Data> {
        public static class Data implements InstructionData {
            public int topOffsetLeft;
            public int topOffsetRight;
        }

        public void execute(ProgramContext context, Data data) {
            context.getStack().swap(data.topOffsetLeft, data.topOffsetRight);
        }

        @Override
        public String toString() {
            return "POP";
        }
    }

    public static class Duplicate implements InstructionExecutor<Duplicate.Data> {
        public static class Data implements InstructionData {
            public int topOffset;
        }

        public void execute(ProgramContext context, Data data) {
            context.getStack().duplicate(data.topOffset);
        }

        @Override
        public String toString() {
            return "DUPLICATE";
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              CONDITIONALS                                              --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    private static byte[] TRUE = {1};
    private static byte[] FALSE = {0};

    private static abstract class ConditionInstructionExecutor implements InstructionExecutor<NoData> {
        public void execute(ProgramContext context, NoData data) {
            LayeredStack<byte[]> stack = context.getStack();
            byte[] left = stack.pop();
            byte[] right = stack.pop();
            boolean equals = innerExecute(left, right);
            stack.push(equals ? TRUE.clone() : FALSE.clone());
        }

        public abstract boolean innerExecute(byte[] left, byte[] right);
    }

    public static class Equals extends ConditionInstructionExecutor {
        @Override
        public boolean innerExecute(byte[] left, byte[] right) {
            return Arrays.equals(left, right);
        }


        @Override
        public String toString() {
            return "EQUALS";
        }
    }

    public static class GreaterThan extends ConditionInstructionExecutor {
        @Override
        public boolean innerExecute(byte[] left, byte[] right) {
            BigInteger leftAsBigInteger = new BigInteger(left);
            BigInteger rightAsBigInteger = new BigInteger(right);
            return leftAsBigInteger.compareTo(rightAsBigInteger) > 0;
        }

        @Override
        public String toString() {
            return "GREATER_THAN";
        }
    }

    public static class LessThan extends ConditionInstructionExecutor {
        @Override
        public boolean innerExecute(byte[] left, byte[] right) {
            BigInteger leftAsBigInteger = new BigInteger(left);
            BigInteger rightAsBigInteger = new BigInteger(right);
            return leftAsBigInteger.compareTo(rightAsBigInteger) < 0;
        }

        @Override
        public String toString() {
            return "LESS_THAN";
        }
    }

    public static class IsZero implements InstructionExecutor<NoData> {
        public void execute(ProgramContext context, NoData data) {
            LayeredStack<byte[]> stack = context.getStack();
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
        public String toString() {
            return "IS_ZERO";
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              BITWISE OPERATIONS                                        --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    private static abstract class BitwiseInstructionExecutor implements InstructionExecutor<NoData> {

        public void execute(ProgramContext context, NoData data) {
            LayeredStack<byte[]> stack = context.getStack();
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

        public abstract byte innerExecute(byte left, byte right);
    }

    public static class BitwiseOr extends BitwiseInstructionExecutor {
        @Override
        public byte innerExecute(byte left, byte right) {
            byte result = left;
            result |= right;
            return result;
        }

        @Override
        public String toString() {
            return "OR";
        }
    }

    public static class BitwiseXor extends BitwiseInstructionExecutor {
        @Override
        public byte innerExecute(byte left, byte right) {
            byte result = left;
            result ^= right;
            return result;
        }

        @Override
        public String toString() {
            return "XOR";
        }
    }

    public static class BitwiseAnd extends BitwiseInstructionExecutor {
        @Override
        public byte innerExecute(byte left, byte right) {
            byte result = left;
            result &= right;
            return result;
        }

        @Override
        public String toString() {
            return "AND";
        }
    }

    public static class BitwiseNot implements InstructionExecutor<NoData> {
        @Override
        public void execute(ProgramContext context, NoData Data) {
            LayeredStack<byte[]> stack = context.getStack();
            byte[] pop = stack.pop();
            byte[] result = new byte[pop.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = 127;
                result[i] ^= pop[i];
            }
            stack.push(result);
        }

        @Override
        public String toString() {
            return "NOT";
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              MATHEMATICAL OPERATIONS                                   --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    private static abstract class MathematicalInstructionExecutor implements InstructionExecutor<NoData> {

        public void execute(ProgramContext context, NoData data) {
            LayeredStack<byte[]> stack = context.getStack();
            byte[] left = stack.pop();
            byte[] right = stack.pop();
            BigInteger leftAsBigInteger = new BigInteger(left);
            BigInteger rightAsBigInteger = new BigInteger(right);
            BigInteger result = innerExecute(leftAsBigInteger, rightAsBigInteger);
            stack.push(result.toByteArray());
        }

        public abstract BigInteger innerExecute(BigInteger left, BigInteger right);
    }

    public static class Add extends MathematicalInstructionExecutor {
        @Override
        public BigInteger innerExecute(BigInteger left, BigInteger right) {
            return left.add(right);
        }

        @Override
        public String toString() {
            return "ADD";
        }
    }

    public static class Subtract extends MathematicalInstructionExecutor {
        @Override
        public BigInteger innerExecute(BigInteger left, BigInteger right) {
            return left.subtract(right);
        }

        @Override
        public String toString() {
            return "SUBTRACT";
        }
    }

    public static class Multiply extends MathematicalInstructionExecutor {
        @Override
        public BigInteger innerExecute(BigInteger left, BigInteger right) {
            return left.multiply(right);
        }

        @Override
        public String toString() {
            return "MULTIPLY";
        }
    }

    public static class Divide extends MathematicalInstructionExecutor {
        @Override
        public BigInteger innerExecute(BigInteger left, BigInteger right) {
            return left.divide(right);
        }

        @Override
        public String toString() {
            return "DIVIDE";
        }
    }

    public static class Modulo extends MathematicalInstructionExecutor {
        @Override
        public BigInteger innerExecute(BigInteger left, BigInteger right) {
            return left.mod(right);
        }

        @Override
        public String toString() {
            return "MODULO";
        }
    }

    public static class Hash implements InstructionExecutor<Hash.Data> {

        public static final String HASH_METHOD_SHA_3 = "SHA3";
        public static final String HASH_METHOD_NONE = "NONE";

        public static class Data implements InstructionData {
            public String hashMethod;
        }

        public void execute(ProgramContext context, Data data) {
            if (HASH_METHOD_SHA_3.equals(data.hashMethod)) {
                throw new UnsupportedOperationException(String.format("Unimplemented hash method: %s", data.hashMethod));
            } else if (!HASH_METHOD_NONE.equals(data.hashMethod)) {
                throw new IllegalArgumentException(String.format("Unknown hash method: %s", data.hashMethod));
            }
        }

        @Override
        public String toString() {
            return "HASH";
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              PROGRAM FLOW OPERATIONS                                   --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    public static class Jump implements InstructionExecutor<Jump.Data> {
        public static class Data implements InstructionData {
            public int destination;
        }

        public void execute(ProgramContext context, Data data) {
            context.setJumping(true);
            context.setInstructionPosition(data.destination);
        }

        @Override
        public String toString() {
            return "JUMP";
        }
    }

    public static class JumpDestination implements InstructionExecutor<NoData> {
        public void execute(ProgramContext context, NoData data) {
            context.setJumping(false);
        }

        @Override
        public String toString() {
            return "JUMP_DESTINATION";
        }
    }

    public static class JumpIf implements InstructionExecutor<JumpIf.Data> {
        public static class Data implements InstructionData {
            public int destination;
        }

        public void execute(ProgramContext context, Data data) {
            LayeredStack<byte[]> stack = context.getStack();
            byte[] top = stack.pop();
            boolean isZero = true;
            for (byte item : top) {
                if (item > 0) {
                    isZero = false;
                }
            }
            if (!isZero) {
                context.setJumping(true);
                context.setInstructionPosition(data.destination);
            }
        }

        @Override
        public String toString() {
            return "JUMP_IF";
        }
    }

    public static class Exit implements InstructionExecutor<NoData> {
        public void execute(ProgramContext context, NoData data) {
            context.setExiting(true);
        }

        @Override
        public String toString() {
            return "EXIT";
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              PERSISTENCE & PARAMETERS                                  --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    public static class Load implements InstructionExecutor<Load.Data> {
        public static class Data implements InstructionData {
            public String group;
            public String address;
        }

        public void execute(ProgramContext context, Data data) {
            Map<String, LayeredMap<String, byte[]>> storage = context.getStorage();
            LayeredStack<byte[]> stack = context.getStack();
            if (!storage.containsKey(data.group)) {
                throw new IllegalArgumentException();
            }
            LayeredMap<String, byte[]> volume = storage.get(data.group);
            if (!volume.containsKey(data.address)) {
                throw new IllegalArgumentException();
            }
            byte[] bytes = volume.get(data.address);
            stack.push(bytes);
        }

        @Override
        public String toString() {
            return "LOAD";
        }
    }

    public static class Save implements InstructionExecutor<Save.Data> {
        public static class Data implements InstructionData {
            public String group;
            public String address;
        }

        public void execute(ProgramContext context, Data data) {
            Map<String, LayeredMap<String, byte[]>> storage = context.getStorage();
            LayeredStack<byte[]> stack = context.getStack();
            byte[] bytes = stack.pop();
            if (!storage.containsKey(data.group)) {
                throw new IllegalArgumentException();
            }
            LayeredMap<String, byte[]> volume = storage.get(data.group);
            if (!volume.containsKey(data.address)) {
                throw new IllegalArgumentException();
            }
            volume.put(data.address, bytes);
        }

        @Override
        public String toString() {
            return "SAVE";
        }
    }

    // --------------------------------------------------------------------------------------------
    // --                                                                                        --
    // --                              TESTING                                                   --
    // --                                                                                        --
    // --------------------------------------------------------------------------------------------

    public static class NoOp implements InstructionExecutor<NoData> {
        public void execute(ProgramContext context, NoData data) {
        }

        @Override
        public String toString() {
            return "NOOP";
        }
    }

}
