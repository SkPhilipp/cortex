package com.hileco.cortex.constraints;

import lombok.Data;

import java.util.Objects;

@Data
public class Expression {
    private Type type;
    private Expression left;
    private Expression right;
    private Reference reference;
    private Long constant;

    private Expression(Type type, Expression left, Expression right, Reference reference, Long constant) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.reference = reference;
        this.constant = constant;
    }

    public static Expression operation(Type type, Expression left, Expression right) {
        return new Expression(type, left, right, null, null);
    }

    public static Expression reference(Reference reference) {
        return new Expression(Type.REFERENCE, null, null, reference, null);
    }

    public static Expression value(Long constant) {
        return new Expression(Type.VALUE, null, null, null, constant);
    }

    @Override
    public String toString() {
        switch (this.type) {
            case ADD:
            case SUBTRACT:
            case MULTIPLY:
            case DIVIDE:
            case LESS_THAN:
            case GREATER_THAN:
            case EQUAL_TO:
            case NOT_EQUAL_TO:
            case OR:
            case AND:
            case MODULO:
                return String.format("(%s %s %s)", this.left, this.type, this.right);
            case HASH:
                return String.format("%s(%s)", this.type, this.left);
            case REFERENCE:
                return Objects.toString(this.reference);
            case VALUE:
                return Objects.toString(this.constant);
            default:
                throw new IllegalStateException();
        }
    }

    public enum Type {
        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY("*"),
        DIVIDE("/"),
        LESS_THAN("<"),
        GREATER_THAN(">"),
        EQUAL_TO("=="),
        NOT_EQUAL_TO("!="),
        OR("||"),
        AND("&&"),
        MODULO("%"),
        HASH("HASH"),
        REFERENCE("REFERENCE"),
        VALUE("VALUE");

        private final String representation;

        Type(String representation) {
            this.representation = representation;
        }

        @Override
        public String toString() {
            return this.representation;
        }
    }
}
