package com.hileco.cortex.instructions;

import com.hileco.cortex.data.ProgramData;
import com.hileco.cortex.primitives.LayeredStack;

import java.math.BigInteger;
import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class Operations {

    public interface Operation<T extends Operands> {
        void execute(ProgramContext context, T operands);
    }

    public interface Operands {
    }

    public static class NoOperands implements Operands {

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

    public static class Push implements Operation<Push.Operands> {
        public static class Operands implements Operations.Operands {
            public byte[] bytes;

            @Override
            public String toString() {
                return new BigInteger(bytes).toString();
            }
        }

        public void execute(ProgramContext context, Operands operands) {
            context.getStack().push(operands.bytes);
        }

        @Override
        public String toString() {
            return "PUSH";
        }
    }

    public static class Pop implements Operation<NoOperands> {
        public void execute(ProgramContext context, NoOperands operands) {
            context.getStack().pop();
        }

        @Override
        public String toString() {
            return "POP";
        }
    }

    public static class Swap implements Operation<Swap.Operands> {
        public static class Operands implements Operations.Operands {
            public int topOffsetLeft;
            public int topOffsetRight;

            @Override
            public String toString() {
                return String.format("%d, %d", topOffsetLeft, topOffsetRight);
            }
        }

        public void execute(ProgramContext context, Operands operands) {
            context.getStack().swap(operands.topOffsetLeft, operands.topOffsetRight);
        }

        @Override
        public String toString() {
            return "POP";
        }
    }

    public static class Duplicate implements Operation<Duplicate.Operands> {
        public static class Operands implements Operations.Operands {
            public int topOffset;

            @Override
            public String toString() {
                return Integer.toString(topOffset);
            }
        }

        public void execute(ProgramContext context, Operands operands) {
            context.getStack().duplicate(operands.topOffset);
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

    private static abstract class ConditionOperation implements Operation<NoOperands> {
        public void execute(ProgramContext context, NoOperands operands) {
            LayeredStack<byte[]> stack = context.getStack();
            byte[] left = stack.pop();
            byte[] right = stack.pop();
            boolean equals = innerExecute(left, right);
            stack.push(equals ? TRUE.clone() : FALSE.clone());
        }

        public abstract boolean innerExecute(byte[] left, byte[] right);
    }

    public static class Equals extends ConditionOperation {
        @Override
        public boolean innerExecute(byte[] left, byte[] right) {
            return Arrays.equals(left, right);
        }


        @Override
        public String toString() {
            return "EQUALS";
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
        public String toString() {
            return "GREATER_THAN";
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
        public String toString() {
            return "LESS_THAN";
        }
    }

    public static class IsZero implements Operation<NoOperands> {
        public void execute(ProgramContext context, NoOperands operands) {
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

    private static abstract class BitwiseOperation implements Operation<NoOperands> {

        public void execute(ProgramContext context, NoOperands operands) {
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

    public static class BitwiseOr extends BitwiseOperation {
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

    public static class BitwiseXor extends BitwiseOperation {
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

    public static class BitwiseAnd extends BitwiseOperation {
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

    public static class BitwiseNot implements Operation<NoOperands> {
        @Override
        public void execute(ProgramContext context, NoOperands operands) {
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

    private static abstract class MathematicalOperation implements Operation<NoOperands> {

        public void execute(ProgramContext context, NoOperands operands) {
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

    public static class Add extends MathematicalOperation {
        @Override
        public BigInteger innerExecute(BigInteger left, BigInteger right) {
            return left.add(right);
        }

        @Override
        public String toString() {
            return "ADD";
        }
    }

    public static class Subtract extends MathematicalOperation {
        @Override
        public BigInteger innerExecute(BigInteger left, BigInteger right) {
            return left.subtract(right);
        }

        @Override
        public String toString() {
            return "SUBTRACT";
        }
    }

    public static class Multiply extends MathematicalOperation {
        @Override
        public BigInteger innerExecute(BigInteger left, BigInteger right) {
            return left.multiply(right);
        }

        @Override
        public String toString() {
            return "MULTIPLY";
        }
    }

    public static class Divide extends MathematicalOperation {
        @Override
        public BigInteger innerExecute(BigInteger left, BigInteger right) {
            return left.divide(right);
        }

        @Override
        public String toString() {
            return "DIVIDE";
        }
    }

    public static class Modulo extends MathematicalOperation {
        @Override
        public BigInteger innerExecute(BigInteger left, BigInteger right) {
            return left.mod(right);
        }

        @Override
        public String toString() {
            return "MODULO";
        }
    }

    public static class Hash implements Operation<Hash.Operands> {

        public static final String HASH_METHOD_SHA_3 = "SHA3";
        public static final String HASH_METHOD_NONE = "NONE";

        public static class Operands implements Operations.Operands {
            public String hashMethod;

            @Override
            public String toString() {
                return hashMethod;
            }
        }

        public void execute(ProgramContext context, Operands operands) {
            if (HASH_METHOD_SHA_3.equals(operands.hashMethod)) {
                throw new UnsupportedOperationException(String.format("Unimplemented hash method: %s", operands.hashMethod));
            } else if (!HASH_METHOD_NONE.equals(operands.hashMethod)) {
                throw new IllegalArgumentException(String.format("Unknown hash method: %s", operands.hashMethod));
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

    public static class Jump implements Operation<NoOperands> {
        public void execute(ProgramContext context, NoOperands noOperands) {
            byte[] destination = context.getStack().pop();
            context.setJumping(true);
            context.setInstructionPosition(new BigInteger(destination).intValue());
        }

        @Override
        public String toString() {
            return "JUMP";
        }
    }

    public static class JumpDestination implements Operation<NoOperands> {
        public void execute(ProgramContext context, NoOperands operands) {
            context.setJumping(false);
        }

        @Override
        public String toString() {
            return "JUMP_DESTINATION";
        }
    }

    public static class JumpIf implements Operation<NoOperands> {
        public void execute(ProgramContext context, NoOperands noOperands) {
            LayeredStack<byte[]> stack = context.getStack();
            byte[] destination = context.getStack().pop();
            byte[] top = stack.pop();
            boolean isZero = true;
            for (byte item : top) {
                if (item > 0) {
                    isZero = false;
                }
            }
            if (!isZero) {
                context.setJumping(true);
                context.setInstructionPosition(new BigInteger(destination).intValue());
            }
        }

        @Override
        public String toString() {
            return "JUMP_IF";
        }
    }

    public static class Exit implements Operation<NoOperands> {
        public void execute(ProgramContext context, NoOperands operands) {
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

    public static class Load implements Operation<Load.Operands> {
        public static class Operands implements Operations.Operands {
            public String group;

            @Override
            public String toString() {
                return String.format("%s", group);
            }
        }

        public void execute(ProgramContext context, Operands operands) {
            byte[] addressBytes = context.getStack().pop();
            BigInteger address = new BigInteger(addressBytes);
            ProgramData programData = context.getData(operands.group, address);
            if (programData == null) {
                throw new IllegalStateException(String.format("Loading empty data at %s:%s", operands.group, address.toString()));
            }
            context.getStack().push(programData.content);
        }

        @Override
        public String toString() {
            return "LOAD";
        }
    }

    public static class Save implements Operation<Save.Operands> {
        public static class Operands implements Operations.Operands {
            public String group;

            @Override
            public String toString() {
                return String.format("%s", group);
            }
        }

        public void execute(ProgramContext context, Operands operands) {
            LayeredStack<byte[]> stack = context.getStack();
            byte[] addressBytes = context.getStack().pop();
            BigInteger address = new BigInteger(addressBytes);
            byte[] bytes = stack.pop();
            context.setData(operands.group, address, new ProgramData(bytes));
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

    public static class NoOp implements Operation<NoOperands> {
        public void execute(ProgramContext context, NoOperands operands) {
        }

        @Override
        public String toString() {
            return "NOOP";
        }
    }

}
